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

import cats.implicits._
import models.GuaranteeType
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages._

sealed trait GuaranteeDetails

object GuaranteeDetails {

  final case class GuaranteeReference(
    guaranteeType: GuaranteeType,
    guaranteeReferenceNumber: String,
    liabilityAmount: String,
    useDefaultAmount: Option[Boolean],
    accessCode: String
  ) extends GuaranteeDetails

  object GuaranteeReference {

    implicit val parseGuaranteeReference: UserAnswersReader[GuaranteeReference] =
      (
        GuaranteeTypePage.reader,
        GuaranteeReferencePage.reader,
        LiabilityAmountPage.reader,
        DefaultAmountPage.optionalReader,
        AccessCodePage.reader
      ).tupled.map((GuaranteeReference.apply _).tupled)

  }

  final case class GuaranteeOther(
    guaranteeType: GuaranteeType,
    otherReference: String,
    liabilityAmount: String
  ) extends GuaranteeDetails

  object GuaranteeOther {

    implicit val parseGuaranteeOther: UserAnswersReader[GuaranteeOther] =
      (
        GuaranteeTypePage.reader,
        OtherReferencePage.reader,
        OtherReferenceLiabilityAmountPage.reader
      ).tupled.map((GuaranteeOther.apply _).tupled)
  }
}
