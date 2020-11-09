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

package utils

import base.SpecBase
import models.SpecialMentionList
import models.reference.SpecialMention
import pages.addItems.specialMentions.SpecialMentionTypePage
import uk.gov.hmrc.viewmodels.Text.Message

class SpecialMentionsCheckYourAnswersSpec extends SpecBase {

  "SpecialMentionsCheckYourAnswers" - {

    "specialMentionType" in {

      val specialMentionList = SpecialMentionList(
        Seq(
          SpecialMention("10600", "Negotiable Bill of lading 'to order blank endorsed'"),
          SpecialMention("30400", "RET-EXP – Copy 3 to be returned")
        )
      )

      val userAnswers = emptyUserAnswers
        .set(SpecialMentionTypePage(itemIndex, referenceIndex), "10600")
        .toOption
        .value

      val checkYourAnswers = new SpecialMentionsCheckYourAnswers(userAnswers)

      val row = checkYourAnswers.specialMentionType(itemIndex, referenceIndex, specialMentionList)

      row.value.key.content mustBe Message("(10600) Negotiable Bill of lading 'to order blank endorsed'")
    }
  }
}
