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
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.{GuaranteeType, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages._

class GuaranteeDetailsSpec extends SpecBase with GeneratorSpec {

  "GuaranteeDetails" - {

    "GuaranteeReference" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        GuaranteeTypePage,
        GuaranteeReferencePage,
        LiabilityAmountPage,
        AccessCodePage
      )

      "can be parsed" - {

        "when all details for section have been answered" in {

          forAll(guaranteeReferenceUserAnswers) {
            case (expected, userAnswers) =>
              val result = UserAnswersReader[GuaranteeReference].run(userAnswers).value

              result mustEqual expected
          }
        }
      }

      "cannot be parsed" - {

        "when an answer is missing" in {

          forAll(guaranteeReferenceUserAnswers, mandatoryPages) {
            case ((_, ua), mandatoryPage) =>
              val userAnswers = ua.remove(mandatoryPage).success.value

              val result = UserAnswersReader[GuaranteeReference].run(userAnswers)

              result mustBe None
          }
        }
      }
    }

    "GuaranteeOther" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        GuaranteeTypePage,
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

              result mustBe None
          }
        }
      }
    }
  }

  implicit lazy val arbitraryGuaranteeOther: Arbitrary[GuaranteeOther] =
    Arbitrary {
      for {
        guaranteeType   <- Arbitrary.arbitrary[GuaranteeType]
        otherReference  <- nonEmptyString
        liabilityAmount <- nonEmptyString
      } yield GuaranteeOther(guaranteeType, otherReference, liabilityAmount)
    }

  implicit lazy val arbitraryGuaranteeReference: Arbitrary[GuaranteeReference] =
    Arbitrary {
      for {
        guaranteeType            <- Arbitrary.arbitrary[GuaranteeType]
        guaranteeReferenceNumber <- nonEmptyString
        liabilityAmount          <- nonEmptyString
        useDefaultAmount         <- Gen.option(Arbitrary.arbitrary[Boolean])
        accessCode               <- nonEmptyString
      } yield GuaranteeReference(guaranteeType, guaranteeReferenceNumber, liabilityAmount, useDefaultAmount, accessCode)
    }

  // format: off

  private val guaranteeOtherUserAnswers: Gen[(GuaranteeOther, UserAnswers)] =
    for {
      baseUserAnswers <- Arbitrary.arbitrary[UserAnswers]
      guaranteeOther  <- Arbitrary.arbitrary[GuaranteeOther]
    } yield {
      val guaranteeOtherUserAnswers = baseUserAnswers
        .set(GuaranteeTypePage, guaranteeOther.guaranteeType).success.value
        .set(OtherReferencePage, guaranteeOther.otherReference).success.value
        .set(OtherReferenceLiabilityAmountPage, guaranteeOther.liabilityAmount).success.value

      (guaranteeOther, guaranteeOtherUserAnswers)
    }

  private val guaranteeReferenceUserAnswers: Gen[(GuaranteeReference, UserAnswers)] =
    for {
      baseUserAnswers     <- Arbitrary.arbitrary[UserAnswers]
      guaranteeReference  <- Arbitrary.arbitrary[GuaranteeReference]
    } yield {
      val userAnswers = baseUserAnswers
        .set(GuaranteeTypePage, guaranteeReference.guaranteeType).success.value
        .set(GuaranteeReferencePage, guaranteeReference.guaranteeReferenceNumber).success.value
        .set(LiabilityAmountPage, guaranteeReference.liabilityAmount).success.value
        .set(AccessCodePage, guaranteeReference.accessCode).success.value

      val userAnswersWithOptionals = guaranteeReference.useDefaultAmount match {
        case Some(defaultAmount) => userAnswers.set(DefaultAmountPage, defaultAmount).success.value
        case _                   => userAnswers
      }

      (guaranteeReference, userAnswersWithOptionals)
    }
  // format: on

}
