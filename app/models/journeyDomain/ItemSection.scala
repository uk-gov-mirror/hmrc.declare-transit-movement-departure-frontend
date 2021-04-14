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
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.ItemsSecurityTraderDetails.SecurityTraderDetails
import models.{Index, UserAnswers}
import pages.addItems.specialMentions.AddSpecialMentionPage
import pages.safetyAndSecurity.AddSafetyAndSecurityConsignorPage
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
    DeriveNumberOfPackages(itemIndex).reader.flatMap {
      case list if list.nonEmpty =>
        list.zipWithIndex
          .traverse[UserAnswersReader, Packages]({
            case (_, index) =>
              Packages.packagesReader(itemIndex, Index(index))
          })
          .map(NonEmptyList.fromListUnsafe)
      case _ => UserAnswersReader.failed[NonEmptyList[Packages]]
    }

  private def deriveContainers(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[Container]]] =
    ContainersUsedPage.reader
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfContainers(itemIndex).reader.flatMap {
              case list if list.nonEmpty =>
                list.zipWithIndex
                  .traverse[UserAnswersReader, Container]({
                    case (_, index) =>
                      Container.containerReader(itemIndex, Index(index))
                  })
                  .map(NonEmptyList.fromList)
              case _ => UserAnswersReader.failed[Option[NonEmptyList[Container]]]
            }
          } else none[NonEmptyList[Container]].pure[UserAnswersReader]
      }

  private def deriveSpecialMentions(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[SpecialMention]]] =
    AddSpecialMentionPage(itemIndex).reader
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfSpecialMentions(itemIndex).reader.flatMap {
              case list if list.nonEmpty =>
                list.zipWithIndex
                  .traverse[UserAnswersReader, SpecialMention]({
                    case (_, index) =>
                      SpecialMention.specialMentionsReader(itemIndex, Index(index))
                  })
                  .map(NonEmptyList.fromList)
              case _ => UserAnswersReader.failed[Option[NonEmptyList[SpecialMention]]]
            }
          } else none[NonEmptyList[SpecialMention]].pure[UserAnswersReader]
      }

  private def securityItemsSecurityTraderDetails(itemIndex: Index): UserAnswersReader[Option[ItemsSecurityTraderDetails]] =
    AddSecurityDetailsPage.reader
      .flatMap {
        case true  => ItemsSecurityTraderDetails.parser(itemIndex).map(_.some)
        case false => none[ItemsSecurityTraderDetails].pure[UserAnswersReader]
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
    DeriveNumberOfItems.reader.flatMap {
      case list if list.nonEmpty =>
        list.zipWithIndex
          .traverse[UserAnswersReader, ItemSection]({
            case (_, index) =>
              readerItemSection(Index(index))
          })
          .map(NonEmptyList.fromListUnsafe)
      case _ => UserAnswersReader.failed[NonEmptyList[ItemSection]]
    }
}
