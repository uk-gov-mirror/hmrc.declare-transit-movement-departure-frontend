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

package pages

import models.{ConsigneeAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsConsigneeEoriKnownPageSpec extends PageBehaviours {

  "IsConsigneeEoriKnownPage" - {

    beRetrievable[Boolean](IsConsigneeEoriKnownPage)

    beSettable[Boolean](IsConsigneeEoriKnownPage)

    beRemovable[Boolean](IsConsigneeEoriKnownPage)
  }

  "cleanup" - {

    "must remove ConsigneeAddressPage and ConsigneeNamePage when there is a change of the answer to 'Yes'" in {

      val consigneeAddress = arbitrary[ConsigneeAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(ConsigneeNamePage, "answer")
            .success
            .value
            .set(ConsigneeAddressPage, consigneeAddress)
            .success
            .value
            .set(IsConsigneeEoriKnownPage, true)
            .success
            .value

          result.get(ConsigneeNamePage) must not be defined
          result.get(ConsigneeAddressPage) must not be defined
      }
    }

    "must remove ConsigneeEoriPage when there is a change of the answer to 'No'" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(WhatIsConsigneeEoriPage, "GB123456")
            .success
            .value
            .set(IsConsigneeEoriKnownPage, false)
            .success
            .value

          result.get(WhatIsConsigneeEoriPage) must not be defined
      }
    }
  }
}
