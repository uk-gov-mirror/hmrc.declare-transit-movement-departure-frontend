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

import controllers.transportDetails.routes
import models.{CheckMode, CountryList, LocalReferenceNumber, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class TransportDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def modeAtBorder: Option[Row] = userAnswers.get(ModeAtBorderPage) map {
    answer =>
      Row(
        key     = Key(msg"modeAtBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ModeAtBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeAtBorder.checkYourAnswersLabel"))
          )
        )
      )
  }

  def modeCrossingBorder: Option[Row] = userAnswers.get(ModeCrossingBorderPage) map {
    answer =>
      Row(
        key     = Key(msg"modeCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ModeCrossingBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeCrossingBorder.checkYourAnswersLabel"))
          )
        )
      )
  }

  def inlandMode: Option[Row] = userAnswers.get(InlandModePage) map {
    answer =>
      Row(
        key     = Key(msg"inlandMode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.InlandModeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"inlandMode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def idCrossingBorder: Option[Row] = userAnswers.get(IdCrossingBorderPage) map {
    answer =>
      Row(
        key     = Key(msg"idCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IdCrossingBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idCrossingBorder.checkYourAnswersLabel"))
          )
        )
      )
  }

  def nationalityAtDeparture(codeList: CountryList): Option[Row] = userAnswers.get(NationalityAtDeparturePage) map {
    answer =>
      val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

      Row(
        key     = Key(msg"nationalityAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$countryName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.NationalityAtDepartureController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityAtDeparture.checkYourAnswersLabel"))
          )
        )
      )
  }

  def nationalityCrossingBorder(codeList: CountryList): Option[Row] = userAnswers.get(NationalityCrossingBorderPage) map {
    answer =>
      val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

      Row(
        key     = Key(msg"nationalityCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$countryName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.NationalityCrossingBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityCrossingBorder.checkYourAnswersLabel"))
          )
        )
      )
  }

  def idAtDeparture: Option[Row] = userAnswers.get(IdAtDeparturePage) map {
    answer =>
      Row(
        key     = Key(msg"idAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IdAtDepartureController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idAtDeparture.checkYourAnswersLabel"))
          )
        )
      )
  }

  def changeAtBorder: Option[Row] = userAnswers.get(ChangeAtBorderPage) map {
    answer =>
      Row(
        key     = Key(msg"changeAtBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ChangeAtBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeAtBorder.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addIdAtDeparture: Option[Row] = userAnswers.get(AddIdAtDeparturePage) map {
    answer =>
      Row(
        key     = Key(msg"addIdAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddIdAtDepartureController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addIdAtDeparture.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}


