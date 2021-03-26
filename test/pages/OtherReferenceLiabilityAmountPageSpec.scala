/*
 * Copyright 2021 HM Revenue & Customs
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

package pages

import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class OtherReferenceliabilityAmountPageSpec extends PageBehaviours {

  "OtherReferenceliabilityAmountPage" - {

    beRetrievable[String](OtherReferenceLiabilityAmountPage(Index(0)))

    beSettable[String](OtherReferenceLiabilityAmountPage(Index(0)))

    beRemovable[String](OtherReferenceLiabilityAmountPage(Index(0)))

    "cleanup logic" - {

      "must remove DefaultAmount page when value is not empty" in {

        val index = Index(0)

        forAll(arbitrary[UserAnswers], nonEmptyString, arbitrary[Boolean]) {
          (userAnswers, nonEmptyValue, useDefaultAmount) =>
            val result = userAnswers
              .set(DefaultAmountPage(index), useDefaultAmount)
              .success
              .value
              .set(OtherReferenceLiabilityAmountPage(index), nonEmptyValue)
              .success
              .value

            result.get(DefaultAmountPage(index)) must not be defined
        }
      }

      "must not remove DefaultAmount page when value is empty" in {

        val index = Index(0)

        forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
          (userAnswers, useDefaultAmount) =>
            val result = userAnswers
              .set(DefaultAmountPage(index), useDefaultAmount)
              .success
              .value
              .set(OtherReferenceLiabilityAmountPage(index), "")
              .success
              .value

            result.get(DefaultAmountPage(index)) must be(defined)
        }
      }
    }
  }

}
