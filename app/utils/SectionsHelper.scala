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

import controllers.routes
import models.Status.{Completed, InProgress, NotStarted}
import models.{NormalMode, SectionDetails, Status, UserAnswers}
import pages._
import play.api.i18n.Messages

class SectionsHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def getSections: Seq[SectionDetails] = {

    val mandatorySections = Seq(
      movementDetailsSection,
      routesSection,
      transportSection,
      tradersDetailsSection,
      goodsSummarySection,
      guaranteeSection
    )

    if (userAnswers.get(AddSecurityDetailsPage).contains(true)) {
      mandatorySections :+ safetyAnsSecuritySection
    } else {
      mandatorySections
    }
  }

  private def getIncompletePage(startPage: String): Option[(String, Status)] = {
    movementDetailsPages.collectFirst {
      case (page, url) if page.isEmpty && url == startPage => (url, NotStarted)
      case (page, url) if page.isEmpty && url != startPage => (url, InProgress)
    }
  }

  private def movementDetailsSection: SectionDetails = {
    val startPage: String = routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (routes.CheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed) //TODO specific check your answers
    val (page, status) = getIncompletePage(startPage).getOrElse(cyaPageAndStatus)

    SectionDetails(messages("declarationSummary.section.movementDetails",addOrEdit(status)), page, status)
  }

  private def addOrEdit(status: Status): String = if(status == Completed) messages("site.edit") else messages("site.add")

  private def routesSection: SectionDetails = {
    val status = NotStarted
    SectionDetails(messages("declarationSummary.section.routes", addOrEdit(status)), "", status)
  }

  private def transportSection: SectionDetails = {
    val status = NotStarted
    SectionDetails(messages("declarationSummary.section.transport", addOrEdit(status)), "", status)
  }

  private def tradersDetailsSection: SectionDetails = {
    val status = NotStarted
    SectionDetails(messages("declarationSummary.section.tradersDetails", addOrEdit(status)), "", NotStarted)
  }

  private def goodsSummarySection: SectionDetails = {
    val status = NotStarted
    SectionDetails(messages("declarationSummary.section.goodsSummary", addOrEdit(status)), "", status)
  }

  private def guaranteeSection: SectionDetails = {
    val status = NotStarted
    SectionDetails(messages("declarationSummary.section.guarantee", addOrEdit(status)), "", status)
  }

  private def safetyAnsSecuritySection: SectionDetails = {
    val status = NotStarted
    SectionDetails(messages("declarationSummary.section.safetyAndSecurity", addOrEdit(status)), "", status)
  }

  private val movementDetailsPages: Seq[(Option[_], String)] = {
    val lrn = userAnswers.id
    Seq(
      userAnswers.get(DeclarationTypePage) -> routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(ProcedureTypePage) -> routes.ProcedureTypeController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(ContainersUsedPage) -> routes.ContainersUsedPageController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DeclarationPlacePage) -> routes.DeclarationPlaceController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DeclarationForSomeoneElsePage) -> routes.DeclarationForSomeoneElseController.onPageLoad(lrn, NormalMode).url,
    )
  }

}
