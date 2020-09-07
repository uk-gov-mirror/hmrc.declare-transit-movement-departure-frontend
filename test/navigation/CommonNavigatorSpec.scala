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
import controllers.routeDetails.{routes => routeDetailsRoute}
import controllers.routes
import controllers.traderDetails.{routes => traderDetailsRoute}
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class CommonNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new CommonNavigator

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

      "Trader Details section" - {

        "must go from Is principal eori known page to what is eori number page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsPrincipalEoriKnownPage, true).success.value
              navigator.nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsPrincipalEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is principal eori known page to principal name page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsPrincipalEoriKnownPage, false).success.value
              navigator.nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Principal eori page to add consignor page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(WhatIsPrincipalEoriPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Principal name page to Principal address page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(PrincipalNamePage, NormalMode, answers)
                .mustBe(traderDetailsRoute.PrincipalAddressController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Principal address page to Add consignor page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(PrincipalAddressPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignor page to Is consignor eori known page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsignorPage, true).success.value
              navigator.nextPage(AddConsignorPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsignorEoriKnownController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignor page to Add consignee page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsignorPage, false).success.value
              navigator.nextPage(AddConsignorPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.AddConsigneeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignor eori known page to Consignor eori page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsignorEoriKnownPage, true).success.value
              navigator.nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignor eori known page to Consignor name page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsignorEoriKnownPage, false).success.value
              navigator.nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignor name page to Consignor address page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsignorNamePage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsignorAddressController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignor address page to Add consignee page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsignorAddressPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.AddConsigneeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignee page to Is consignee eori known page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsigneePage, true).success.value
              navigator.nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsigneeEoriKnownController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Add consignee page to Trader details cya page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsigneePage, false).success.value
              navigator.nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Is consignee eori known page to Consignee eori page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsigneeEoriKnownPage, true).success.value
              navigator.nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsConsigneeEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignee eori known page to Consignee name page when 'NO' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsConsigneeEoriKnownPage, false).success.value
              navigator.nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignee name page to Consignee address page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsigneeNamePage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsigneeAddressController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignee address page to Trader details cya page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator.nextPage(ConsigneeAddressPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

      }

      "Route Details section" - {
        "must go from Country of dispatch page to Office of departure page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(CountryOfDispatchPage, NormalMode, answers)
                .mustBe(routeDetailsRoute.OfficeOfDepartureController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Office of departure page to destination country page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(OfficeOfDeparturePage, NormalMode, answers)
                .mustBe(routeDetailsRoute.DestinationCountryController.onPageLoad(answers.id, NormalMode))
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
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
        }
      }


      "Trader Details Section" - {

        "Must go from Is principal eori known page to What is principal eori page if the option selected is 'YES'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(IsPrincipalEoriKnownPage, true).toOption.value
                .remove(WhatIsPrincipalEoriPage).toOption.value

              navigator.nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsPrincipalEoriController.onPageLoad(answers.id, CheckMode))
          }
        }

        "Must go from Is principal eori known page to What is principal's name page if the option selected is 'NO'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(IsPrincipalEoriKnownPage, false).toOption.value
                .remove(WhatIsPrincipalEoriPage).toOption.value

              navigator.nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.id, CheckMode))
          }
        }

        "must go from Principal name page to Check Your Answers page if Principal's Address previously answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[PrincipalAddress]) {

            (answers, principalAddress) =>

              val updatedAnswers =
                answers.set(PrincipalAddressPage, principalAddress).success.value
              navigator.nextPage(PrincipalNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Principal name page to Principals Address page if Principal's Address not previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              val updatedAnswers =
                answers.remove(PrincipalAddressPage).success.value

              navigator.nextPage(PrincipalNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.PrincipalAddressController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }

        "must go from What is Principal's Eori page to Check Your Answers Page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(WhatIsPrincipalEoriPage, CheckMode, answers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Add consignor page to Is consignor eori known page when 'YES' is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddConsignorPage, true).success.value
              navigator.nextPage(AddConsignorPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsignorEoriKnownController.onPageLoad(answers.id, CheckMode))
          }
        }
      }
    }
  }
}