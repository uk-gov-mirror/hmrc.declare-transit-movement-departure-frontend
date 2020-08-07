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

import base.SpecBase
import controllers.routes
import models.Status.{Completed, InProgress, NotStarted}
import models.{DeclarationType, NormalMode, ProcedureType, SectionDetails}
import pages._

class SectionsHelperSpec extends SpecBase {

  "SectionsHelper" - {
    "must return movement details section with status as NotStarted" in {
      val sectionsHelper = new SectionsHelper(emptyUserAnswers)

      val url = routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url
      val expectedResult = List(SectionDetails(messages("declarationSummary.section.addMovementDetails", "Add"), url, NotStarted))
      val result = sectionsHelper.getSections

      result mustBe expectedResult
    }

    "must return movement details section with status as InProgress" in {

      val userAnswers = emptyUserAnswers.set(DeclarationTypePage, DeclarationType.values.head).toOption.value
        .set(ProcedureTypePage, ProcedureType.values.head).toOption.value

      val url = routes.ContainersUsedPageController.onPageLoad(lrn, NormalMode).url
      val expectedResult = List(SectionDetails(messages("declarationSummary.section.addMovementDetails", "Add"), url, InProgress))

      val sectionsHelper = new SectionsHelper(userAnswers)
      val result = sectionsHelper.getSections

      result mustBe expectedResult
    }

    "must return movement details section with status as Complete" in {

      val userAnswers = emptyUserAnswers.set(DeclarationTypePage, DeclarationType.values.head).toOption.value
        .set(ProcedureTypePage, ProcedureType.values.head).toOption.value
        .set(ContainersUsedPage, true).toOption.value
        .set(DeclarationPlacePage, "answers").toOption.value
        .set(DeclarationForSomeoneElsePage, true).toOption.value

      val url = routes.CheckYourAnswersController.onPageLoad(lrn).url
      val expectedResult = List(SectionDetails(messages("declarationSummary.section.addMovementDetails", "Edit"), url, Completed))

      val sectionsHelper = new SectionsHelper(userAnswers)
      val result = sectionsHelper.getSections

      result mustBe expectedResult
    }
  }

  lazy val expectedSections: Seq[SectionDetails] = Seq(
    SectionDetails(messages("declarationSummary.section.addMovementDetails", "Add"), "", NotStarted)/*,
    Section(messages("declarationSummary.section.AddRoute", add), "", NotStarted),
    Section(messages("declarationSummary.section.addTransport", add), "", NotStarted),
    Section(messages("declarationSummary.section.addTradersDetails", add), "", NotStarted),
    Section(messages("declarationSummary.section.AddGoodsSummary", add), "", NotStarted),
    Section(messages("declarationSummary.section.AddGoodsSummary", add), "", NotStarted),
    Section(messages("declarationSummary.section.AddGoodsSummary", add), "", NotStarted),*/
  )
}

