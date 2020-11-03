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

import cats._
import cats.data._
import cats.implicits._
import models.{EoriNumber, PrincipalAddress, UserAnswers}
import TraderDetails.RequiredDetails
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
    def apply(eori: EoriNumber): RequiredDetails                        = TraderEori(eori)
    def apply(name: String, address: PrincipalAddress): RequiredDetails = PersonalInformation(name, address)
  }

  final case class PersonalInformation(name: String, address: PrincipalAddress) extends RequiredDetails
  final case class TraderEori(eori: EoriNumber) extends RequiredDetails

  val principalTraderDetails: UserAnswersReader[RequiredDetails] = {
    val useEori = WhatIsPrincipalEoriPage.reader
      .map(
        eori => RequiredDetails(EoriNumber(eori))
      )

    val useNameAndAddress: UserAnswersReader[RequiredDetails] = (
      PrincipalNamePage.reader,
      PrincipalAddressPage.reader
    ).tupled.map((PersonalInformation.apply _).tupled)

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
          case (name, address) => RequiredDetails(name, address.principalAddressDoNotMerge)
        }

    val asdf: UserAnswersReader[RequiredDetails] =
      IsConsignorEoriKnownPage.reader.flatMap(
        isEoriKnown => if (isEoriKnown) useEori else useAddress
      )

    AddConsignorPage.reader
      .filter(identity)
      .flatMap(
        _ => asdf
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
          case (name, address) => RequiredDetails(name, address.principalAddressDoNotMerge)
        }

    val asdf: UserAnswersReader[RequiredDetails] =
      IsConsigneeEoriKnownPage.reader.flatMap {
        isEoriKnown =>
          if (isEoriKnown) useEori else useAddress
      }

    AddConsigneePage.reader
      .filter(identity)
      .flatMap(
        _ => asdf
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
