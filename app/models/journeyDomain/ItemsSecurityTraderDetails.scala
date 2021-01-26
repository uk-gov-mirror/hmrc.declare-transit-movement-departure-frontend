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

import cats.implicits._
import models.domain.Address
import models.journeyDomain.ItemsSecurityTraderDetails.SecurityTraderDetails
import models.{EoriNumber, Index}
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity._

final case class ItemsSecurityTraderDetails(
  consignor: Option[SecurityTraderDetails],
  consignee: Option[SecurityTraderDetails]
)

object ItemsSecurityTraderDetails {

  def parser(index: Index): UserAnswersParser[Option, ItemsSecurityTraderDetails] =
    UserAnswersOptionalParser(
      (
        consignorDetails(index),
        consigneeDetails(index)
      ).tupled
    )((ItemsSecurityTraderDetails.apply _).tupled)

  sealed trait SecurityTraderDetails

  object SecurityTraderDetails {
    def apply(eori: EoriNumber): SecurityTraderDetails = SecurityTraderEori(eori)

    def apply(name: String, address: Address): SecurityTraderDetails = SecurityPersonalInformation(name, address)
  }

  final case class SecurityPersonalInformation(name: String, address: Address) extends SecurityTraderDetails

  final case class SecurityTraderEori(eori: EoriNumber) extends SecurityTraderDetails

  private def consignorDetails(index: Index): UserAnswersReader[Option[SecurityTraderDetails]] = {

    val useEori =
      SecurityConsignorEoriPage(index).reader.map(
        eori => SecurityTraderDetails(EoriNumber(eori))
      )

    val useNameAndAddress =
      (
        SecurityConsignorNamePage(index).reader,
        SecurityConsignorAddressPage(index).reader
      ).tupled
        .map {
          case (name, consignorAddress) =>
            val address = Address.prismAddressToConsignorAddress(consignorAddress)
            SecurityTraderDetails(name, address)
        }

    val isEoriKnown =
      AddSecurityConsignorsEoriPage(index).reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useNameAndAddress
      )

    AddSafetyAndSecurityConsignorPage.reader //TODO - add a matcher
    .flatMap {
      _ =>
        isEoriKnown
    }.lower
  }

  private def consigneeDetails(index: Index): UserAnswersReader[Option[SecurityTraderDetails]] = {

    val useEori = SecurityConsigneeEoriPage(index).reader.map(
      eori => SecurityTraderDetails(EoriNumber(eori))
    )

    val useNameAndAddress =
      (
        SecurityConsigneeNamePage(index).reader,
        SecurityConsigneeAddressPage(index).reader
      ).tupled
        .map {
          case (name, consigneeAddress) =>
            val address = Address.prismAddressToConsigneeAddress(consigneeAddress)
            SecurityTraderDetails(name, address)
        }

    val isEoriKnown = AddSecurityConsigneesEoriPage(index).reader.flatMap(
      isEoriKnown => if (isEoriKnown) useEori else useNameAndAddress
    )

    AddSafetyAndSecurityConsigneePage.reader.flatMap {
      _ =>
        isEoriKnown
    }.lower

  }
}
