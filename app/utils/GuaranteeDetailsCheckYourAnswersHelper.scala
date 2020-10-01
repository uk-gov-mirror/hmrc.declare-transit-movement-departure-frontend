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

import controllers.guaranteeDetails.routes
import models.{CheckMode, LocalReferenceNumber, UserAnswers}
import pages._
import pages.guaranteeDetails.GuaranteeReferencePage
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class GuaranteeDetailsCheckYourAnswersHelper(userAnswers: UserAnswers)  {

  def otherReference: Option[Row] = userAnswers.get(OtherReferencePage) map {
    answer =>
      Row(
        key     = Key(msg"otherReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.OtherReferenceController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"otherReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def guaranteeReference: Option[Row] = userAnswers.get(GuaranteeReferencePage) map {
    answer =>
      Row(
        key     = Key(msg"guaranteeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.GuaranteeReferenceController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"guaranteeReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}

