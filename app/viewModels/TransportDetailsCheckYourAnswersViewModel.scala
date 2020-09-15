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

package viewModels

import models.{CountryList, TransportModeList, UserAnswers}
import uk.gov.hmrc.viewmodels.SummaryList
import utils.TransportDetailsCheckYourAnswersHelper
import viewModels.sections.Section

case class TransportDetailsCheckYourAnswersViewModel(sections: Seq[Section])

object TransportDetailsCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, countryList: CountryList, transportModeList: TransportModeList): TransportDetailsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new TransportDetailsCheckYourAnswersHelper (userAnswers)

    //TODO: Need to add all rows here (see the createSections from TransportDetailsCheckYourAnswersController
    val modeCrossingBorder: Option[SummaryList.Row] = checkYourAnswersHelper.modeCrossingBorder(transportModeList)



    val inlandMode: Option[SummaryList.Row] = checkYourAnswersHelper.inlandMode(transportModeList)

//    checkYourAnswersHelper.inlandMode(transportModeList),
//    checkYourAnswersHelper.addIdAtDeparture,
//    checkYourAnswersHelper.idAtDeparture,
//    checkYourAnswersHelper.nationalityAtDeparture(countryList),
//    checkYourAnswersHelper.changeAtBorder,
//    checkYourAnswersHelper.modeAtBorder(transportModeList),
//    checkYourAnswersHelper.idCrossingBorder,
//    checkYourAnswersHelper.modeCrossingBorder(transportModeList),
//    checkYourAnswersHelper.nationalityCrossingBorder(countryList)

    TransportDetailsCheckYourAnswersViewModel(Seq(Section(Seq(inlandMode, modeCrossingBorder).flatten)))
  }
}
