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

import controllers.routeDetails.routes
import models.{CheckMode, CountryList, CustomsOfficeList, LocalReferenceNumber, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class RouteDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def destinationOffice: Option[Row] = userAnswers.get(DestinationOfficePage) map {
    answer =>
      Row(
        key     = Key(msg"destinationOffice.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DestinationOfficeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"destinationOffice.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addTransitOffice: Option[Row] = userAnswers.get(AddTransitOfficePage) map {
    answer =>
      Row(
        key     = Key(msg"addTransitOffice.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddTransitOfficeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTransitOffice.checkYourAnswersLabel"))
          )
        )
      )
  }

  def officeOfDeparture(customsOfficeList: CustomsOfficeList): Option[Row] = userAnswers.get(OfficeOfDeparturePage) flatMap {
    answer =>
    customsOfficeList.getCustomsOffice(answer) map {
      customsOffice =>
      Row(
        key = Key(msg"officeOfDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"${customsOffice.name} (${customsOffice.id})"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.OfficeOfDepartureController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"officeOfDeparture.checkYourAnswersLabel"))
          )
        )
      )
    }
  }
  def addAnotherTransitOffice(customsOfficeList: CustomsOfficeList): Option[Row] = userAnswers.get(AddAnotherTransitOfficePage) flatMap  {
    answer =>
      customsOfficeList.getCustomsOffice(answer) map{
        customsOffice =>
          Row(
            key     = Key(msg"addAnotherTransitOffice.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value   = Value(lit"${customsOffice.name} ${customsOffice.id}"),
            actions = List(
              Action(
                content            = msg"site.edit",
                href               = routes.AddAnotherTransitOfficeController.onPageLoad(lrn, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAnotherTransitOffice.checkYourAnswersLabel"))
              )
            )
          )
      }
  }

  def countryOfDispatch(codeList: CountryList): Option[Row] = userAnswers.get(CountryOfDispatchPage) map {
    answer =>
      val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

      Row(
        key     = Key(msg"countryOfDispatch.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$countryName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CountryOfDispatchController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"countryOfDispatch.checkYourAnswersLabel"))
          )
        )
      )
  }

  def destinationCountry(codeList: CountryList): Option[Row] = userAnswers.get(DestinationCountryPage) map {
    answer =>
      val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

      Row(
        key     = Key(msg"destinationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$countryName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DestinationCountryController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"destinationCountry.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}


