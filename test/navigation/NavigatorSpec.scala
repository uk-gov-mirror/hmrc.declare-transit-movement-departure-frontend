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
import controllers.routes
import generators.Generators
import pages._
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(UnknownPage, NormalMode, answers)
              .mustBe(routes.IndexController.onPageLoad())
        }
      }

      "must go from Local Reference Number page to Add Security Details page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(LocalReferenceNumberPage, NormalMode, answers)
              .mustBe(routes.AddSecurityDetailsController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Declaration Type page to Procedure Type page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(DeclarationTypePage, NormalMode, answers)
              .mustBe(routes.ProcedureTypeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Procedure Type page to Container Used page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(ProcedureTypePage, NormalMode, answers)
              .mustBe(routes.ContainersUsedPageController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from  Container Used page to Declaration Place page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(ContainersUsedPage, NormalMode, answers)
              .mustBe(routes.DeclarationPlaceController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Declaration Place page to Declaration For Someone Else page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(DeclarationPlacePage, NormalMode, answers)
              .mustBe(routes.DeclarationForSomeoneElseController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Declaration For Someone Else page to Representative Name page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(DeclarationForSomeoneElsePage, NormalMode, answers)
              .mustBe(routes.RepresentativeNameController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Representative Name page to Representative Capacity page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(RepresentativeNamePage, NormalMode, answers)
              .mustBe(routes.RepresentativeCapacityController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Representative Capacity page to Check Your Answers page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(RepresentativeCapacityPage, NormalMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
        }
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map  to Check Your Answers" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(UnknownPage, CheckMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
        }
      }
    }
  }
}
