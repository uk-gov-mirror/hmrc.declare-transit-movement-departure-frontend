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
import pages.addItems.traderSecurityDetails.{AddSecurityConsignorsEoriPage, SecurityConsignorNamePage}

class TradersSecurityDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new SecurityDetailsNavigator

  "In Normal mode" - {

    "Must go from AddSecurityConsignorEoriPage to ConsignorEoriPage when user selects yes" in {
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

    "Must go from AddSecurityConsignorEoriPage to ConsignorNamePage when user selects no" in {
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
  }
}
