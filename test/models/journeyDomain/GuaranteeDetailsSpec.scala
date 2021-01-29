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

import base.{GeneratorSpec, SpecBase}
import generators.JourneyModelGenerators
import models.{Index, UserAnswers}
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.journeyDomain.PackagesSpec.UserAnswersNoErrorSet
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}

class GuaranteeDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "GuaranteeDetails" - {

    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[GuaranteeDetails], arbitrary[UserAnswers]) {
          case (guarantee, userAnswers) =>
            val updatedUserAnswer                = GuaranteeDetailsSpec.setGuaranteeDetails(guarantee)(userAnswers)
            val result: Option[GuaranteeDetails] = UserAnswersReader[GuaranteeDetails](GuaranteeDetails.parseGuaranteeDetails(index)).run(updatedUserAnswer)

            result.value mustEqual guarantee
        }
      }
    }

    "GuaranteeReference" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        GuaranteeTypePage(index),
        GuaranteeReferencePage(index),
        LiabilityAmountPage(index),
        AccessCodePage(index)
      )

      "can be parsed" - {

        "when all mandatory field are defined and DefaultLiability is not defined" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer = {
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected)(userAnswers)
                  .remove(DefaultAmountPage(index))
                  .toOption
                  .value
              }
              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer).value

              result mustEqual expected
          }
        }

        "when all mandatory field are defined and use DefaultLiability amount when DefaultLiability is defined as true" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer = {
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected)(userAnswers)
                  .set(DefaultAmountPage(index), true)
                  .toOption
                  .value
                  .remove(LiabilityAmountPage(index))
                  .toOption
                  .value
              }
              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer).value

              result.liabilityAmount mustEqual "10000"
          }
        }

        "when all mandatory field are defined and use LiabilityAmount when DefaultLiability is defined as false" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer = {
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected)(userAnswers)
                  .set(DefaultAmountPage(index), false)
                  .toOption
                  .value
              }
              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer).value

              result.liabilityAmount mustEqual expected.liabilityAmount
          }
        }
      }

      "cannot be parsed" - {

        "when an answer is missing" in {

          forAll(arbitrary[UserAnswers], mandatoryPages) {
            case (ua, mandatoryPage) =>
              val userAnswers = ua.remove(mandatoryPage).success.value
              val result      = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(userAnswers)

              result mustBe None
          }
        }

        "when LiabilityAmount is missing and DefaultLiability is false" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer = {
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected)(userAnswers)
                  .set(DefaultAmountPage(index), false)
                  .toOption
                  .value
                  .remove(LiabilityAmountPage(index))
                  .toOption
                  .value
              }

              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer)

              result mustBe None
          }
        }

        "when LiabilityAmount is missing and DefaultLiability is missing" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer = {
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected)(userAnswers)
                  .remove(DefaultAmountPage(index))
                  .toOption
                  .value
                  .remove(LiabilityAmountPage(index))
                  .toOption
                  .value
              }

              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer)

              result mustBe None
          }
        }
      }
    }

    "GuaranteeOther" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        GuaranteeTypePage(index),
        OtherReferencePage(index)
      )

      "can be parsed" - {

        "when all details for section have been answered" in {

          forAll(arbitrary[GuaranteeOther], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer = GuaranteeDetailsSpec.setGuaranteeOtherUserAnswers(expected)(userAnswers)
              val result            = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(updatedUserAnswer).value

              result mustEqual expected
          }
        }
      }

      "cannot be parsed" - {

        "when an answer is missing" in {

          forAll(arbitrary[UserAnswers], mandatoryPages) {
            case (ua, mandatoryPage) =>
              val userAnswers = ua.remove(mandatoryPage).success.value
              val result      = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(userAnswers)

              result mustBe None
          }
        }
      }
    }
  }
}

object GuaranteeDetailsSpec {

  val index: Index = Index(0)

  def setGuaranteeDetails(guaranteeDetails: GuaranteeDetails)(startUserAnswers: UserAnswers): UserAnswers =
    guaranteeDetails match {
      case guaranteeReference: GuaranteeReference => setGuaranteeReferenceUserAnswers(guaranteeReference)(startUserAnswers)
      case guaranteeOther: GuaranteeOther         => setGuaranteeOtherUserAnswers(guaranteeOther)(startUserAnswers)
    }

  def setGuaranteeOtherUserAnswers(otherGuarantee: GuaranteeOther)(startUserAnswers: UserAnswers): UserAnswers = {

    val guaranteeOtherUserAnswers = startUserAnswers
      .unsafeSetVal(GuaranteeTypePage(index))(otherGuarantee.guaranteeType)
      .unsafeSetVal(OtherReferencePage(index))(otherGuarantee.otherReference)

    guaranteeOtherUserAnswers
  }

  def setGuaranteeReferenceUserAnswers(guaranteeReference: GuaranteeReference)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
      .unsafeSetVal(GuaranteeTypePage(index))(guaranteeReference.guaranteeType)
      .unsafeSetVal(GuaranteeReferencePage(index))(guaranteeReference.guaranteeReferenceNumber)
      .unsafeSetVal(LiabilityAmountPage(index))(guaranteeReference.liabilityAmount)
      .unsafeSetVal(AccessCodePage(index))(guaranteeReference.accessCode)
}
