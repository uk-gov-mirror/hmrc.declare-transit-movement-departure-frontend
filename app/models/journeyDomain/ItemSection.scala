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

import cats.data._
import cats.implicits._
import derivable._
import models.Index
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import pages.addItems.specialMentions.AddSpecialMentionPage
import pages.{AddSecurityDetailsPage, ContainersUsedPage}

case class ItemSection(
  itemDetails: ItemDetails,
  consignor: Option[RequiredDetails],
  consignee: Option[RequiredDetails],
  packages: NonEmptyList[Packages],
  containers: Option[NonEmptyList[Container]],
  specialMentions: Option[NonEmptyList[SpecialMention]],
  producedDocuments: Option[NonEmptyList[ProducedDocument]],
  itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails],
  previousReferences: Option[NonEmptyList[PreviousReferences]]
)

object ItemSection {

  private def derivePackage(itemIndex: Index): UserAnswersReader[NonEmptyList[Packages]] =
    DeriveNumberOfPackages(itemIndex).mandatoryNonEmptyListReader.flatMap {
      _.zipWithIndex
        .traverse[UserAnswersReader, Packages]({
          case (_, index) =>
            Packages.packagesReader(itemIndex, Index(index))
        })
    }

  private def deriveContainers(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[Container]]] =
    ContainersUsedPage.filterOptionalDependent(identity) {
      DeriveNumberOfContainers(itemIndex).mandatoryNonEmptyListReader.flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, Container]({
            case (_, index) =>
              Container.containerReader(itemIndex, Index(index))
          })
      }
    }

  private def deriveSpecialMentions(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[SpecialMention]]] =
    AddSpecialMentionPage(itemIndex).filterOptionalDependent(identity) {
      DeriveNumberOfSpecialMentions(itemIndex).mandatoryNonEmptyListReader.flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, SpecialMention]({
            case (_, index) =>
              SpecialMention.parseSpecialMention(itemIndex, Index(index))
          })
      }
    }

  private def securityItemsSecurityTraderDetails(itemIndex: Index): UserAnswersReader[Option[ItemsSecurityTraderDetails]] =
    AddSecurityDetailsPage.filterOptionalDependent(identity) {
      ItemsSecurityTraderDetails.parser(itemIndex)
    }

  implicit def readerItemSection(index: Index): UserAnswersReader[ItemSection] =
    (
      ItemDetails.itemDetailsReader(index),
      ItemTraderDetails.consignorDetails(index),
      ItemTraderDetails.consigneeDetails(index),
      derivePackage(index),
      deriveContainers(index),
      deriveSpecialMentions(index),
      ProducedDocument.deriveProducedDocuments(index),
      securityItemsSecurityTraderDetails(index),
      PreviousReferences.derivePreviousReferences(index)
    ).tupled.map((ItemSection.apply _).tupled)

  implicit def readerItemSections: UserAnswersReader[NonEmptyList[ItemSection]] =
    DeriveNumberOfItems.mandatoryNonEmptyListReader.flatMap {
      _.zipWithIndex
        .traverse[UserAnswersReader, ItemSection]({
          case (_, index) =>
            readerItemSection(Index(index))
        })
    }
}
