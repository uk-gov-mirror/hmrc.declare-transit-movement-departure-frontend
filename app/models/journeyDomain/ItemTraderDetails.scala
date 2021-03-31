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
import models.domain.Address
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.{EoriNumber, Index, UserAnswers}
import pages.addItems.traderDetails._
import pages._

case class ItemTraderDetails(consignor: Option[RequiredDetails], consignee: Option[RequiredDetails])

object ItemTraderDetails {

  final case class RequiredDetails(name: String, address: Address, eori: Option[EoriNumber])

  def consignorDetails(index: Index): UserAnswersReader[Option[RequiredDetails]] = {
    def readConsignorEoriPage: UserAnswersReader[Option[EoriNumber]] =
      TraderDetailsConsignorEoriKnownPage(index).reader
        .flatMap {
          bool =>
            if (bool)
              TraderDetailsConsignorEoriNumberPage(index).reader.map(
                eori => Some(EoriNumber(eori))
              )
            else none[EoriNumber].pure[UserAnswersReader]
        }

    val consignorInformation: ReaderT[Option, UserAnswers, RequiredDetails] =
      (
        TraderDetailsConsignorNamePage(index).reader,
        TraderDetailsConsignorAddressPage(index).reader,
        readConsignorEoriPage
      ).tupled
        .map {
          case (name, consignorAddress, eori) =>
            val address = Address.prismAddressToConsignorAddress(consignorAddress)
            RequiredDetails(name, address, eori)
        }

    (
      UserAnswersReader.unsafeEmpty[Boolean], // TODO - should be AddConsignorPage.reader
      AddConsignorPage.reader
    ).tupled.flatMap {
      case (consignorForAllItems, addConsignor) if !consignorForAllItems | !addConsignor => consignorInformation
    }.lower
  }

  def consigneeDetails(index: Index): UserAnswersReader[Option[RequiredDetails]] = {

    def readConsigneeEoriPage: UserAnswersReader[Option[EoriNumber]] =
      TraderDetailsConsigneeEoriKnownPage(index).reader
        .flatMap {
          bool =>
            if (bool)
              TraderDetailsConsigneeEoriNumberPage(index).reader.map(
                eori => Some(EoriNumber(eori))
              )
            else none[EoriNumber].pure[UserAnswersReader]
        }

    val consigneeInformation: ReaderT[Option, UserAnswers, RequiredDetails] =
      (
        TraderDetailsConsigneeNamePage(index).reader,
        TraderDetailsConsigneeAddressPage(index).reader,
        readConsigneeEoriPage
      ).tupled
        .map {
          case (name, consigneeAddress, eori) =>
            val address = Address.prismAddressToConsigneeAddress(consigneeAddress)
            RequiredDetails(name, address, eori)
        }

    (
      UserAnswersReader.unsafeEmpty[Boolean], // TODO - should be AddConsigneePage.reader
      AddConsigneePage.reader
    ).tupled.flatMap {
      //TODO this can throw matcher error, need amending
      case (consigneeForAllItems, addConsignee) if !consigneeForAllItems | !addConsignee => consigneeInformation
    }.lower
  }

  def userAnswersParser(index: Index): UserAnswersParser[Option, ItemTraderDetails] =
    UserAnswersOptionalParser((consignorDetails(index), consigneeDetails(index)).tupled)(
      x => ItemTraderDetails(x._1, x._2)
    )
}
