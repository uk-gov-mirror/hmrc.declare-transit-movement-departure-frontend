/*
 * Copyright 2021 HM Revenue & Customs
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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.{JourneyModelGenerators, ModelGenerators, UserAnswersGenerator}
import models.journeyDomain._
import models.reference.CountryCode
import models.{DeclarationType, EoriNumber, GuaranteeType, Index, NormalMode, ProcedureType, Status}
import org.scalacheck.Gen
import pages._
import pages.guaranteeDetails.GuaranteeTypePage
import pages.safetyAndSecurity.AddCircumstanceIndicatorPage
import play.api.libs.json.{JsObject, Json}

class TaskListViewModelSpec
    extends SpecBase
    with GeneratorSpec
    with JourneyModelGenerators
    with UserAnswersSpecHelper
    with UserAnswersGenerator
    with ModelGenerators {
  import TaskListViewModelSpec._

  val movementSectionName          = "declarationSummary.section.movementDetails"
  val tradersSectionName           = "declarationSummary.section.tradersDetails"
  val transportSectionName         = "declarationSummary.section.transport"
  val routeSectionName             = "declarationSummary.section.routes"
  val addItemsSectionName          = "declarationSummary.section.addItems"
  val goodsSummarySectionName      = "declarationSummary.section.goodsSummary"
  val guaranteeSectionName         = "declarationSummary.section.guarantee"
  val safetyAndSecuritySectionName = "declarationSummary.section.safetyAndSecurity"

  "TaskListViewModelSpec" - {

    "MovementDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(movementSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(movementSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[DeclarationType]) {
            data =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(DeclarationTypePage)(data)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(movementSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {
          forAll(arb[MovementDetails]) {
            movementDetails =>
              val userAnswers = MovementDetailsSpec.setMovementDetails(movementDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(movementSectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[ProcedureType]) {
            procedure =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(procedure)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(movementSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {
          forAll(arb[MovementDetails]) {
            movementDetails =>
              val userAnswers = MovementDetailsSpec.setMovementDetails(movementDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(movementSectionName).value mustEqual expectedHref
          }

        }
      }
    }

    "RouteDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(routeSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(routeSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[CountryCode]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(routeSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {

          forAll(arbitraryRouteDetails(true).arbitrary) {
            sectionDetails =>
              val setSafetyAndSecurity = emptyUserAnswers.unsafeSetVal(AddSecurityDetailsPage)(true)
              val userAnswers          = RouteDetailsSpec.setRouteDetails(sectionDetails)(setSafetyAndSecurity)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(routeSectionName).value mustEqual Status.Completed
          }

        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(routeSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[CountryCode]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(routeSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {

          forAll(arbitraryRouteDetails(true).arbitrary) {
            sectionDetails =>
              val setSafetyAndSecurity = emptyUserAnswers.unsafeSetVal(AddSecurityDetailsPage)(true)

              val userAnswers = RouteDetailsSpec.setRouteDetails(sectionDetails)(setSafetyAndSecurity)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.routeDetails.routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(routeSectionName).value mustEqual expectedHref
          }
        }
      }
    }

    "TransportDetail" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(transportSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(transportSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(Gen.chooseNum(10, 100)) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(InlandModePage)(pageAnswer.toString)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(transportSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {
          forAll(arb[TransportDetails]) {
            sectionDetails =>
              val userAnswers = TransportDetailsSpec.setTransportDetail(sectionDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(transportSectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(transportSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(Gen.chooseNum(10, 100)) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(InlandModePage)(pageAnswer.toString)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(transportSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {
          forAll(arb[TransportDetails]) {
            sectionDetails =>
              val userAnswers = TransportDetailsSpec.setTransportDetail(sectionDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.transportDetails.routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(transportSectionName).value mustEqual expectedHref
          }

        }
      }
    }

    "TraderDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(tradersSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(tradersSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(tradersSectionName).value mustEqual Status.InProgress
          }
        }

        "is InProgress when the first question for the section has been answered for Procedure type 'Simplified'" ignore { //TODO Bug CTCTRADERS-2071
          val eori = arb[EoriNumber].sample.value
          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
            .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)

          val viewModel = TaskListViewModel(userAnswers)

          viewModel.getStatus(tradersSectionName).value mustEqual Status.InProgress
        }

        "is InProgress when the first question for the section has been answered for Procedure type 'Normal'" ignore { //TODO Bug CTCTRADERS-2071
          val eori = arb[EoriNumber].sample.value
          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)

          val viewModel = TaskListViewModel(userAnswers)

          viewModel.getStatus(tradersSectionName).value mustEqual Status.InProgress
        }

        "is Completed when all the answers are completed" in {
          val procedureType = arb[ProcedureType].sample.value
          forAll(arbitraryTraderDetails(procedureType).arbitrary) {
            sectionDetails =>
              val userAnswers = TraderDetailsSpec.setTraderDetails(sectionDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(tradersSectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {
        "when the status is Not started and 'Procedure Type is Normal', links to the first page" in {
          val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
          val viewModel   = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is Not started and 'Procedure Type is Simplified', links to the first page" in {
          val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
          val viewModel   = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.traderDetails.routes.WhatIsPrincipalEoriController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is Not started and 'Procedure Type is Unknown', links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.routes.SessionExpiredController.onPageLoad().url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress and 'Procedure Type is Normal', links to the first page" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(tradersSectionName).value mustEqual expectedHref
          }
        }

        "when the status is InProgress and 'Procedure Type is Simplified', links to the first page" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.traderDetails.routes.WhatIsPrincipalEoriController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(tradersSectionName).value mustEqual expectedHref
          }
        }

        "when the status is InProgress and 'Procedure Type is Unknown', links to the first page" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(IsPrincipalEoriKnownPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.routes.SessionExpiredController.onPageLoad().url

              viewModel.getHref(tradersSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {
          val procedureType = arb[ProcedureType].sample.value
          forAll(arbitraryTraderDetails(procedureType).arbitrary) {
            sectionDetails =>
              val userAnswers = TraderDetailsSpec.setTraderDetails(sectionDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.traderDetails.routes.TraderDetailsCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(tradersSectionName).value mustEqual expectedHref
          }

        }
      }
    }

    "SecurityDetails" - {

      "section task" - {
        "is included when user has chosen to add Security Details" in {
          val useranswers = emptyUserAnswers.unsafeSetVal(AddSecurityDetailsPage)(true)

          val viewModel = TaskListViewModel(useranswers)

          viewModel.getSection(safetyAndSecuritySectionName) must be(defined)
        }

        "is not included when user has chosen to not add Security Details" in {
          val useranswers = emptyUserAnswers.unsafeSetVal(AddSecurityDetailsPage)(false)

          val viewModel = TaskListViewModel(useranswers)

          viewModel.getSection(safetyAndSecuritySectionName) must not be defined
        }
      }

      "status when section is required" - {
        "is Not started when there are no answers for the section" in {
          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddSecurityDetailsPage)(true)

          val viewModel = TaskListViewModel(userAnswers)

          viewModel.getStatus(safetyAndSecuritySectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(safetyAndSecuritySectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {
          forAll(arb[SafetyAndSecurity]) {
            sectionDetails =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
              val updatedUserAnswers = SafetyAndSecuritySpec.setSafetyAndSecurity(sectionDetails)(userAnswers)

              val viewModel = TaskListViewModel(updatedUserAnswers)

              viewModel.getStatus(safetyAndSecuritySectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation when section is required" - {
        "when the status is Not started, links to the first page" in {
          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddSecurityDetailsPage)(true)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(safetyAndSecuritySectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(safetyAndSecuritySectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {
          forAll(arb[SafetyAndSecurity]) {
            sectionDetails =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
              val updatedUserAnswers = SafetyAndSecuritySpec.setSafetyAndSecurity(sectionDetails)(userAnswers)

              val viewModel = TaskListViewModel(updatedUserAnswers)

              val expectedHref: String = controllers.safetyAndSecurity.routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(safetyAndSecuritySectionName).value mustEqual expectedHref
          }
        }
      }
    }

    "ItemsDetails" - {
      val zeroIndex = Index(0)

      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(addItemsSectionName) must be(defined)
      }

      "status" - {

        "is Cannot start yet when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(addItemsSectionName).value mustEqual Status.CannotStartYet
        }

        "is Not started when there are no answers for the section" in {
          val procedureType = arb[ProcedureType].sample.value
          forAll(arbitraryTraderDetails(procedureType).arbitrary) {
            sectionDetails =>
              val userAnswers = TraderDetailsSpec.setTraderDetails(sectionDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(addItemsSectionName).value mustEqual Status.NotStarted
          }
        }

        "is InProgress when the first question for the section has been answered" in {
          val procedureType = arb[ProcedureType].sample.value
          val traderDetails = arbitraryTraderDetails(procedureType).arbitrary.sample.value
          forAll(stringsWithMaxLength(stringMaxLength)) {
            pageAnswer =>
              val userAnswers        = TraderDetailsSpec.setTraderDetails(traderDetails)(emptyUserAnswers)
              val updatedUserAnswers = userAnswers.unsafeSetVal(ItemDescriptionPage(zeroIndex))(pageAnswer)

              val viewModel = TaskListViewModel(updatedUserAnswers)

              viewModel.getStatus(addItemsSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" ignore {
          forAll(arb[ItemSection]) {
            sectionDetails =>
              val userAnswers = ItemSectionSpec.setItemSection(sectionDetails, zeroIndex)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(addItemsSectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {

        "when the status is Cannot start yet with links disable until trader details section is complete" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.addItems.routes.ItemDescriptionController.onPageLoad(lrn, zeroIndex, NormalMode).url

          viewModel.getHref(addItemsSectionName).value.isEmpty mustEqual true
        }

        "when the status is Not started, links to the first page" in {
          val procedureType = arb[ProcedureType].sample.value
          val traderDetails = arbitraryTraderDetails(procedureType).arbitrary.sample.value
          val userAnswers   = TraderDetailsSpec.setTraderDetails(traderDetails)(emptyUserAnswers)
          val viewModel     = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.addItems.routes.ItemDescriptionController.onPageLoad(lrn, zeroIndex, NormalMode).url

          viewModel.getHref(addItemsSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          val procedureType = arb[ProcedureType].sample.value
          val traderDetails = arbitraryTraderDetails(procedureType).arbitrary.sample.value
          val userAnswers   = TraderDetailsSpec.setTraderDetails(traderDetails)(emptyUserAnswers)
          forAll(stringsWithMaxLength(stringMaxLength)) {
            pageAnswer =>
              val updatedUserAnswers = userAnswers.unsafeSetVal(ItemDescriptionPage(zeroIndex))(pageAnswer)

              val viewModel = TaskListViewModel(updatedUserAnswers)

              val expectedHref: String = controllers.addItems.routes.ItemDescriptionController.onPageLoad(lrn, zeroIndex, NormalMode).url

              viewModel.getHref(addItemsSectionName).value mustEqual expectedHref
          }
        }

        // TODO: to be uncommmented when section is complete
        "when the status is Completed, links to the Check your answers page for the section" ignore {
          forAll(arb[ItemSection]) {
            sectionDetails =>
              val userAnswers = ItemSectionSpec.setItemSection(sectionDetails, zeroIndex)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.addItems.routes.AddAnotherItemController.onPageLoad(lrn).url

              viewModel.getHref(addItemsSectionName).value mustEqual expectedHref
          }

        }
      }
    }

    "GoodsSummaryDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(goodsSummarySectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(DeclarePackagesPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {

          val isSecurityDefined = arb[Boolean].sample.value

          forAll(arb(arbitraryGoodsSummary(isSecurityDefined))) {
            sectionDetails =>
              val userAnswers = GoodsSummarySpec
                .setGoodsSummary(sectionDetails)(emptyUserAnswers)
                .unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.goodsSummary.routes.DeclarePackagesController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[Boolean]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(DeclarePackagesPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.goodsSummary.routes.DeclarePackagesController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {
          val isSecurityDefined = arb[Boolean].sample.value

          forAll(arb(arbitraryGoodsSummary(isSecurityDefined))) {
            sectionDetails =>
              val userAnswers = GoodsSummarySpec
                .setGoodsSummary(sectionDetails)(emptyUserAnswers)
                .unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.goodsSummary.routes.GoodsSummaryCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
          }
        }
      }
    }

    "GuranteeDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(guaranteeSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(guaranteeSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[GuaranteeType]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(GuaranteeTypePage(index))(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(guaranteeSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed for the first Item" in {
          forAll(nonEmptyListOf[GuaranteeDetails](1)) {
            sectionDetails =>
              val userAnswers = GuaranteeDetailsSpec.setGuaranteeDetails(sectionDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(guaranteeSectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, index, NormalMode).url

          viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[GuaranteeType]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(GuaranteeTypePage(index))(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, index, NormalMode).url

              viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the add another guarantee page" in {
          forAll(nonEmptyListOf[GuaranteeDetails](2)) {
            sectionDetails =>
              val userAnswers = GuaranteeDetailsSpec.setGuaranteeDetails(sectionDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(lrn).url

              viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
          }
        }
      }
    }
  }
}

object TaskListViewModelSpec {

  implicit class TaskListViewModelSpecHelper(vm: TaskListViewModel) {

    def getSection(sectionName: String): Option[JsObject] =
      Json
        .toJson(vm)
        .as[List[JsObject]]
        .find(
          section => (section \ "name").as[String] == sectionName
        )
        .map(_.as[JsObject])

    def getStatus(sectionName: String): Option[Status] =
      getSection(sectionName: String).map(
        section => (section \ "status").as[Status]
      )

    def getHref(sectionName: String): Option[String] =
      getSection(sectionName: String).map(
        section => (section \ "href").as[String]
      )

  }
}
