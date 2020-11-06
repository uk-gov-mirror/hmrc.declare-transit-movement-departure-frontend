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
import models.messages.guarantee.GuaranteeReferenceWithGrn
import pages.{OtherReferenceLiabilityAmountPage, OtherReferencePage}

sealed trait Guarantee

object Guarantee {

  final case class GuaranteeReference(
    guaranteeType: GuaranteeType,
    guaranteeReferenceNumber: GuaranteeReferenceWithGrn,
    liabilityAmount: String,
    useDefaultAmount: Option[Boolean],
    accessCode: String
  ) extends Guarantee

  final case class GuaranteeOther(otherReference: String, liabilityAmount: String) extends Guarantee

  object GuaranteeOther {

    implicit val parseGuaranteeOther: UserAnswersReader[GuaranteeOther] =
      (
        OtherReferencePage.reader,
        OtherReferenceLiabilityAmountPage.reader
      ).tupled.map((GuaranteeOther.apply _).tupled)
  }
}
