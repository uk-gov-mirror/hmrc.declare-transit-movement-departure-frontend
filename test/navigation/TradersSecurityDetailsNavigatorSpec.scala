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
import controllers.addItems.traderSecurityDetails.routes
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.AddSafetyAndSecurityConsigneePage

class TradersSecurityDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TradersSecurityDetailsNavigator

  "In Normal mode" - {

    "Must go from AddSecurityConsignorEoriPage to SecurityConsignorEoriPage when user selects yes" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsignorsEoriPage(index), true)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsignorsEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.SecurityConsignorEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from AddSecurityConsignorEoriPage to SecurityConsignorNamePage when user selects no" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsignorsEoriPage(index), false)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsignorsEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.SecurityConsignorNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsignorNamePage to SecurityConsignorAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsignorNamePage(index), NormalMode, answers)
            .mustBe(routes.SecurityConsignorAddressController.onPageLoad(answers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsignorAddressPage to AddSecurityConsigneeEoriPage if there is not 1 consignee for all items" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneePage, false)
            .success
            .value
          navigator
            .nextPage(SecurityConsignorAddressPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsignorAddressPage to ItemsCheckYourAnswer page if there is 1 consignee for all items" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneePage, true)
            .success
            .value
          navigator
            .nextPage(SecurityConsignorAddressPage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
      }
    }

    "Must go from SecurityConsignorEoriPage to AddSecurityConsigneeEoriPage if there is not 1 consignee for all items" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneePage, false)
            .success
            .value
          navigator
            .nextPage(SecurityConsignorEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsignorEoriPage to ItemsCheckYourAnswer page if there is 1 consignee for all items" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsigneePage, true)
            .success
            .value
          navigator
            .nextPage(SecurityConsignorEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
      }
    }

    "Must go from AddSecurityConsigneeEoriPage to SecurityConsigneeEoriPage when user selects yes" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsigneesEoriPage(index), true)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsigneesEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from AddSecurityConsigneeEoriPage to SecurityConsigneeNamePage when user selects no" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSecurityConsigneesEoriPage(index), false)
            .success
            .value
          navigator
            .nextPage(AddSecurityConsigneesEoriPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.SecurityConsigneeNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsigneeNamePage to SecurityConsigneeAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsigneeNamePage(index), NormalMode, answers)
            .mustBe(routes.SecurityConsigneeAddressController.onPageLoad(answers.id, index, NormalMode))
      }
    }

    "Must go from SecurityConsigneeAddressPage to AddItemsCheckYourAnswers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsigneeAddressPage(index), NormalMode, answers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
      }
    }

    "Must go from SecurityConsigneeEoriPage to AddItemsCheckYourAnswers page" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(SecurityConsigneeEoriPage(index), NormalMode, answers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
      }
    }

  }
}
