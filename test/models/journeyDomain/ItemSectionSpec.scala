/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.journeyDomain

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import cats.data.NonEmptyList
import generators.JourneyModelGenerators
import models.journeyDomain.PackagesSpec.UserAnswersNoErrorSet
import models.reference.CircumstanceIndicator
import models.{Index, UserAnswers}
import pages.addItems.AddDocumentsPage
import pages.addItems.specialMentions.AddSpecialMentionPage
import pages.safetyAndSecurity._
import pages.{AddConsigneePage, AddConsignorPage, AddSecurityDetailsPage, ConsigneeForAllItemsPage, ConsignorForAllItemsPage, ContainersUsedPage}

class ItemSectionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  "ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(genItemSectionOld(), arb[UserAnswers]) {
          case (itemSection, userAnswers) =>
            val indicator = CircumstanceIndicator.conditionalIndicators.head

            val updatedUserAnswer = {
              itemSection.itemSecurityTraderDetails match {
                case Some(value) =>
                  userAnswers
                    .unsafeSetVal(AddTransportChargesPaymentMethodPage)(value.methodOfPayment.isEmpty)
                    .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(value.commercialReferenceNumber.isEmpty)

                case None => userAnswers
              }
            }.unsafeSetVal(AddSecurityDetailsPage)(itemSection.producedDocuments.isDefined)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(itemSection.producedDocuments.isDefined)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(itemSection.producedDocuments.isDefined)
              .unsafeSetVal(AddDocumentsPage(itemIndex))(itemSection.producedDocuments.isDefined)
              .unsafeSetVal(CircumstanceIndicatorPage)(indicator)
              .unsafeSetVal(ConsignorForAllItemsPage)(false)
              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(ConsigneeForAllItemsPage)(false)
              .unsafeSetVal(AddConsigneePage)(false)

            val setSectionUserAnswers = ItemSectionSpec.setItemSection(itemSection, index)(updatedUserAnswer)

            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(setSectionUserAnswers)

            result.value.itemDetails mustEqual itemSection.itemDetails
            result.value.consignor mustEqual itemSection.consignor
            result.value.consignee mustEqual itemSection.consignee
            result.value.packages mustEqual itemSection.packages
            result.value.containers mustEqual itemSection.containers
            result.value.specialMentions mustEqual itemSection.specialMentions
            result.value.producedDocuments mustEqual itemSection.producedDocuments
            result.value.itemSecurityTraderDetails mustEqual itemSection.itemSecurityTraderDetails
        }
      }
    }

    "cannot be parsed" - {
      "when an answer is missing" in {
        forAll(arb[ItemSection], arb[UserAnswers]) {
          case (itemSection, ua) =>
            val userAnswers                 = ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index)(ua)
            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(userAnswers)

            result mustBe None
        }
      }
    }
  }

  "Seq of ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(genItemSectionOld(), arb[UserAnswers]) {
          case (itemSections, userAnswers) =>
            val indicator = CircumstanceIndicator.conditionalIndicators.head

            val updatedUserAnswer = {
              itemSections.itemSecurityTraderDetails match {
                case Some(value) =>
                  userAnswers
                    .unsafeSetVal(AddTransportChargesPaymentMethodPage)(value.methodOfPayment.isEmpty)
                    .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(value.commercialReferenceNumber.isEmpty)
                case None => userAnswers
              }
            }.unsafeSetVal(AddSecurityDetailsPage)(itemSections.producedDocuments.isDefined)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(itemSections.producedDocuments.isDefined)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(itemSections.producedDocuments.isDefined)
              .unsafeSetVal(CircumstanceIndicatorPage)(indicator)
              .unsafeSetVal(ConsignorForAllItemsPage)(false)
              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(ConsigneeForAllItemsPage)(false)
              .unsafeSetVal(AddConsigneePage)(false)

            val setItemSections = ItemSectionSpec.setItemSections(Seq(itemSections, itemSections).toList)(updatedUserAnswer)

            val result = ItemSection.readerItemSections.run(setItemSections)

            result.value mustEqual NonEmptyList(itemSections, List(itemSections))
        }
      }
    }
  }
}

object ItemSectionSpec extends UserAnswersSpecHelper {

  private def setPackages(packages: NonEmptyList[Packages], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers =
    packages.zipWithIndex.foldLeft(startUserAnswers) {
      case (userAnswers, (pckge, index)) => PackagesSpec.setPackageUserAnswers(pckge, itemIndex, Index(index))(userAnswers)
    }

  def setItemSections(itemSections: Seq[ItemSection])(startUserAnswers: UserAnswers): UserAnswers = {

    val methodOfPayments: Seq[Option[Option[String]]]     = itemSections.map(_.itemSecurityTraderDetails.map(_.methodOfPayment))
    val commercialReferences: Seq[Option[Option[String]]] = itemSections.map(_.itemSecurityTraderDetails.map(_.commercialReferenceNumber))

    require(sequenceIsConsistent(methodOfPayments), "Method of payment contained a false")
    require(sequenceIsConsistent(commercialReferences), "Commercial reference contained a false")

    itemSections.zipWithIndex.foldLeft(startUserAnswers) {
      case (ua, (section, i)) =>
        val updatedUserAnswer = ua.unsafeSetVal(AddDocumentsPage(Index(i)))(section.producedDocuments.isDefined)
        ItemSectionSpec.setItemSection(section, Index(i))(updatedUserAnswer)
    }
  }

  def sequenceIsConsistent[A](xs: Seq[Option[Option[A]]]): Boolean = {

    val topLevelNotDefined: Boolean = xs.forall(_.isEmpty)
    val allDefined: Boolean         = xs.flatten.forall(_.isDefined)
    val allUndefined: Boolean       = xs.flatten.forall(_.isEmpty)

    topLevelNotDefined | allDefined | allUndefined
  }

  private def setContainers(containers: Option[NonEmptyList[Container]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val ua = startUserAnswers.unsafeSetVal(ContainersUsedPage)(containers.isDefined)
    containers match {
      case Some(containers) => ContainerSpec.setContainers(containers.toList, itemIndex)(startUserAnswers)
      case None             => ua
    }
  }

  private def setSpecialMentions(specialMentions: Option[NonEmptyList[SpecialMention]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val smUserAnswers = startUserAnswers.set(AddSpecialMentionPage(itemIndex), false).toOption.get
    specialMentions.fold(smUserAnswers)(_.zipWithIndex.foldLeft(smUserAnswers) {
      case (userAnswers, (specialMention, index)) =>
        SpecialMentionSpec.setSpecialMentionsUserAnswers(specialMention, itemIndex, Index(index))(userAnswers)
    })
  }

  private def setProducedDocuments(producedDocument: Option[NonEmptyList[ProducedDocument]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers =
    producedDocument.fold(startUserAnswers)(_.zipWithIndex.foldLeft(startUserAnswers) {
      case (userAnswers, (producedDocument, index)) =>
        ProducedDocumentSpec.setProducedDocumentsUserAnswers(producedDocument, itemIndex, Index(index))(userAnswers)
    })

  def setItemsSecurityTraderDetails(itemsSecurityTraderDetails: Option[ItemsSecurityTraderDetails], index: Index)(userAnswers: UserAnswers): UserAnswers =
    itemsSecurityTraderDetails match {
      case Some(result) => ItemsSecurityTraderDetailsSpec.setItemsSecurityTraderDetails(result, index)(userAnswers)
      case None         => userAnswers
    }

  def setItemSection(itemSection: ItemSection, itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers =
    (
      ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, itemIndex) _ andThen
        ItemTraderDetailsSpec.setItemTraderDetails(ItemTraderDetails(itemSection.consignor, itemSection.consignee), itemIndex) andThen
        setPackages(itemSection.packages, itemIndex) andThen
        setContainers(itemSection.containers, itemIndex) andThen
        setSpecialMentions(itemSection.specialMentions, itemIndex) andThen
        setProducedDocuments(itemSection.producedDocuments, itemIndex) andThen
        setItemsSecurityTraderDetails(itemSection.itemSecurityTraderDetails, itemIndex)
    )(startUserAnswers)
}
