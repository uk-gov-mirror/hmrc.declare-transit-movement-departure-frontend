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

import controllers.addItems.routes
import models.{CheckMode, Index, LocalReferenceNumber, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def totalNetMass(index: Index): Option[Row] = userAnswers.get(TotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"totalNetMass.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNetMass.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addTotalNetMass(index: Index): Option[Row] = userAnswers.get(AddTotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"addTotalNetMass.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTotalNetMass.checkYourAnswersLabel"))
          )
        )
      )
  }

  def itemTotalGrossMass(index: Index): Option[Row] = userAnswers.get(ItemTotalGrossMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemTotalGrossMass.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemTotalGrossMass.checkYourAnswersLabel"))
          )
        )
      )
  }

  def itemDescription(index: Index): Option[Row] = userAnswers.get(ItemDescriptionPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemDescription.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemDescription.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id

}
