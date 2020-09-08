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
import controllers.movementDetails.{routes => movementDetailsRoute}
import controllers.traderDetails.{routes => traderDetailsRoute}
import controllers.transportDetails.{routes => transportDetailsRoute}
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

      "Movement Details Section" - {

        "must go from Declaration Type page to Procedure Type page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(DeclarationTypePage, NormalMode, answers)
                .mustBe(movementDetailsRoute.ProcedureTypeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Procedure Type page to Container Used page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(ProcedureTypePage, NormalMode, answers)
                .mustBe(movementDetailsRoute.ContainersUsedPageController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from  Container Used page to Declaration Place page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(ContainersUsedPage, NormalMode, answers)
                .mustBe(movementDetailsRoute.DeclarationPlaceController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Declaration Place page to Declaration For Someone Else page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(DeclarationPlacePage, NormalMode, answers)
                .mustBe(movementDetailsRoute.DeclarationForSomeoneElseController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Declaration For Someone Else page to Representative Name page on selecting option 'Yes'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, true).toOption.value

              navigator.nextPage(DeclarationForSomeoneElsePage, NormalMode, updatedUserAnswers)
                .mustBe(movementDetailsRoute.RepresentativeNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Declaration For Someone Else page to movement details check your answers page on selecting option 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, false).toOption.value

              navigator.nextPage(DeclarationForSomeoneElsePage, NormalMode, updatedUserAnswers)
                .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Representative Name page to Representative Capacity page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(RepresentativeNamePage, NormalMode, answers)
                .mustBe(movementDetailsRoute.RepresentativeCapacityController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Representative Capacity page to Check Your Answers page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(RepresentativeCapacityPage, NormalMode, answers)
                .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
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
      "Movement Details Section" - {

        "Must go from Declaration-Type to Movement Details Check Your Answers" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>

              navigator.nextPage(DeclarationTypePage, CheckMode, answers)
                .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }
        }

          "must go from Declaration For Someone Else page to Representative Name page on selecting option 'Yes'" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, true).toOption.value
                  .remove(RepresentativeNamePage).toOption.value

                navigator.nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
                  .mustBe(movementDetailsRoute.RepresentativeNameController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from Declaration For Someone Else page to CYA page on selecting option 'Yes' and representativeNamePage has data" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, true).toOption.value
                  .set(RepresentativeNamePage, "answer").toOption.value

                navigator.nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
                  .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
            }
          }

          "must go from Declaration For Someone Else page to movement details check your answers page on selecting option 'No'" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(DeclarationForSomeoneElsePage, false).toOption.value

                navigator.nextPage(DeclarationForSomeoneElsePage, CheckMode, updatedUserAnswers)
                  .mustBe(movementDetailsRoute.MovementDetailsCheckYourAnswersController.onPageLoad(answers.id))
            }
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
      "Transport Details section" - {

        "in Normal Mode" - {
          "must go from InlandMode page to AddIdAtDeparture Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(InlandModePage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.AddIdAtDepartureController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from AddIdAtDeparture page to AddIdAtDepartureLater page when user selects 'no' " in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers.set(AddIdAtDeparturePage, false).toOption.value

                navigator.nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.AddIdAtDepartureLaterController.onPageLoad(updatedAnswers.id))
            }
          }

          "must go from AddIdAtDeparture page to IdAtDeparture page when user selects 'yes' " in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers.set(AddIdAtDeparturePage, true).toOption.value
                  .remove(IdAtDeparturePage).success.value

                navigator.nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(updatedAnswers.id, NormalMode))
            }
          }

          "must go from AddIdAtDepartureLater page to NationalityAtDeparture Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(AddIdAtDepartureLaterPage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.NationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from IdAtDeparture page to NationalityAtDeparture Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(IdAtDeparturePage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.NationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from NationalityAtDeparture page to ChangeAtBorder Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(NationalityAtDeparturePage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from ChangeAtBorder page to TraderDetailsCheckYourAnswers page when user selects 'no' " in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers.set(ChangeAtBorderPage, false).toOption.value

                navigator.nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
            }
          }

          "must go from ChangeAtBorder page to ModeAtBorder page when user selects 'yes' " in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers.set(ChangeAtBorderPage, true).toOption.value

                navigator.nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.ModeAtBorderController.onPageLoad(updatedAnswers.id, NormalMode))
            }
          }

          "must go from ModeAtBorder page to IdCrossingBorder Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(ModeAtBorderPage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.IdCrossingBorderController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from IdCrossingBorder page to ModeCrossingBorder Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(IdCrossingBorderPage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.ModeCrossingBorderController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from ModeCrossingBorder page to NationalityCrossingBorder Page" in { //TODO needs refactoring with reference data

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(ModeCrossingBorderPage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.NationalityCrossingBorderController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from NationalityCrossingBorder page to TransportDetailsCheckYourAnswers Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(NationalityCrossingBorderPage, NormalMode, answers)
                  .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
            }
          }

        }

        "in Check Mode" - {

          "must go from InlandMode page to TransportDetailsCheckYourAnswers Page" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator.nextPage(InlandModePage, CheckMode, answers)
                  .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
            }
          }

          "must go from AddIdAtDeparture page to IdAtDeparture page on selecting option 'Yes' and IdAtDeparture has no data " in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(AddIdAtDeparturePage, true).toOption.value
                  .remove(IdAtDeparturePage).success.value

                navigator.nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
                  .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(answers.id, CheckMode))
            }
          }

          "must go from AddIdAtDeparture page to CYA page on selecting option 'Yes' and IdAtDeparture has data" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(AddIdAtDeparturePage, true).toOption.value
                  .set(IdAtDeparturePage, "Bob").toOption.value

                navigator.nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
                  .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
            }
          }


          "must go from AddIdAtDeparture page to AddIdAtDepartureLater on selecting option 'No' and IdAtDeparture has data" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedUserAnswers = answers.set(AddIdAtDeparturePage, false).toOption.value
                  .set(IdAtDeparturePage, "Bob").toOption.value

                navigator.nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
                  .mustBe(transportDetailsRoute.AddIdAtDepartureLaterController.onPageLoad(answers.id))
            }
          }

         }
      }
  }

}