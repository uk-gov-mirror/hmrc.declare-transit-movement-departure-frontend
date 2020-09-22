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
import controllers.routeDetails.routes
import controllers.{routes => mainRoutes}
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class RouteDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new RouteDetailsNavigator

  "RouteDetailsNavigator" - {

    "in Normal mode" - {

      "Route Details section" - {
        "must go from Country of dispatch page to Office of departure page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(CountryOfDispatchPage, NormalMode, answers)
                .mustBe(routes.OfficeOfDepartureController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Office of departure page to destination country page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(OfficeOfDeparturePage, NormalMode, answers)
                .mustBe(routes.DestinationCountryController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Destination Office Page to Add another transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(DestinationOfficePage, NormalMode, answers)
                .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from Add another transit office to arrival times at office of transit page when AddSecurityDetailsPage value is true" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, true).toOption.value

              navigator.nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.ArrivalTimesAtOfficeController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from Add another transit office to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, false).toOption.value

              navigator.nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Arrival times at office of transit page to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(ArrivalTimesAtOfficePage(index), NormalMode, answers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map  to Check Your Answers" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(UnknownPage, CheckMode, answers)
              .mustBe(mainRoutes.SessionExpiredController.onPageLoad())
        }
      }

      "Must go from Country of dispatch to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(CountryOfDispatchPage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))

        }

      }

      "Must go from Office Of Departure to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(OfficeOfDeparturePage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))

        }

      }

      "Must go from Destination Country to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(DestinationCountryPage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))

        }

      }
    }
  }
}