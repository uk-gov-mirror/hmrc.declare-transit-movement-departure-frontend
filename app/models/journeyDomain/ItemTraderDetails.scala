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

import cats.data._
import cats.implicits._
import models.domain.Address
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.{EoriNumber, Index, UserAnswers}
import pages.addItems.traderDetails._
import pages._

case class ItemTraderDetails(consignor: Option[RequiredDetails], consignee: Option[RequiredDetails])

object ItemTraderDetails {

  sealed trait RequiredDetails

  object RequiredDetails {
    def apply(eori: EoriNumber): RequiredDetails               = TraderEori(eori)
    def apply(name: String, address: Address): RequiredDetails = PersonalInformation(name, address)
  }

  final case class PersonalInformation(name: String, address: Address) extends RequiredDetails
  final case class TraderEori(eori: EoriNumber) extends RequiredDetails

  def consignorDetails(index: Index): UserAnswersReader[Option[RequiredDetails]] = {
    val useEori: ReaderT[Option, UserAnswers, RequiredDetails] =
      TraderDetailsConsignorEoriNumberPage(index).reader.map(
        eori => RequiredDetails(EoriNumber(eori))
      )

    val useAddress =
      (
        TraderDetailsConsignorNamePage(index).reader,
        TraderDetailsConsignorAddressPage(index).reader
      ).tupled
        .map {
          case (name, consignorAddress) =>
            val address = Address.prismAddressToConsignorAddress(consignorAddress)
            RequiredDetails(name, address)
        }

    val isEoriKnown: UserAnswersReader[RequiredDetails] =
      TraderDetailsConsignorEoriKnownPage(index).reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useAddress
      )

    (
      ConsignorForAllItemsPage.reader,
      AddConsignorPage.reader
    ).tupled.flatMap {
      case (consignorForAllItems, addConsignor) if !consignorForAllItems | !addConsignor => isEoriKnown
    }.lower
  }

  def consigneeDetails(index: Index): UserAnswersReader[Option[RequiredDetails]] = {
    val useEori: ReaderT[Option, UserAnswers, RequiredDetails] =
      TraderDetailsConsigneeEoriNumberPage(index).reader.map(
        eori => RequiredDetails(EoriNumber(eori))
      )

    val useAddress =
      (
        TraderDetailsConsigneeNamePage(index).reader,
        TraderDetailsConsigneeAddressPage(index).reader
      ).tupled
        .map {
          case (name, consignorAddress) =>
            val address = Address.prismAddressToConsigneeAddress(consignorAddress)
            RequiredDetails(name, address)
        }

    val isEoriKnown: UserAnswersReader[RequiredDetails] =
      TraderDetailsConsigneeEoriKnownPage(index).reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useAddress
      )

    (
      ConsigneeForAllItemsPage.reader,
      AddConsigneePage.reader
    ).tupled.flatMap {
      case (consigneeForAllItems, addConsignee) if !consigneeForAllItems | !addConsignee => isEoriKnown
    }.lower
  }

  def userAnswersParser(index: Index): UserAnswersParser[Option, ItemTraderDetails] =
    UserAnswersOptionalParser((consignorDetails(index), consigneeDetails(index)).tupled)(x => ItemTraderDetails(x._1, x._2))
}
