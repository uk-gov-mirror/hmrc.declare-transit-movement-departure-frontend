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
import controllers.movementDetails.{routes => movementDetailsRoutes}
import controllers.routeDetails.{routes => routeDetailsRoutes}
import models.Status.{Completed, InProgress, NotStarted}
import models.{DeclarationType, NormalMode, ProcedureType, RepresentativeCapacity, SectionDetails}
import pages._

class SectionsHelperSpec extends SpecBase {

  "SectionsHelper" - {
    "MovementDetails" - {
      "must return movement details section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

      val url = movementDetailsRoutes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url
      val sectionName = "declarationSummary.section.movementDetails"
      val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return movement details section with status as InProgress" in {

        val userAnswers = emptyUserAnswers.set(DeclarationTypePage, DeclarationType.values.head).toOption.value
          .set(ProcedureTypePage, ProcedureType.values.head).toOption.value

      val url = movementDetailsRoutes.ContainersUsedPageController.onPageLoad(lrn, NormalMode).url
      val expectedSection = SectionDetails("declarationSummary.section.movementDetails", url, InProgress)
      val expectedResult = updateSectionsWithExpectedValue(expectedSection)

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
          .set(RepresentativeNamePage, "name").toOption.value
          .set(RepresentativeCapacityPage, RepresentativeCapacity.Direct).toOption.value

      val url = movementDetailsRoutes.MovementDetailsCheckYourAnswersController.onPageLoad(lrn).url
      val expectedSection = SectionDetails("declarationSummary.section.movementDetails", url, Completed)
      val expectedResult = updateSectionsWithExpectedValue(expectedSection)

        val sectionsHelper = new SectionsHelper(userAnswers)
        val result = sectionsHelper.getSections

        result mustBe expectedResult
      }
    }

    "Trader Details" - {
      "must return trader's details section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

        val url = routes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url
        val sectionName = "declarationSummary.section.tradersDetails"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return trader's details section with status as In Progress" in {
        val userAnswers = emptyUserAnswers.set(IsPrincipalEoriKnownPage,true).success.value
        val sectionsHelper = new SectionsHelper(userAnswers)

        val url = routes.WhatIsPrincipalEoriController.onPageLoad(lrn, NormalMode).url
        val sectionName = "declarationSummary.section.tradersDetails"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, InProgress))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }
      "must return trader's details section with status as Completed" in {
        val userAnswers = emptyUserAnswers
          .set(IsPrincipalEoriKnownPage,true).success.value
          .set(WhatIsPrincipalEoriPage, "GB123456").success.value
          .set(AddConsignorPage, true).success.value
          .set(IsConsignorEoriKnownPage, true).success.value
          .set(ConsignorEoriPage, "GB123456").success.value
          .set(AddConsigneePage, true).success.value
          .set(IsConsigneeEoriKnownPage, true).success.value
          .set(WhatIsConsigneeEoriPage, "GB123456").success.value


        val sectionsHelper = new SectionsHelper(userAnswers)

        val url = routes.TraderDetailsCheckYourAnswersController.onPageLoad(lrn).url
        val sectionName = "declarationSummary.section.tradersDetails"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, Completed))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }
    }

    "Routes Details" - {
      "must return routes section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

      val url = routeDetailsRoutes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url
      val sectionName = "declarationSummary.section.routes"
      val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }
    }
  }

  private def updateSectionsWithExpectedValue(sectionDtls: SectionDetails): Seq[SectionDetails] = {
     val sections: Seq[SectionDetails] = Seq(
      SectionDetails("declarationSummary.section.movementDetails",movementDetailsRoutes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url, NotStarted),
      SectionDetails("declarationSummary.section.routes", routeDetailsRoutes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url, NotStarted),
      SectionDetails("declarationSummary.section.transport", "", NotStarted),
      SectionDetails("declarationSummary.section.tradersDetails",routes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url, NotStarted),
      SectionDetails("declarationSummary.section.goodsSummary", "", NotStarted),
      SectionDetails("declarationSummary.section.guarantee", "", NotStarted)
    )
    sections.map {
      section =>
        if (section.name == sectionDtls.name) sectionDtls else section
    }
  }
}
