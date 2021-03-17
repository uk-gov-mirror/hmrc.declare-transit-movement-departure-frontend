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

package derivable

import base.{SpecBase, UserAnswersSpecHelper}
import models.{Index, UserAnswers}
import pages.AddAnotherTransitOfficePage

class DeriveOfficesOfTransitIdsSpec extends SpecBase with UserAnswersSpecHelper {

  "returns the office ids from user answers" in {

    val userAnswers = emptyUserAnswers
      .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("officeId0")
      .unsafeSetVal(AddAnotherTransitOfficePage(Index(1)))("officeId1")
      .unsafeSetVal(AddAnotherTransitOfficePage(Index(2)))("officeId2")

    val result = userAnswers.get(DeriveOfficesOfTransitIds).value

    result must contain theSameElementsInOrderAs Seq("officeId0", "officeId1", "officeId2")
  }

  "returns None when there are no office ids in user answers" in {

    val userAnswers = emptyUserAnswers

    val result = userAnswers.get(DeriveOfficesOfTransitIds)

    result mustBe None
  }

}
