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

import derivable.DeriveNumberOfSeals
import models.{Index, NormalMode, UserAnswers}
import utils.{AddSealHelper, GoodsSummaryCheckYourAnswersHelper}
import viewModels.sections.Section
import uk.gov.hmrc.viewmodels.SummaryList


case class GoodsSummaryCheckYourAnswersViewModel(sections: Seq[Section])

object GoodsSummaryCheckYourAnswersViewModel {


  def apply(userAnswers: UserAnswers): GoodsSummaryCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new GoodsSummaryCheckYourAnswersHelper(userAnswers)
    val addSealHelper = new AddSealHelper(userAnswers)

    val declarePackages: Option[SummaryList.Row] = checkYourAnswersHelper.declarePackages
    val totalPackages: Option[SummaryList.Row] = checkYourAnswersHelper.totalPackages
    val totalGrossMass: Option[SummaryList.Row] = checkYourAnswersHelper.totalGrossMass
    val authorisedLocationCode: Option[SummaryList.Row] = checkYourAnswersHelper.authorisedLocationCode
    val controlResultDateLimit: Option[SummaryList.Row] = checkYourAnswersHelper.controlResultDateLimit
    val addCustomsApprovedLocation: Option[SummaryList.Row] = checkYourAnswersHelper.addCustomsApprovedLocation
    val customsApprovedLocation: Option[SummaryList.Row] = checkYourAnswersHelper.customsApprovedLocation
    val addSeals: Option[SummaryList.Row] = checkYourAnswersHelper.addSeals

    val seals = addSealHelper.sealsRow(userAnswers.id, NormalMode)


      /*
       val numberOfSeals    = userAnswers.get(DeriveNumberOfSeals(eventIndex)).getOrElse(0)
    val listOfSealsIndex = List.range(0, numberOfSeals).map(Index(_))
    val seals = listOfSealsIndex.flatMap {
      index =>
        helper.sealIdentity(eventIndex, index)
    }

    Section(msg"addSeal.sealList.heading", (helper.haveSealsChanged(eventIndex) ++ seals).toSeq)
       */

//    val sealIdDetails: Option[SummaryList.Row] = addSealHelper.sealRow(userAnswers.id,sealIndex, NormalMode )
    val sealsInformation: Option[SummaryList.Row] = checkYourAnswersHelper.sealsInformation

val secrti = Seq(
  declarePackages,
  totalPackages,
  totalGrossMass,
  authorisedLocationCode,
  controlResultDateLimit,
  addCustomsApprovedLocation,
  customsApprovedLocation,
  addSeals,
  seals,
  sealsInformation
).flatten

     GoodsSummaryCheckYourAnswersViewModel(Seq(Section(secrti)))
  }
}