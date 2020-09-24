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

import java.time.LocalDate

import base.SpecBase
import controllers.goodsSummary.{routes => goodsSummaryRoute}
import generators.Generators
import models.ProcedureType.{Normal, Simplified}
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class GoodsSummaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new GoodsSummaryNavigator

  "GoodsSummaryNavigator" - {

    "in Normal Mode" - {

      "must go from DeclarePackagesPage to TotalPackagesPage when user selects Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(DeclarePackagesPage, true).toOption.value

            navigator.nextPage(DeclarePackagesPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalPackagesController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from DeclarePackagesPage to TotalGrossMassPage when user selects No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(DeclarePackagesPage, false).toOption.value

            navigator.nextPage(DeclarePackagesPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalGrossMassController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from TotalPackagesPage to TotalGrossMassPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(TotalPackagesPage, 1).toOption.value

            navigator.nextPage(TotalPackagesPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalGrossMassController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from TotalGrossMassPage to AuthorisedLocationCodePage when on Simplified journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ProcedureTypePage, Simplified).toOption.value

            navigator.nextPage(TotalGrossMassPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AuthorisedLocationCodeController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from TotalGrossMassPage to AddCustomsApprovedLocationPage when on Normal journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ProcedureTypePage, Normal).toOption.value

            navigator.nextPage(TotalGrossMassPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddCustomsApprovedLocationController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AuthorisedLocationCodePage to ControlResultDateLimitPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AuthorisedLocationCodePage, "test").toOption.value

            navigator.nextPage(AuthorisedLocationCodePage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.ControlResultDateLimitController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AddCustomsApprovedLocationPage to CustomsApprovedLocationPage when answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, true).toOption.value

            navigator.nextPage(AddCustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.CustomsApprovedLocationController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AddCustomsApprovedLocationPage to AddSealsPage when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, false).toOption.value

            navigator.nextPage(AddCustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from CustomsResultDateLimitPage to AddSealsPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val date = LocalDate.now
            val updatedAnswers = answers.set(ControlResultDateLimitPage, date).toOption.value

            navigator.nextPage(ControlResultDateLimitPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from CustomsApprovedLocationPage to AddSealsPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(CustomsApprovedLocationPage, "test").toOption.value

            navigator.nextPage(CustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }
    }

    "in Check Mode" - {

      "must go from DeclarePackagesPage to TotalPackagesPage when selecting Yes and TotalPackages has no data" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(DeclarePackagesPage, true).toOption.value
              .remove(TotalPackagesPage).success.value

            navigator.nextPage(DeclarePackagesPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalPackagesController.onPageLoad(updatedAnswers.id, CheckMode))
        }
      }

      "must go from DeclarePackagesPage to CheckYourAnswersPage when selecting Yes and TotalPackages has data" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(DeclarePackagesPage, true).toOption.value
              .set(TotalPackagesPage, 1).success.value

            navigator.nextPage(DeclarePackagesPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from DeclarePackagesPage to CheckYourAnswersPage when selecting No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(DeclarePackagesPage, false).toOption.value
              .remove(TotalPackagesPage).success.value

            navigator.nextPage(DeclarePackagesPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

            "must go from TotalGrossMage to CheckYourAnswersPage " in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers.set(TotalGrossMassPage, "100").success.value

                  navigator.nextPage(TotalGrossMassPage, CheckMode, updatedAnswers)
                    .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
              }
            }

            "must go from AuthLocationCodePage to CheckYourAnswersPage " in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers.set(AuthorisedLocationCodePage, "test code").success.value

                  navigator.nextPage(AuthorisedLocationCodePage, CheckMode, updatedAnswers)
                    .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
              }
            }


      "must go from AddCustomsApprovedLocation to CheckYourAnswers page when selecting No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, false).toOption.value

            navigator.nextPage(AddCustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }


      "must go from AddCustomsApprovedLocation to CustomsApprovedLocation when selecting Yes and CustomsApprovedLocation has data" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, true).toOption.value
              .remove(CustomsApprovedLocationPage).success.value

            navigator.nextPage(AddCustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.CustomsApprovedLocationController.onPageLoad(updatedAnswers.id, CheckMode))
        }
      }

            "must go from ControlResultDateLimitPage to CheckYourAnswersPage " in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                val date = LocalDate.now
                val updatedAnswers = answers.set(ControlResultDateLimitPage, date).success.value

                  navigator.nextPage(ControlResultDateLimitPage, CheckMode, updatedAnswers)
                    .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
              }
            }


            "must go from CustomsApprovedLocation page to CheckYourAnswersPage " in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers.set(CustomsApprovedLocationPage, "test data").success.value

                  navigator.nextPage(CustomsApprovedLocationPage, CheckMode, updatedAnswers)
                    .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
              }
            }



    }
  }
}
