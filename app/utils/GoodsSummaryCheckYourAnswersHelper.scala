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

import controllers.goodsSummary.routes
import models.{CheckMode, LocalReferenceNumber, UserAnswers}
import pages.{AddCustomsApprovedLocationPage, AuthorisedLocationCodePage, DeclarePackagesPage, TotalGrossMassPage, TotalPackagesPage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

class GoodsSummaryCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def addCustomsApprovedLocation: Option[Row] = userAnswers.get(AddCustomsApprovedLocationPage) map {
    answer =>
      Row(
        key     = Key(msg"addCustomsApprovedLocation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCustomsApprovedLocationController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addCustomsApprovedLocation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def totalGrossMass: Option[Row] = userAnswers.get(TotalGrossMassPage) map {
    answer =>
      Row(
        key     = Key(msg"totalGrossMass.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalGrossMassController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalGrossMass.checkYourAnswersLabel"))
          )
        )
      )
  }

  def authorisedLocationCode: Option[Row] = userAnswers.get(AuthorisedLocationCodePage) map {
    answer =>
      Row(
        key     = Key(msg"authorisedLocationCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AuthorisedLocationCodeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"authorisedLocationCode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def totalPackages: Option[Row] = userAnswers.get(TotalPackagesPage) map {
    answer =>
      Row(
        key     = Key(msg"totalPackages.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(Literal(answer.toString)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalPackagesController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalPackages.checkYourAnswersLabel"))
          )
        )
      )
  }

  def declarePackages: Option[Row] = userAnswers.get(DeclarePackagesPage) map {
    answer =>
      Row(
        key     = Key(msg"declarePackages.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DeclarePackagesController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarePackages.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}
