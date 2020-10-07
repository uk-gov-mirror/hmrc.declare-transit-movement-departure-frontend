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

package navigation

import base.SpecBase
import controllers.guaranteeDetails.{routes => guaranteeDetailsRoute}
import generators.Generators
import models.GuaranteeType._
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.guaranteeDetails._

//TODO update to CYA when CYA merged in
class GuaranteeDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off

  val navigator = new GuaranteeDetailsNavigator
  "GuaranteeDetailsNavigator" - {
    "in normal mode" - {
      "must go from GuaranteeTypePage to GuaranteeReferenceNumberPage when user selects" - {
        "GuaranteeWaiver" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage, GuaranteeWaiver).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "ComprehensiveGuarantee" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage, ComprehensiveGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "IndividualGuarantee" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage, IndividualGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "FlatRateVoucher" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage, FlatRateVoucher).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "IndividualGuaranteeMultiple" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage, IndividualGuaranteeMultiple).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }
      }

      "to OtherReferencePage when user selects" - {
        "CashDepositGuarantee" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage).success.value
                .set(GuaranteeTypePage, CashDepositGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "GuaranteeNotRequired" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage).success.value
                .set(GuaranteeTypePage, GuaranteeNotRequired).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "GuaranteeWaivedRedirect" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage).success.value
                .set(GuaranteeTypePage, GuaranteeWaivedRedirect).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "GuaranteeWaiverByAgreement" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage).success.value
                .set(GuaranteeTypePage, GuaranteeWaiverByAgreement).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "GuaranteeWaiverSecured" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage).success.value
                .set(GuaranteeTypePage, GuaranteeWaiverSecured).success.value
              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }
      }

      "From OtherReferencePage to LiabilityAmountPage" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers =
              answers.set(GuaranteeReferencePage, "12345678901234567").success.value
            navigator
              .nextPage(OtherReferencePage, NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.LiabilityAmountController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "From GuaranteeReferencePage to LiabilityAmountPage" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers.set(GuaranteeReferencePage, "test").success.value
            navigator
              .nextPage(GuaranteeReferencePage, NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.LiabilityAmountController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "From LiabilityAmountPage to AccessCodePage" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers =
            answers
              .set(GuaranteeTypePage, GuaranteeWaiver).success.value
              .set(GuaranteeReferencePage, "test").success.value
              .set(LiabilityAmountPage, "100.12").success.value
            navigator
              .nextPage(LiabilityAmountPage, NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "From AccessCodeController to CYA" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers.set(AccessCodePage, "1234").success.value
            navigator
              .nextPage(AccessCodePage, NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }
    }

    "in Checkmode" - {
      "must go from Guarantee Type page to" - {
        "OtherReferencePage when user selects 3,5,6,7 or A and guaranteeReference has been set to 17 characters" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .set(GuaranteeReferencePage, "12345678901234567").success.value
                .set(GuaranteeTypePage, CashDepositGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage, CheckMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }

        "to CYA page when user selects 3,5,6,7 or A and previously had selected GuaranteeWaivedRedirect" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage).success.value
                .set(OtherReferencePage, "test").success.value
                .set(GuaranteeTypePage, GuaranteeWaivedRedirect).success.value
                .set(OtherReferencePage, "test").success.value
                .remove(GuaranteeReferencePage).success.value

              navigator
                .nextPage(GuaranteeTypePage, CheckMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }

        "to GuaranteeReference page when user changes answer from 0,1,2, or 9 and guaranteeReference has been set to 17 characters" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .set(GuaranteeTypePage, GuaranteeWaiver).success.value
                .set(GuaranteeReferencePage, "12345678901234567").success.value
                .set(GuaranteeTypePage, ComprehensiveGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage, CheckMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }

        "to GuaranteedReference page when user changes answer from 0,1,2, or 9 and guaranteeReference has been set to 17 characters" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .set(GuaranteeTypePage, FlatRateVoucher).success.value
                .set(GuaranteeReferencePage, "12345678901234567").success.value
              navigator
                .nextPage(GuaranteeTypePage, CheckMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }

        "to GuaranteedReference page when user selects 0,1,2, or 9  and guaranteeReference has been set to 24 characters" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .set(GuaranteeReferencePage, "123456789012345678901234").success.value
                .set(GuaranteeTypePage, GuaranteeWaiver).success.value
              navigator
                .nextPage(GuaranteeTypePage, CheckMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }

        "to CYA page when user selects FlatRateVoucher and guaranteeReference has been set to 17 characters" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .set(GuaranteeReferencePage, "123456789012345678901234").success.value
                .set(GuaranteeTypePage, FlatRateVoucher).success.value
              navigator
                .nextPage(GuaranteeTypePage, CheckMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }
      }

      "From OtherReferencePage to CYA" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(OtherReferencePage, "test").success.value
            navigator
              .nextPage(OtherReferencePage, CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "From GuaranteeReferencePage to CYA if liability amountc and access code exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(OtherReferencePage, "125678901234567").success.value
              .set(LiabilityAmountPage, "1").success.value
              .set(AccessCodePage, "1111").success.value
            navigator
              .nextPage(GuaranteeReferencePage, CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "From LiabilityAmountPage to CYA if access code exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(LiabilityAmountPage, "100.00").success.value
              .set(AccessCodePage, "1111").success.value
            navigator
              .nextPage(GuaranteeReferencePage, CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "From AccessCodePage to CYA" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeTypePage, GuaranteeWaiver).success.value
              .set(GuaranteeReferencePage, "12345678901234567").success.value
              .set(LiabilityAmountPage, "1").success.value
              .set(AccessCodePage, "1111").success.value
            navigator
              .nextPage(GuaranteeReferencePage, CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }
    }
  }
  // format: on
}
