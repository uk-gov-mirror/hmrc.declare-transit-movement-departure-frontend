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
import models.{EoriNumber, UserAnswers}
import TraderDetails.RequiredDetails
import models.domain.Address
import pages.{
  AddConsigneePage,
  AddConsignorPage,
  ConsigneeAddressPage,
  ConsigneeNamePage,
  ConsignorAddressPage,
  ConsignorEoriPage,
  ConsignorNamePage,
  IsConsigneeEoriKnownPage,
  IsConsignorEoriKnownPage,
  IsPrincipalEoriKnownPage,
  PrincipalAddressPage,
  PrincipalNamePage,
  WhatIsConsigneeEoriPage,
  WhatIsPrincipalEoriPage
}

case class TraderDetails(
  principalTraderDetails: RequiredDetails,
  consignor: Option[RequiredDetails],
  consignee: Option[RequiredDetails]
)

object TraderDetails {

  sealed trait RequiredDetails

  object RequiredDetails {
    def apply(eori: EoriNumber): RequiredDetails               = TraderEori(eori)
    def apply(name: String, address: Address): RequiredDetails = PersonalInformation(name, address)
  }

  final case class PersonalInformation(name: String, address: Address) extends RequiredDetails
  final case class TraderEori(eori: EoriNumber) extends RequiredDetails

  val principalTraderDetails: UserAnswersReader[RequiredDetails] = {
    val useEori = WhatIsPrincipalEoriPage.reader
      .map(
        eori => RequiredDetails(EoriNumber(eori))
      )

    val useNameAndAddress: UserAnswersReader[RequiredDetails] = (
      PrincipalNamePage.reader,
      PrincipalAddressPage.reader
    ).tupled.map {
      case (name, principalAddress) =>
        val address = Address.prismAddressToPrincipalAddress(principalAddress)
        PersonalInformation(name, address)
    }

    IsPrincipalEoriKnownPage.reader.flatMap {
      isPrincipalEoriKnown =>
        if (isPrincipalEoriKnown) useEori else useNameAndAddress
    }
  }

  val consignorDetails: UserAnswersReader[Option[RequiredDetails]] = {
    val useEori: ReaderT[Option, UserAnswers, RequiredDetails] =
      ConsignorEoriPage.reader.map(
        eori => RequiredDetails(EoriNumber(eori))
      )

    val useAddress =
      (
        ConsignorNamePage.reader,
        ConsignorAddressPage.reader
      ).tupled
        .map {
          case (name, consignorAddress) =>
            val address = Address.prismAddressToConsignorAddress(consignorAddress)
            RequiredDetails(name, address)
        }

    val isEoriKnown: UserAnswersReader[RequiredDetails] =
      IsConsignorEoriKnownPage.reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useAddress
      )

    AddConsignorPage.reader
      .filter(identity)
      .flatMap(
        _ => isEoriKnown
      )
      .lower
  }

  val consigneeDetails: UserAnswersReader[Option[RequiredDetails]] = {
    val useEori =
      WhatIsConsigneeEoriPage.reader.map {
        eori =>
          RequiredDetails(EoriNumber(eori))
      }

    val useAddress =
      (
        ConsigneeNamePage.reader,
        ConsigneeAddressPage.reader
      ).tupled
        .map {
          case (name, consigneeAddress) =>
            val address = Address.prismAddressToConsigneeAddress(consigneeAddress)
            RequiredDetails(name, address)
        }

    val isConsigneeEoriKnown: UserAnswersReader[RequiredDetails] =
      IsConsigneeEoriKnownPage.reader.flatMap {
        isEoriKnown =>
          if (isEoriKnown) useEori else useAddress
      }

    AddConsigneePage.reader
      .filter(identity)
      .flatMap(
        _ => isConsigneeEoriKnown
      )
      .lower
  }

  implicit val userAnswersParser: UserAnswersParser[Option, TraderDetails] =
    UserAnswersOptionalParser(
      (
        principalTraderDetails,
        consignorDetails,
        consigneeDetails
      ).tupled
    )(
      x => TraderDetails(x._1, x._2, x._3)
    )

}
