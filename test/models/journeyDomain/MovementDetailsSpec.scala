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

import base.{GeneratorSpec, SpecBase}
import models.UserAnswers
import pages._

class MovementDetailsSpec extends SpecBase with GeneratorSpec {

  "SimplifiedMovementDetails" - {
    "ParseUserAnswers parseNoDetails" - {

      "can be constructed when all the answers have been answered" in {
        forAll(arbitrary[SimplifiedMovementDetails], arbitrary[UserAnswers]) {
          (expected, baseUserAnswers) =>
            val interstitialUserAnswers = baseUserAnswers
              .set(DeclarationTypePage, expected.declarationType)
              .toOption
              .value
              .set(ContainersUsedPage, expected.containersUsed)
              .toOption
              .value
              .set(DeclarationPlacePage, expected.declarationPlacePage)
              .toOption
              .value
              .set(DeclarationForSomeoneElsePage, expected.declarationForSomeoneElse != DeclarationForSelf)
              .toOption
              .value

            val userAnswers = expected.declarationForSomeoneElse match {
              case DeclarationForSelf =>
                interstitialUserAnswers
              case DeclarationForSomeoneElse(companyName, capacity) =>
                interstitialUserAnswers
                  .set(RepresentativeNamePage, companyName)
                  .toOption
                  .value
                  .set(RepresentativeCapacityPage, capacity)
                  .toOption
                  .value
            }

            val result = UserAnswersParser[Option, SimplifiedMovementDetails].run(userAnswers).value

            result mustEqual expected

        }

      }
    }

  }
}
