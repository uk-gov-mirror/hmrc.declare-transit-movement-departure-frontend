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

import controllers.addItems.specialMentions.{routes => specialMentionRoutes}
import models.{CheckMode, Index, LocalReferenceNumber, NormalMode, SpecialMentionList, UserAnswers}
import pages.addItems.specialMentions._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class SpecialMentionsCheckYourAnswers(userAnswers: UserAnswers) {

  def specialMentionType(itemIndex: Index, referenceIndex: Index, specialMentionList: SpecialMentionList): Option[Row] =
    userAnswers.get(SpecialMentionTypePage(itemIndex, referenceIndex)) map {
      answer =>
        val answerFormatted = specialMentionList.getSpecialMention(answer).map(x => s"(${x.code}) ${x.description}")

        Row(
          key = Key(
            msg"${answerFormatted.getOrElse(answer)}"
          ),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = specialMentionRoutes.SpecialMentionTypeController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answerFormatted.getOrElse(answer)))
            ),
            Action(
              content            = msg"site.delete",
              href               = specialMentionRoutes.RemoveSpecialMentionController.onPageLoad(userAnswers.id, itemIndex, referenceIndex, NormalMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answerFormatted.getOrElse(answer))),
              attributes         = Map("id" -> s"""remove-special-mentions-${itemIndex.display}""")
            )
          )
        )
    }

  private def lrn: LocalReferenceNumber = userAnswers.id
}
