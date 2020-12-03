/*
 * Copyright 2020 HM Revenue & Customs
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
import models.journeyDomain.Packages.UnpackedPackages
import models.reference.{CircumstanceIndicator, PackageType}
import models.{EoriNumber, Index, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages.{AddSecurityDetailsPage, ContainersUsedPage}
import pages.addItems.AddDocumentsPage
import pages.addItems.specialMentions.AddSpecialMentionPage
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, CircumstanceIndicatorPage}

class ItemSectionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  "ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(genItemSectionOld(), arb[UserAnswers]) {
          case (itemSection, userAnswers) =>
            val updatedUserAnswer           = ItemSectionSpec.setItemSection(itemSection, index)(userAnswers)
            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(updatedUserAnswer)

            result.value mustEqual itemSection
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

      "this value fails" in {

        val woa = NonEmptyList(
          ItemSection(
            ItemDetails("asdf", "-2147483648", Some("2147483647"), Some("㢨諹")),
            Some(ItemTraderDetails.RequiredDetails(EoriNumber("asdfa"))),
            Some(ItemTraderDetails.RequiredDetails(EoriNumber("asefas"))),
            NonEmptyList(UnpackedPackages(PackageType("asdf", "aasd"), None, 5, Some("")),
                         List(UnpackedPackages(PackageType("asdf", "aasd"), Some(3), 2, Some("إ쫯ㄯ栢린놱೗꓎")))),
            Some(NonEmptyList(Container("⚉獍욀妙伥㷀墲捺純ꢖ퀢껸玄楧簲삮鄯ᛰㇽฝ፤"), List.empty)),
            Some(NonEmptyList(SpecialMention("asdf", "asdf"), List(SpecialMention("asdf", "asdfasd")))),
            None
          ),
          List.empty
        )

        val updatedUserAnswer = ItemSectionSpec.setItemSections(woa.toList)(emptyUserAnswers)

        val result = ItemSection.readerItemSections.run(updatedUserAnswer)

        result.value mustEqual woa
      }
    }
  }

  "Seq of ItemSection" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(nonEmptyListOf[ItemSection](3)(Arbitrary(genItemSectionOld())), arb[UserAnswers]) {
          case (itemSections, userAnswers) =>
            val updatedUserAnswer = ItemSectionSpec.setItemSections(itemSections.toList)(userAnswers)
            val result            = ItemSection.readerItemSections.run(updatedUserAnswer)

            result.value mustEqual itemSections
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

  def setItemSections(itemSections: Seq[ItemSection])(startUserAnswers: UserAnswers): UserAnswers =
    itemSections.zipWithIndex.foldLeft(startUserAnswers) {
      case (ua, (section, i)) =>
        ItemSectionSpec.setItemSection(section, Index(i))(ua)
    }

  private def setContainers(containers: Option[NonEmptyList[Container]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val ua = startUserAnswers.unsafeSetVal(ContainersUsedPage)(containers.isDefined)
    containers match {
      case Some(containers) => ContainerSpec.setContainers(containers.toList, itemIndex)(ua)
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

  private def setProducedDocuments(producedDocument: Option[NonEmptyList[ProducedDocument]], itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val smUserAnswers = startUserAnswers
      .unsafeSetVal(AddSecurityDetailsPage)(producedDocument.isDefined)
      .unsafeSetVal(AddCircumstanceIndicatorPage)(producedDocument.isDefined)
      .unsafeSetVal(AddDocumentsPage(itemIndex))(producedDocument.isDefined)

    val ua = if (producedDocument.isDefined) {
      smUserAnswers.unsafeSetVal(CircumstanceIndicatorPage)(CircumstanceIndicator.conditionalIndicators.head)
    } else {
      smUserAnswers
    }

    producedDocument.fold(ua)(_.zipWithIndex.foldLeft(ua) {
      case (ua, (producedDocument, index)) =>
        ProducedDocumentSpec.setProducedDocumentsUserAnswers(producedDocument, itemIndex, Index(index))(ua)
    })
  }

  def setItemSection(itemSection: ItemSection, itemIndex: Index)(startUserAnswers: UserAnswers): UserAnswers =
    (
      ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, itemIndex) _ andThen
        ItemTraderDetailsSpec.setItemTraderDetails(ItemTraderDetails(itemSection.consignor, itemSection.consignee), itemIndex) andThen
        setPackages(itemSection.packages, itemIndex) andThen
        setContainers(itemSection.containers, itemIndex) andThen
        setSpecialMentions(itemSection.specialMentions, itemIndex) andThen
        setProducedDocuments(itemSection.producedDocuments, itemIndex)
    )(startUserAnswers)

}
