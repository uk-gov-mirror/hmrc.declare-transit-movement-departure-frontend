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

import cats._
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

  implicit val principalTraderDetails: UserAnswersReader[PrincipalTraderDetails] = {
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

  implicit val consignorDetails: UserAnswersReader[Option[ConsignorDetails]] = {
    val readConsignorEoriPage =
      IsConsignorEoriKnownPage.reader
        .flatMap {
          eoriKnown =>
            if (eoriKnown)
              ConsignorEoriPage.reader.map(EoriNumber(_)).map(Option(_))
            else
              none[EoriNumber].pure[UserAnswersReader]
        }

    AddConsignorPage.reader
      .flatMap(
        addConsignor =>
          if (addConsignor) {
            (
              readConsignorEoriPage,
              ConsignorNamePage.reader,
              ConsignorAddressPage.reader
            ).tupled
              .map {
                case (eori, name, consignorAddress) =>
                  val address = Address.prismAddressToConsignorAddress(consignorAddress)
                  Option(ConsignorDetails(name, address, eori))
              }
          } else {
            none[ConsignorDetails].pure[UserAnswersReader]
        }
      )
  }

  implicit val consigneeDetails: UserAnswersReader[Option[ConsigneeDetails]] = {
    val readConsigneeEoriPage =
      IsConsigneeEoriKnownPage.reader
        .flatMap {
          eoriKnown =>
            if (eoriKnown)
              WhatIsConsigneeEoriPage.reader.map(EoriNumber(_)).map(Option(_))
            else
              none[EoriNumber].pure[UserAnswersReader]
        }

    AddConsigneePage.reader
      .flatMap(
        addConsignor =>
          if (addConsignor) {
            (
              readConsigneeEoriPage,
              ConsigneeNamePage.reader,
              ConsigneeAddressPage.reader
            ).tupled
              .map {
                case (eori, name, consigneeAddress) =>
                  val address = Address.prismAddressToConsigneeAddress(consigneeAddress)
                  Option(ConsigneeDetails(name, address, eori))
              }
          } else {
            none[ConsigneeDetails].pure[UserAnswersReader]
        }
      )
  }

  implicit val userAnswersParser: UserAnswersParser[Option, TraderDetails] =
    UserAnswersOptionalParser(
      (
        UserAnswersReader[PrincipalTraderDetails],
        UserAnswersReader[Option[ConsignorDetails]],
        UserAnswersReader[Option[ConsigneeDetails]]
      ).tupled
    )(
      x => TraderDetails(x._1, x._2, x._3)
    )

}
