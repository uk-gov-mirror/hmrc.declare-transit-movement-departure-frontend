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
import controllers.safetyAndSecurity.routes
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck._
import pages.ModeAtBorderPage
import pages.safetyAndSecurity._

class SafetyAndSecurityNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
  val navigator = new SafetyAndSecurityNavigator

  "SafetyAndSecurity section" - {

    "in NormalMode" - {

      "must got from AddCircumstanceIndicator page to CircumstanceIndicator page if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCircumstanceIndicatorPage, true).success.value

            navigator
              .nextPage(AddCircumstanceIndicatorPage, NormalMode, updatedAnswers)
              .mustBe(routes.CircumstanceIndicatorController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must got from AddCircumstanceIndicator to AddTransportChargesPaymentMethod if 'false'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCircumstanceIndicatorPage, false).success.value

            navigator
              .nextPage(AddCircumstanceIndicatorPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddTransportChargesPaymentMethodController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must got from CircumstanceIndicator to AddTransportChargesPaymentMethod" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(CircumstanceIndicatorPage, NormalMode, answers)
              .mustBe(routes.AddTransportChargesPaymentMethodController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must got from AddTransportChargesPaymentMethod to TransportChargesPaymentMethod if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddTransportChargesPaymentMethodPage, true).success.value

            navigator
              .nextPage(AddTransportChargesPaymentMethodPage, NormalMode, updatedAnswers)
              .mustBe(routes.TransportChargesPaymentMethodController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddTransportChargesPaymentMethod to AddCommercialReferenceNumber if 'false'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddTransportChargesPaymentMethodPage, false).success.value

            navigator
              .nextPage(AddTransportChargesPaymentMethodPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddCommercialReferenceNumberController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from TransportChargesPaymentMethod to AddCommercialReferenceNumber" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(TransportChargesPaymentMethodPage, NormalMode, answers)
              .mustBe(routes.AddCommercialReferenceNumberController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumber to AddCommercialReferenceNumberAllItems if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberPage, true).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumber to AddConveyanceReferenceNumber if 'false' and if transport mode at border is not 4 or 40" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberPage, false).success.value
              .set(ModeAtBorderPage, "1").success.value

            navigator
              .nextPage(AddCommercialReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddConveyancerReferenceNumberController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumber to ConveyanceReferenceNumber if 'false' and if transport mode at border 4 or 40" in {

        val genTransportMode: Gen[String] = Gen.oneOf(Seq("4", "40"))

        forAll(arbitrary[UserAnswers], genTransportMode) {
          (answers, transportMode) =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberPage, false).success.value
              .set(ModeAtBorderPage, transportMode).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumberAllItems to CommercialReferenceNumberAllItems when 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberAllItemsPage, true).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.CommercialReferenceNumberAllItemsController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumberAllItems to AddConveyanceReferenceNumber when 'false' and if transport mode at border is not 4 or 40" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
              .set(ModeAtBorderPage, "1").success.value

            navigator
              .nextPage(AddCommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddConveyancerReferenceNumberController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddCommercialReferenceNumberAllItems to ConveyanceReferenceNumber when 'false' and if transport mode at border is 4 or 40" in {

        val genTransportMode: Gen[String] = Gen.oneOf(Seq("4", "40"))

        forAll(arbitrary[UserAnswers], genTransportMode) {
          (answers, transportMode) =>

            val updatedAnswers = answers
              .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
              .set(ModeAtBorderPage, transportMode).success.value

            navigator
              .nextPage(AddCommercialReferenceNumberAllItemsPage, NormalMode, updatedAnswers)
              .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddConveyanceReferenceNumber to ConveyanceReferenceNumber if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddConveyancerReferenceNumberPage, true).success.value

            navigator
              .nextPage(AddConveyancerReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.ConveyanceReferenceNumberController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddConveyanceReferenceNumber to AddPlaceOfUnloadingCode if 'false' and CircumstanceIndicator is '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddConveyancerReferenceNumberPage, false).success.value
              .set(CircumstanceIndicatorPage, "E").success.value

            navigator
              .nextPage(AddConveyancerReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddPlaceOfUnloadingCodeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddConveyanceReferenceNumber to PlaceOfUnloadingCode if 'false' and CircumstanceIndicator is not '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddConveyancerReferenceNumberPage, false).success.value
              .set(CircumstanceIndicatorPage, "A").success.value

            navigator
              .nextPage(AddConveyancerReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.PlaceOfUnloadingCodeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from ConveyanceReferenceNumber to AddPlaceOfUnloadingCode if 'false' and CircumstanceIndicator is '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(ConveyanceReferenceNumberPage, "answer").success.value
              .set(CircumstanceIndicatorPage, "E").success.value

            navigator
              .nextPage(ConveyanceReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.AddPlaceOfUnloadingCodeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from ConveyanceReferenceNumber to PlaceOfUnloadingCode if 'false' and CircumstanceIndicator is not '(E) Authorised Economic Operators'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(ConveyanceReferenceNumberPage, "answer").success.value
              .set(CircumstanceIndicatorPage, "A").success.value

            navigator
              .nextPage(ConveyanceReferenceNumberPage, NormalMode, updatedAnswers)
              .mustBe(routes.PlaceOfUnloadingCodeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddPlaceOfUnloadingCode to PlaceOfUnloadingCode if 'true'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddPlaceOfUnloadingCodePage, true).success.value

            navigator
              .nextPage(AddPlaceOfUnloadingCodePage, NormalMode, updatedAnswers)
              .mustBe(routes.PlaceOfUnloadingCodeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddPlaceOfUnloadingCode to CountryOfRouting if 'false'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers
              .set(AddPlaceOfUnloadingCodePage, false).success.value

            navigator
              .nextPage(AddPlaceOfUnloadingCodePage, NormalMode, updatedAnswers)
              .mustBe(routes.CountryOfRoutingController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from PlaceOfUnloadingCode to CountryOfRouting" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(PlaceOfUnloadingCodePage, NormalMode, answers)
              .mustBe(routes.CountryOfRoutingController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from CountryOfRouting to AddAnotherCountryOfRouting" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(CountryOfRoutingPage, NormalMode, answers)
              .mustBe(routes.AddAnotherCountryOfRoutingController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddAnotherCountryOfRouting to CountryOfRouting if 'true'" in {

      }

      "must go from AddAnotherCountryOfRouting to AddSafetyAndSecurityConsignor if 'false'" in {

      }

      "must go from AddSafetyAndSecurityConsignor to AddSafetyAndSecurityConsignorEori if 'true'" in {

      }

      "must go from AddSafetyAndSecurityConsignor to AddSafetyAndSecurityConsignee if 'false'" in {

      }

      "must go from AddSafetyAndSecurityConsignorEori to SafetyAndSecurityConsignorEori if 'true'" in {

      }

      "must go from AddSafetyAndSecurityConsignorEori to SafetyAndSecurityConsignorName if 'false'" in {

      }

      "must go from SafetyAndSecurityConsignorName to SafetyAndSecurityConsignorAddress" in {

      }

      "must go from SafetyAndSecurityConsignorEori to AddSafetyAndSecurityConsignee" in {

      }

      "must go from AddSafetyAndSecurityConsignee to AddSafetyAndSecurityConsigneeEori if 'true'" in {

      }

      "must go from AddSafetyAndSecurityConsignee to AddCarrier if 'false'" in {

      }

      "must go from AddSafetyAndSecurityConsigneeEori to SafetyAndSecurityConsigneeEori if 'true'" in {

      }

      "must go from AddSafetyAndSecurityConsigneeEori to SafetyAndSecurityConsigneeName if 'false'" in {

      }

      "must go from SafetyAndSecurityConsigneeName to SafetyAndSecurityConsigneeAddress" in {

      }

      "must go from SafetyAndSecurityConsigneeEori to AddCarrier" in {

      }

      "must go from AddCarrier to AddCarrierEori if 'true'" in {

      }

      "must go from AddCarrier to CheckYourAnswers if 'false'" in {

      }

      "must go from AddCarrierEori to CarrierEori if 'true'" in {

      }

      "must go from AddCarrierEori to CarrierName if 'false'" in {

      }

      "must go from CarrierName to CarrierAddress" in {

      }

      "must go from CarrierEori to CheckYourAnswers" in {

      }


    }

    "in CheckMode" - {



    }

  }
  // format: on
}
