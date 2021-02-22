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
import controllers.transportDetails.{routes => transportDetailsRoute}
import generators.Generators
import models._
import models.reference.{Country, CountryCode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class TransportDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TransportDetailsNavigator

  "TransportDetailsNavigator" - {

    "in Normal Mode" - {
      "must go from InlandMode page to AddIdAtDeparture Page if value is not 5,7 or 50,70 and selected yes to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, true).toOption.value
            navigator
              .nextPage(InlandModePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from InlandMode page to AddIdAtDeparture Page if value is not 5,7 or 50,70 and selected no to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, false).toOption.value
            navigator
              .nextPage(InlandModePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from InlandMode page to Will these details change at border Page if value is 5/50 or 7/70" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            Seq("5", "50", "7", "70") foreach {
              inlandModeAnswer =>
                val updatedAnswers = answers.set(InlandModePage, inlandModeAnswer).success.value
                navigator
                  .nextPage(InlandModePage, NormalMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.id, NormalMode))
            }
        }
      }

      "must go from AddIdAtDeparture page to AddIdAtDepartureLater page when user selects 'no' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddIdAtDeparturePage, false).toOption.value

            navigator
              .nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureLaterController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from AddIdAtDeparture page to IdAtDeparture page when user selects 'yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddIdAtDeparturePage, true)
              .toOption
              .value
              .remove(IdAtDeparturePage)
              .success
              .value

            navigator
              .nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AddIdAtDepartureLater page to NationalityAtDeparture Page if selected no to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, false).toOption.value

            navigator
              .nextPage(AddIdAtDepartureLaterPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.NationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddIdAtDepartureLater page to AddNationalityAtDeparture Page if selected yes to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, true).toOption.value

            navigator
              .nextPage(AddIdAtDepartureLaterPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddNationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from IdAtDeparture page to NationalityAtDeparture Page if selected no to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, false).toOption.value
            navigator
              .nextPage(IdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.NationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from IdAtDeparture page to AddNationalityAtDeparture Page if selected yes to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, true).toOption.value
            navigator
              .nextPage(IdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddNationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from NationalityAtDeparture page to ChangeAtBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityAtDeparturePage, NormalMode, answers)
              .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from ChangeAtBorder page to TraderDetailsCheckYourAnswers page when user selects 'no' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ChangeAtBorderPage, false).toOption.value

            navigator
              .nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from ChangeAtBorder page to ModeAtBorder page when user selects 'yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ChangeAtBorderPage, true).toOption.value

            navigator
              .nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.ModeAtBorderController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from ModeAtBorder page to IdCrossingBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ModeAtBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.IdCrossingBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from IdCrossingBorder page to ModeCrossingBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(IdCrossingBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.ModeCrossingBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from ModeCrossingBorder to TransportDetailsCheckYourAnswers answer is  2, 5 or 7" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, "2").success.value

            navigator
              .nextPage(ModeCrossingBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from ModeCrossingBorder to NationalityCrossingBorder Page when answer  not 2, 5 or 7" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, ("17")).success.value

            navigator
              .nextPage(ModeCrossingBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.NationalityCrossingBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from NationalityCrossingBorder page to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityCrossingBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

    }

    "in Check Mode" - {

      "must go from InlandMode page to Add Id at Departure Page if add id at departure page was not asked previously" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ContainersUsedPage, true)
              .toOption
              .value
              .remove(AddIdAtDeparturePage)
              .success
              .value
            navigator
              .nextPage(InlandModePage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureController.onPageLoad(answers.id, CheckMode))
        }
      }

      "must go from InlandMode page to Change at Border Page if 5/50 or 7/70" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            Seq("5", "50", "7", "70") foreach {
              inlandModeAnswer =>
                val updatedAnswers = answers.set(InlandModePage, inlandModeAnswer).success.value
                navigator
                  .nextPage(InlandModePage, CheckMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.id, CheckMode))
            }
        }
      }

      "must go from AddIdAtDeparture page to IdAtDeparture page on selecting option 'Yes' and IdAtDeparture has no data " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(AddIdAtDeparturePage, true)
              .toOption
              .value
              .remove(IdAtDeparturePage)
              .success
              .value

            navigator
              .nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(answers.id, CheckMode))
        }
      }

      "must go from NationalityAtDeparturePage to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityAtDeparturePage, CheckMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from ChangeAtBorderPage to TransportDetailsCheckYourAnswers on selecting option 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(ChangeAtBorderPage, false).toOption.value

            navigator
              .nextPage(ChangeAtBorderPage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from ChangeAtBorderPage to ModeAtBorderPage on selecting option 'Yes' and no answers exist" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(ChangeAtBorderPage, true)
              .toOption
              .value
              .remove(ModeAtBorderPage)
              .success
              .value
              .remove(IdCrossingBorderPage)
              .success
              .value
              .remove(ModeCrossingBorderPage)
              .success
              .value
              .remove(NationalityCrossingBorderPage)
              .success
              .value

            navigator
              .nextPage(ChangeAtBorderPage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.ModeAtBorderController.onPageLoad(answers.id, CheckMode))
        }
      }

      "must go from ChangeAtBorderPage to TransportDetailsCheckYourAnswers on selecting option 'Yes' and answers already exist for ModeAtBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(ChangeAtBorderPage, true)
              .toOption
              .value
              .set(ModeAtBorderPage, "Bob")
              .success
              .value

            navigator
              .nextPage(ChangeAtBorderPage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from ModeAtBorderPage to TransportDetailsCheckYourAnswers Page when answer exists for IdCrossingBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(IdCrossingBorderPage, "Foo").success.value
            navigator
              .nextPage(ModeAtBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from ModeAtBorderPage to IdCrossingBorder Page when no answer exists for IdCrossingBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.remove(IdCrossingBorderPage).success.value
            navigator
              .nextPage(ModeAtBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdCrossingBorderController.onPageLoad(answers.id, CheckMode))
        }
      }

      "must go from IdCrossingBorder to TransportDetailsCheckYourAnswers Page when answer exists for ModeCrossingBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, "Foo").success.value
            navigator
              .nextPage(IdCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from IdCrossingBorder to ModeCrossingBorder Page when no answer exists for ModeCrossingBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.remove(ModeCrossingBorderPage).success.value
            navigator
              .nextPage(IdCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.ModeCrossingBorderController.onPageLoad(updatedAnswers.id, CheckMode))
        }
      }

      "must go from ModeCrossingBorder to TransportDetailsCheckYourAnswers answer is  2, 5 or 7 when answer for NationalityCrossingBorder Exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val country = Country(CountryCode("GB"), "United Kingdom")
            val updatedAnswers = answers
              .set(ModeCrossingBorderPage, "2")
              .success
              .value
              .set(NationalityCrossingBorderPage, country.code)
              .success
              .value

            navigator
              .nextPage(ModeCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from ModeCrossingBorder to NationalityCrossingBorder when answer is  not 2, 5 or 7 when answer for NationalityCrossingBorder does not exist" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ModeCrossingBorderPage, "17")
              .success
              .value
              .remove(NationalityCrossingBorderPage)
              .success
              .value

            navigator
              .nextPage(ModeCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.NationalityCrossingBorderController.onPageLoad(updatedAnswers.id, CheckMode))
        }
      }

      "must go from ModeCrossingBorder to TransportDetailsCheckYourAnswers Page when answer not 2, 5 or 7 and answer for Nationality at Border exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val country = Country(CountryCode("GB"), "United Kingdom")

            val updatedAnswers = answers
              .set(ModeCrossingBorderPage, ("17"))
              .success
              .value
              .set(NationalityCrossingBorderPage, country.code)
              .success
              .value

            navigator
              .nextPage(ModeCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from NationalityCrossingBorderPage to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityCrossingBorderPage, CheckMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }
    }
  }
}
