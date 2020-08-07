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
      val sectionName = messages("declarationSummary.section.movementDetails", "Add")
      val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

      val result = sectionsHelper.getSections

      result mustBe expectedSections
    }

    "must return movement details section with status as InProgress" in {

      val userAnswers = emptyUserAnswers.set(DeclarationTypePage, DeclarationType.values.head).toOption.value
        .set(ProcedureTypePage, ProcedureType.values.head).toOption.value

      val url = routes.ContainersUsedPageController.onPageLoad(lrn, NormalMode).url
      val section = SectionDetails(messages("declarationSummary.section.movementDetails", "Add"), url, InProgress)
      val expectedResult = updateSectionsWithExpectedValue(section)

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
      val section = SectionDetails(messages("declarationSummary.section.movementDetails", "Edit"), url, Completed)
      val expectedResult = updateSectionsWithExpectedValue(section)

      val sectionsHelper = new SectionsHelper(userAnswers)
      val result = sectionsHelper.getSections

      result mustBe expectedResult
    }
  }

  private def updateSectionsWithExpectedValue(sectionDtls: SectionDetails): Seq[SectionDetails] = {
     val sections: Seq[SectionDetails] = Seq(
      SectionDetails(messages("declarationSummary.section.movementDetails", "Add"), "", NotStarted),
      SectionDetails(messages("declarationSummary.section.routes", "Add"), "", NotStarted),
      SectionDetails(messages("declarationSummary.section.transport", "Add"), "", NotStarted),
      SectionDetails(messages("declarationSummary.section.tradersDetails", "Add"), "", NotStarted),
      SectionDetails(messages("declarationSummary.section.goodsSummary", "Add"), "", NotStarted),
      SectionDetails(messages("declarationSummary.section.guarantee", "Add"), "", NotStarted)
    )
    sections.map {
      section =>
        if (section.name == sectionDtls.name) sectionDtls else section
    }
  }

}

