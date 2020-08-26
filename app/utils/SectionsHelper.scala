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
import pages.{RepresentativeNamePage, _}
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

  private def getIncompletePage(startPage: String, pages: Seq[(Option[_], String)]): Option[(String, Status)] = {
    pages.collectFirst {
      case (page, url) if page.isEmpty && url == startPage => (url, NotStarted)
      case (page, url) if page.isEmpty && url != startPage => (url, InProgress)
    }
  }

  private def movementDetailsSection: SectionDetails = {
    val startPage: String = routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (routes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed)
    val (page, status) = getIncompletePage(startPage, movementDetailsPages).getOrElse(cyaPageAndStatus)

    SectionDetails("declarationSummary.section.movementDetails", page, status)
  }

  private def routesSection: SectionDetails = {
    val startPage: String = routes.CountryOfDispatchController.onPageLoad(userAnswers.id, NormalMode).url
    SectionDetails("declarationSummary.section.routes", startPage, NotStarted)
  }

  private def transportSection: SectionDetails = {
    SectionDetails("declarationSummary.section.transport", "", NotStarted)
  }

  private def tradersDetailsSection: SectionDetails = {
    val startPage: String = routes.IsPrincipalEoriKnownController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (routes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed) //TODO specific check your answers
    val (page, status) = getIncompletePage(startPage, traderDetailsPage).getOrElse(cyaPageAndStatus)

    SectionDetails("declarationSummary.section.tradersDetails", page, status)
  }
  private def goodsSummarySection: SectionDetails = {
    SectionDetails("declarationSummary.section.goodsSummary", "", NotStarted)
  }

  private def guaranteeSection: SectionDetails = {
    SectionDetails("declarationSummary.section.guarantee", "", NotStarted)
  }

  private def safetyAnsSecuritySection: SectionDetails = {
    SectionDetails("declarationSummary.section.safetyAndSecurity", "", NotStarted)
  }

  private val movementDetailsPages: Seq[(Option[_], String)] = {
    val lrn = userAnswers.id

    val declareForSomeoneElseDiversionPages = if(userAnswers.get(DeclarationForSomeoneElsePage).contains(true)) {
      Seq(userAnswers.get(RepresentativeNamePage) -> routes.RepresentativeNameController.onPageLoad(lrn, NormalMode).url,
        userAnswers.get(RepresentativeCapacityPage) -> routes.RepresentativeCapacityController.onPageLoad(lrn, NormalMode).url)
    } else {Seq.empty}

    Seq(
      userAnswers.get(DeclarationTypePage) -> routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(ProcedureTypePage) -> routes.ProcedureTypeController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(ContainersUsedPage) -> routes.ContainersUsedPageController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DeclarationPlacePage) -> routes.DeclarationPlaceController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DeclarationForSomeoneElsePage) -> routes.DeclarationForSomeoneElseController.onPageLoad(lrn, NormalMode).url
    ) ++ declareForSomeoneElseDiversionPages

  }

  private val traderDetailsPage: Seq[(Option[_], String)] = {
    val lrn = userAnswers.id

    Seq(
      userAnswers.get(IsPrincipalEoriKnownPage) -> routes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url,
    )
  }

}
