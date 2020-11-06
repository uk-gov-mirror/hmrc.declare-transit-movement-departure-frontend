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
import controllers.addItems.specialMentions.routes
import generators.Generators
import models.{CheckMode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.specialMentions.{AddAnotherSpecialMentionPage, AddSpecialMentionPage, SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}

class SpecialMentionsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new SpecialMentionsNavigator

  "Special Mentions section" - {

    "in check mode" - {

      "must go from SpecialMentionType to SpecialMentionAdditionalInfo" in {
        navigator
          .nextPage(SpecialMentionTypePage(index, index), CheckMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionAdditionalInfoController.onPageLoad(emptyUserAnswers.id, index, index, CheckMode))
      }

      "must go from SpecialMentionAdditionalInfo to AddAnotherSpecialMention" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, index), CheckMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, NormalMode))
      }
    }

    "in normal mode" - {

      "must go from AddSpecialMention to SpecialMentionType" in {
        navigator
          .nextPage(AddSpecialMentionPage(index), NormalMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionTypeController.onPageLoad(emptyUserAnswers.id, index, index, NormalMode))
      }

      "must go from AddSpecialMention to [PRODUCED DOCUMENTS NOT YET IN]" ignore {}

      "must go from SpecialMentionType to SpecialMentionAdditionalInfo" in {
        navigator
          .nextPage(SpecialMentionTypePage(index, index), NormalMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionAdditionalInfoController.onPageLoad(emptyUserAnswers.id, index, index, NormalMode))
      }

      "must go from SpecialMentionAdditionalInfo to AddAnotherSpecialMention" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, index), NormalMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, NormalMode))
      }

      "must go from AddAnotherSpecialMention" - {

        "to SpecialMentionType when set to true" in {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(index), true).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, index, index, NormalMode))
        }

        "to [PRODUCED DOCUMENTS NOT YET IN] when set to false" ignore {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(index), true).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, index, index, NormalMode))
        }
      }
    }
  }
}
