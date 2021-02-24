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
import models.{Index, UserAnswers}
import pages.{AddSecurityDetailsPage, ContainersUsedPage}
import pages.addItems.specialMentions.AddSpecialMentionPage

case class ItemSection(
  itemDetails: ItemDetails,
  consignor: Option[RequiredDetails],
  consignee: Option[RequiredDetails],
  packages: NonEmptyList[Packages],
  containers: Option[NonEmptyList[Container]],
  specialMentions: Option[NonEmptyList[SpecialMention]],
  producedDocuments: Option[NonEmptyList[ProducedDocument]],
  itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails]
)

object ItemSection {

  private def derivePackage(itemIndex: Index): ReaderT[Option, UserAnswers, NonEmptyList[Packages]] =
    DeriveNumberOfPackages(itemIndex).reader
      .flatMap {
        y =>
          println(s"**** $y ****")
          y.zipWithIndex
            .traverse[UserAnswersReader, Packages]({
              case (value, index) =>
                println(s"**** Loop: $index $value ****")
                Packages.packagesReader(itemIndex, Index(index))
            })
            .map(NonEmptyList.fromListUnsafe)
      }

  private def deriveContainers(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[Container]]] =
    ContainersUsedPage.reader
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfContainers(itemIndex).reader
              .filter(_.nonEmpty)
              .flatMap {
                _.zipWithIndex
                  .traverse[UserAnswersReader, Container]({
                    case (_, index) =>
                      Container.containerReader(itemIndex, Index(index))
                  })
                  .map(NonEmptyList.fromList)
              }
          } else none[NonEmptyList[Container]].pure[UserAnswersReader]
      }

  private def deriveSpecialMentions(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[SpecialMention]]] =
    AddSpecialMentionPage(itemIndex).reader
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfSpecialMentions(itemIndex).reader
              .filter(_.nonEmpty)
              .flatMap {
                _.zipWithIndex
                  .traverse[UserAnswersReader, SpecialMention]({
                    case (_, index) =>
                      SpecialMention.specialMentionsReader(itemIndex, Index(index))
                  })
                  .map(NonEmptyList.fromList)
              }
          } else none[NonEmptyList[SpecialMention]].pure[UserAnswersReader]
      }

  private def securityItemsSecurityTraderDetails(itemIndex: Index): ReaderT[Option, UserAnswers, Option[ItemsSecurityTraderDetails]] =
    AddSecurityDetailsPage.reader
      .flatMap(
        _ => ItemsSecurityTraderDetails.parser(itemIndex)
      )
      .lower

  implicit def readerItemSection(index: Index): ReaderT[Option, UserAnswers, ItemSection] =
    for {
      a <- {
        println(s"\n\n GOT HERE 1 \n\n")
        ItemDetails.itemDetailsReader(index)
      }
      b <- {
        println(s"\n\n GOT HERE 2 \n\n")
        ItemTraderDetails.consignorDetails(index)
      }
      c <- {
        println(s"\n\n GOT HERE 3 \n\n")
        ItemTraderDetails.consigneeDetails(index)
      }
      d <- {
        println(s"\n\n GOT HERE 4 \n\n")
        derivePackage(index)
      }
      e <- {
        println(s"\n\n GOT HERE 5 \n\n")
        deriveContainers(index)
      }
      f <- {
        println(s"\n\n GOT HERE 6 \n\n")
        deriveSpecialMentions(index)
      }
      g <- {
        println(s"\n\n GOT HERE 7 \n\n")
        ProducedDocument.deriveProducedDocuments(index)
      }
      h <- {
        println(s"\n\n GOT HERE 8 \n\n")
        securityItemsSecurityTraderDetails(index)
      }
    } yield ItemSection(a, b, c, d, e, f, g, h)

  implicit def readerItemSections: UserAnswersReader[NonEmptyList[ItemSection]] =
    DeriveNumberOfItems.reader
      .filter(_.nonEmpty)
      .flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, ItemSection]({
            case (_, index) =>
              readerItemSection(Index(index))
          })
          .map(NonEmptyList.fromListUnsafe)
      }

}
