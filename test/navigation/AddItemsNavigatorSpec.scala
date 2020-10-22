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
import controllers.addItems.{routes => addItemsRoutes}
import generators.Generators
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{AddTotalNetMassPage, ItemDescriptionPage, ItemTotalGrossMassPage}

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new AddItemsNavigator

  "Add Items section" - {
    "in normal mode" - {
      "must go from item description page to total gross mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), NormalMode, answers)
              .mustBe(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from total gross mass page to add total net mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemTotalGrossMassPage(index), NormalMode, answers)
              .mustBe(addItemsRoutes.AddTotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from add total net mass page to total net mass page if the answer is 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddTotalNetMassPage(index), true).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(addItemsRoutes.TotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }
    }

    "in check mode" - {
      "must go from item description page to Check Your Answers" - {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), CheckMode, answers)
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total grass mass page to Check Your Answers" - {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemTotalGrossMassPage(index), CheckMode, answers)
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total net mass page to Check Your Answers" - {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, answers)
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }
    }
  }

}
