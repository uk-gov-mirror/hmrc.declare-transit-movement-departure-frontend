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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.{Index, UserAnswers}

class ItemSectionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  "ItemSection" - {

    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arb[ItemSection], arb[UserAnswers]) {
          case (itemSection, userAnswers) =>
            val updatedUserAnswer           = ItemSectionSpec.setItemSection(itemSection, index)(userAnswers)
            val result: Option[ItemSection] = UserAnswersReader[ItemSection].run(updatedUserAnswer)

            result.value mustEqual itemSection
        }
      }
    }

    "cannot be parsed" - {
      "when an answer is missing" in {
        forAll(arb[ItemSection], arb[UserAnswers]) {
          case (itemSection, ua) =>
            val userAnswers = ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index)(ua)
            val result      = UserAnswersReader[ItemSection].run(userAnswers)

            result mustBe None
        }
      }
    }
  }
}

object ItemSectionSpec extends UserAnswersSpecHelper {

  def setItemSection(itemSection: ItemSection, index: Index)(startUserAnswers: UserAnswers): UserAnswers =
    (
      ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index) _ andThen
        PackagesSpec.setPackageUserAnswers(itemSection.packages, index)
    )(startUserAnswers)

}
