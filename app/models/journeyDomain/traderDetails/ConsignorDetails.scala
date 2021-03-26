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

package models.journeyDomain.traderDetails

import cats._
import cats.data._
import cats.implicits._
import models.EoriNumber
import models.domain.Address
import models.journeyDomain.UserAnswersReader
import pages.{AddConsignorPage, ConsignorAddressPage, ConsignorEoriPage, ConsignorNamePage, IsConsignorEoriKnownPage}
import models.journeyDomain._

case class ConsignorDetails(name: String, address: Address, eori: Option[EoriNumber])

object ConsignorDetails {

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

}
