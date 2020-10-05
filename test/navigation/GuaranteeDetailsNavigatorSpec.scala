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
import controllers.guaranteeDetails.{routes => guaranteeDetailsRoute}
import generators.Generators
import models.GuaranteeType.{ComprehensiveGuarantee, GuaranteeWaiver}
import models.{NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails._



class GuaranteeDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new GuaranteeDetailsNavigator

  "GuaranteeDetailsNavigator" - {

    "in normal mode" - {

      "must go from GuaranteeTypePage" - {

        "to GuaranteeReferenceNumberPage when user selects 0,1,2,4 or 9" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
       //       val updatedAnswers = answers.set(GuaranteeTypePage, Gen.oneOf(GuaranteeWaiver)).toOption.value

              navigator
                .nextPage(GuaranteeTypePage, NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

          "to OtherReferencePage when user selects anything but 0,1,2,4 or 9" in {

          }
        }
      }

  }
}
