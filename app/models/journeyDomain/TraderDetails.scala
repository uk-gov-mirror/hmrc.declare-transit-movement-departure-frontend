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
import models.ProcedureType.{Normal, Simplified}
import models.domain.Address
import models.journeyDomain.TraderDetails._
import models.{EoriNumber, UserAnswers}
import pages._

case class TraderDetails(
  principalTraderDetails: PrincipalTraderDetails,
  consignor: Option[ConsignorDetails],
  consignee: Option[ConsigneeDetails]
)

object TraderDetails {

  case class ConsignorDetails(name: String, address: Address, eori: Option[EoriNumber])
  case class ConsigneeDetails(name: String, address: Address, eori: Option[EoriNumber])

  sealed trait PrincipalTraderDetails

  object PrincipalTraderDetails {
    def apply(eori: EoriNumber): PrincipalTraderDetails               = PrincipalTraderEoriInfo(eori)
    def apply(name: String, address: Address): PrincipalTraderDetails = PrincipalTraderPersonalInfo(name, address)
  }

  final case class PrincipalTraderPersonalInfo(name: String, address: Address) extends PrincipalTraderDetails
  final case class PrincipalTraderEoriInfo(eori: EoriNumber) extends PrincipalTraderDetails

  val principalTraderDetails: UserAnswersReader[PrincipalTraderDetails] = {
    val simplified = ProcedureTypePage.reader
      .filter(_ == Simplified)
      .productR(
        WhatIsPrincipalEoriPage.reader
          .map(EoriNumber(_))
          .map(PrincipalTraderDetails(_))
      )

    val normal = ProcedureTypePage.reader
      .filter(_ == Normal)
      .productR {
        IsPrincipalEoriKnownPage.reader
          .flatMap {
            case true =>
              WhatIsPrincipalEoriPage.reader
                .map(EoriNumber(_))
                .map(PrincipalTraderDetails(_))
            case false =>
              (
                PrincipalNamePage.reader,
                PrincipalAddressPage.reader
              ).tupled.map {
                case (name, principalAddress) =>
                  val address = Address.prismAddressToPrincipalAddress(principalAddress)
                  PrincipalTraderDetails(name, address)
              }
          }
      }

    normal orElse simplified
  }

  val consignorDetails: UserAnswersReader[Option[ConsignorDetails]] = {
    def readConsignorEoriPage: UserAnswersReader[Option[EoriNumber]] =
      IsConsignorEoriKnownPage.reader
        .flatMap {
          bool =>
            if (bool)
              ConsignorEoriPage.reader.map(
                eori => Some(EoriNumber(eori))
              )
            else none[EoriNumber].pure[UserAnswersReader]
        }
    val consignorInformation: UserAnswersReader[ConsignorDetails] =
      (
        ConsignorNamePage.reader,
        ConsignorAddressPage.reader,
        readConsignorEoriPage
      ).tupled
        .map {
          case (name, consignorAddress, eori) =>
            val address = Address.prismAddressToConsignorAddress(consignorAddress)
//            PrincipalTrader(name, address, eori)
            ???
        }

    AddConsignorPage.reader
      .filter(identity)
      .flatMap(
        _ => consignorInformation
      )
      .lower
  }

  val consigneeDetails: UserAnswersReader[Option[ConsigneeDetails]] = {
    def readConsigneeEoriPage: UserAnswersReader[Option[EoriNumber]] =
      IsConsigneeEoriKnownPage.reader
        .flatMap {
          bool =>
            if (bool)
              WhatIsConsigneeEoriPage.reader.map(
                eori => Some(EoriNumber(eori))
              )
            else none[EoriNumber].pure[UserAnswersReader]
        }

    val consigneeInformation: UserAnswersReader[ConsigneeDetails] =
      (
        ConsigneeNamePage.reader,
        ConsigneeAddressPage.reader,
        readConsigneeEoriPage
      ).tupled
        .map {
          case (name, consigneeAddress, eori) =>
            val address = Address.prismAddressToConsigneeAddress(consigneeAddress)
            ???
        }

    AddConsigneePage.reader
      .filter(identity)
      .flatMap(
        _ => consigneeInformation
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
