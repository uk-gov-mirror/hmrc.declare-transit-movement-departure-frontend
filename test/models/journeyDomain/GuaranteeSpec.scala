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

import base.{GeneratorSpec, SpecBase}
import models.UserAnswers
import models.journeyDomain.Guarantee.GuaranteeOther
import pages.{OtherReferenceLiabilityAmountPage, OtherReferencePage, QuestionPage}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._

class GuaranteeSpec extends SpecBase with GeneratorSpec {

  "Guarantee" - {

    "GuaranteeReferenceNumber" - {

      "can be parsed" - {

        "when all details for section have been answered" in {}
      }
    }

    "GuaranteeOther" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        OtherReferencePage,
        OtherReferenceLiabilityAmountPage
      )

      "can be parsed" - {

        "when all details for section have been answered" in {

          forAll(guaranteeOtherUserAnswers) {
            case (expected, userAnswers) =>
              val result = UserAnswersReader[GuaranteeOther].run(userAnswers).value

              result mustEqual expected
          }
        }
      }

      "cannot be parsed" - {

        "when an answer is missing" in {

          forAll(guaranteeOtherUserAnswers, mandatoryPages) {
            case ((_, ua), mandatoryPage) =>
              val userAnswers = ua.remove(mandatoryPage).success.value

              val result = UserAnswersReader[GuaranteeOther].run(userAnswers)

              result mustEqual None
          }
        }
      }
    }
  }

  implicit lazy val arbitraryGuaranteeOther: Arbitrary[GuaranteeOther] =
    Arbitrary {
      for {
        otherReference  <- nonEmptyString
        liabilityAmount <- nonEmptyString
      } yield GuaranteeOther(otherReference, liabilityAmount)
    }

  private val guaranteeOtherUserAnswers: Gen[(GuaranteeOther, UserAnswers)] =
    for {
      baseUserAnswers <- Arbitrary.arbitrary[UserAnswers]
      guaranteeOther  <- Arbitrary.arbitrary[GuaranteeOther]
    } yield {
      val guaranteeOtherUserAnswers = baseUserAnswers
        .set(OtherReferencePage, guaranteeOther.otherReference)
        .success
        .value
        .set(OtherReferenceLiabilityAmountPage, guaranteeOther.liabilityAmount)
        .success
        .value

      (guaranteeOther, guaranteeOtherUserAnswers)
    }
}
