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
import models.Index
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.safetyAndSecurity.{AddCommercialReferenceNumberAllItemsPage, AddTransportChargesPaymentMethodPage}

final case class SecurityDetails(
  methodOfPayment: Option[String],
  commercialReferenceNumber: Option[String],
  dangerousGoodsCode: Option[String]
)

object SecurityDetails {

  private def methodOfPaymentPage(index: Index): UserAnswersReader[Option[String]] =
    AddTransportChargesPaymentMethodPage.reader
      .flatMap {
        bool =>
          if (!bool) TransportChargesPage(index).reader.map(Some(_))
          else none[String].pure[UserAnswersReader]
      }

  private def commercialReferenceNumberPage(index: Index): UserAnswersReader[Option[String]] =
    AddCommercialReferenceNumberAllItemsPage.reader
      .flatMap {
        bool =>
          if (!bool) CommercialReferenceNumberPage(index).reader.map(Some(_))
          else none[String].pure[UserAnswersReader]
      }

  private def dangerousGoodsCodePage(index: Index): UserAnswersReader[Option[String]] =
    AddDangerousGoodsCodePage(index).reader
      .flatMap {
        bool =>
          if (bool) DangerousGoodsCodePage(index).reader.map(Some(_))
          else none[String].pure[UserAnswersReader]
      }

  def securityDetailsReader(index: Index): UserAnswersReader[SecurityDetails] =
    (
      methodOfPaymentPage(index),
      commercialReferenceNumberPage(index),
      dangerousGoodsCodePage(index)
    ).tupled.map((SecurityDetails.apply _).tupled)
}
