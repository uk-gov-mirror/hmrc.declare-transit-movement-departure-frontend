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

import models.{ConsignorAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ConsignorForAllItemsPageSpec extends PageBehaviours {

  "ConsignorForAllItemsPage" - {

    beRetrievable[Boolean](ConsignorForAllItemsPage)

    beSettable[Boolean](ConsignorForAllItemsPage)

    beRemovable[Boolean](ConsignorForAllItemsPage)
  }

  "cleanup" - {

    "must remove AddConsignorPage, ConsignorAddressPage, ConsignorNamePage and WhatIsConsignorEoriPage when they exist in userAnswers" in {

      val consignorAddress = arbitrary[ConsignorAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddConsignorPage, true)
            .success
            .value
            .set(ConsignorNamePage, "answer")
            .success
            .value
            .set(ConsignorAddressPage, consignorAddress)
            .success
            .value
            .set(IsConsignorEoriKnownPage, true)
            .success
            .value
            .set(ConsignorEoriPage, "GB123456")
            .success
            .value
            .set(ConsignorForAllItemsPage, true)
            .success
            .value

          result.get(AddConsignorPage) must not be defined
          result.get(ConsignorNamePage) must not be defined
          result.get(ConsignorAddressPage) must not be defined
          result.get(ConsignorEoriPage) must not be defined
          result.get(IsConsignorEoriKnownPage) must not be defined

      }
    }
  }
}
