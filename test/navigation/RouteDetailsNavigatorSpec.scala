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

package navigation

import base.SpecBase
import controllers.routeDetails.routes
import controllers.{routes => mainRoutes}
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import queries.OfficeOfTransitQuery

class RouteDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new RouteDetailsNavigator

  "RouteDetailsNavigator" - {

    "in Normal mode" - {

      "Route Details section" - {
        "must go from Country of dispatch page to Office of departure page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(CountryOfDispatchPage, NormalMode, answers)
                .mustBe(routes.OfficeOfDepartureController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Office of departure page to destination country page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(OfficeOfDeparturePage, NormalMode, answers)
                .mustBe(routes.DestinationCountryController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Destination Country page to Movement Destination Country page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(DestinationCountryPage, NormalMode, answers)
                .mustBe(routes.MovementDestinationCountryController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Movement Destination Country page to Destination Office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(MovementDestinationCountryPage, NormalMode, answers)
                .mustBe(routes.DestinationOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Destination Office Page to Add another transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(DestinationOfficePage, NormalMode, answers)
                .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from Add another transit office to arrival times at office of transit page when AddSecurityDetailsPage value is true" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, true).toOption.value

              navigator
                .nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.ArrivalTimesAtOfficeController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from Add another transit office to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, false).toOption.value

              navigator
                .nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Arrival times at office of transit page to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ArrivalTimesAtOfficePage(index), NormalMode, answers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Added transit office page to Add another transit office page when selected option 'Yes'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .set(AddTransitOfficePage, true)
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(index), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(1)), "id2")
                .toOption
                .value

              navigator
                .nextPage(AddTransitOfficePage, NormalMode, userAnswers)
                .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(answers.id, Index(2), NormalMode))
          }
        }

        "must go from Added transit office page to router details check your answers page when selected option 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers.set(AddTransitOfficePage, false).toOption.value

              navigator
                .nextPage(AddTransitOfficePage, NormalMode, userAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Added transit office page to router details check your answers page when number of offices added exceeds 5" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .remove(AddTransitOfficePage)
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(index), "id")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(1)), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(2)), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(3)), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(4)), "id1")
                .toOption
                .value

              navigator
                .nextPage(AddTransitOfficePage, NormalMode, userAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Confirm Remove OfficeOfTransit Page to Added office of transit page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .set(AddTransitOfficePage, true)
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(index), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(1)), "id2")
                .toOption
                .value
                .set(ConfirmRemoveOfficeOfTransitPage, true)
                .toOption
                .value

              navigator
                .nextPage(ConfirmRemoveOfficeOfTransitPage, NormalMode, userAnswers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Confirm Remove OfficeOfTransit Page to Add another offices of transit when all records are removed" in {
          val userAnswers = emptyUserAnswers
            .remove(OfficeOfTransitQuery(index))
            .toOption
            .value
            .set(ConfirmRemoveOfficeOfTransitPage, true)
            .toOption
            .value

          navigator
            .nextPage(ConfirmRemoveOfficeOfTransitPage, NormalMode, userAnswers)
            .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(emptyUserAnswers.id, index, NormalMode))

        }
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map  to Check Your Answers" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(UnknownPage, CheckMode, answers)
              .mustBe(mainRoutes.SessionExpiredController.onPageLoad())
        }
      }

      "Must go from Country of dispatch to Office of Departure" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CountryOfDispatchPage, CheckMode, answers)
              .mustBe(routes.OfficeOfDepartureController.onPageLoad(answers.id, CheckMode))

        }

      }

      "Must go from Office Of Departure to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(OfficeOfDeparturePage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))

        }

      }

      "Must go from Destination Country to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(DestinationCountryPage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))

        }

      }

      "Must go from Destination Office to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(DestinationOfficePage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))

        }

      }
    }
  }
}
