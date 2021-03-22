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

import cats.data.{NonEmptyList, ReaderT}
import cats.implicits._
import models.ProcedureType.{Normal, Simplified}
import models.journeyDomain.{UserAnswersReader, _}
import models.{Index, NormalMode, ProcedureType, SectionDetails, UserAnswers}
import pages._
import pages.guaranteeDetails.GuaranteeTypePage
import pages.safetyAndSecurity.AddCircumstanceIndicatorPage
import play.api.libs.json._

private[viewModels] class TaskListViewModel(userAnswers: UserAnswers) {
  import TaskListViewModel.fromUserAnswersParser

  private val lrn         = userAnswers.id
  private val taskListDsl = new TaskListDslCollectSectionName(userAnswers)

  private val movementDetails =
    taskListDsl
      .sectionName("declarationSummary.section.movementDetails")
      .ifNotDependentOnOtherSectionCompletion
      .ifCompleted(
        UserAnswersReader[MovementDetails],
        controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url
      )
      .ifInProgress(
        DeclarationTypePage.reader,
        controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url
      )
      .ifNotStarted(controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url)
      .section

  private val routeDetails =
    taskListDsl
      .sectionName("declarationSummary.section.routes")
      .ifNotDependentOnOtherSectionCompletion
      .ifCompleted(
        UserAnswersReader[RouteDetails],
        controllers.routeDetails.routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url
      )
      .ifInProgress(
        CountryOfDispatchPage.reader,
        controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url
      )
      .ifNotStarted(controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url)
      .section

  private val transportDetails =
    taskListDsl
      .sectionName("declarationSummary.section.transport")
      .ifDependentSectionCompleted(UserAnswersReader[MovementDetails])
      .ifCompleted(
        UserAnswersReader[TransportDetails],
        controllers.transportDetails.routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url
      )
      .ifInProgress(
        InlandModePage.reader,
        controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url
      )
      .ifNotStarted(controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url)
      .section

  private def traderDetailsStartPage(procedureType: Option[ProcedureType]): String =
    procedureType match {
      case Some(Normal)     => controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(userAnswers.id, NormalMode).url
      case Some(Simplified) => controllers.traderDetails.routes.WhatIsPrincipalEoriController.onPageLoad(userAnswers.id, NormalMode).url
      case _                => controllers.routes.SessionExpiredController.onPageLoad().url
    }

  private def traderDetailsInProgressReader: UserAnswersReader[_] =
    ProcedureTypePage.reader.flatMap {
      case Normal     => IsPrincipalEoriKnownPage.reader
      case Simplified => WhatIsPrincipalEoriPage.reader.map(_.nonEmpty)
    }

  private val traderDetails =
    taskListDsl
      .sectionName("declarationSummary.section.tradersDetails")
      .ifNotDependentOnOtherSectionCompletion
      .ifCompleted(
        UserAnswersReader[TraderDetails],
        controllers.traderDetails.routes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url
      )
      .ifInProgress(
        traderDetailsInProgressReader,
        traderDetailsStartPage(userAnswers.get(ProcedureTypePage))
      )
      .ifNotStarted(traderDetailsStartPage(userAnswers.get(ProcedureTypePage)))
      .section

  private val addItemsDependentSections: UserAnswersReader[_] = {
    val completedSection = for {
      _            <- UserAnswersReader[MovementDetails]
      _            <- UserAnswersReader[TraderDetails]
      routeDetails <- UserAnswersReader[RouteDetails]
    } yield routeDetails

    if (userAnswers.get(AddSecurityDetailsPage).contains(true)) {
      UserAnswersReader[SafetyAndSecurity]
    } else {
      completedSection
    }
  }

  private val itemDetails =
    taskListDsl
      .sectionName("declarationSummary.section.addItems")
      .ifDependentSectionCompleted(addItemsDependentSections)
      .ifCompleted(
        UserAnswersReader[NonEmptyList[ItemSection]],
        controllers.addItems.routes.AddAnotherItemController.onPageLoad(userAnswers.id).url
      )
      .ifInProgress(
        ItemDescriptionPage(Index(0)).reader,
        controllers.addItems.routes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(0), NormalMode).url
      )
      .ifNotStarted(controllers.addItems.routes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(0), NormalMode).url)
      .section

  private val goodsSummaryDetails =
    taskListDsl
      .sectionName("declarationSummary.section.goodsSummary")
      .ifNotDependentOnOtherSectionCompletion
      .ifCompleted(
        UserAnswersReader[GoodsSummary],
        controllers.goodsSummary.routes.GoodsSummaryCheckYourAnswersController.onPageLoad(lrn).url
      )
      .ifInProgress(
        DeclarePackagesPage.reader,
        controllers.goodsSummary.routes.DeclarePackagesController.onPageLoad(lrn, NormalMode).url
      )
      .ifNotStarted(controllers.goodsSummary.routes.DeclarePackagesController.onPageLoad(lrn, NormalMode).url)
      .section

  private val guaranteeDetails =
    taskListDsl
      .sectionName("declarationSummary.section.guarantee")
      .ifDependentSectionCompleted(UserAnswersReader[RouteDetails])
      .ifCompleted(
        UserAnswersReader[NonEmptyList[GuaranteeDetails]],
        controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(lrn).url
      )
      .ifInProgress(
        GuaranteeTypePage(Index(0)).reader,
        controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, Index(0), NormalMode).url
      )
      .ifNotStarted(controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, Index(0), NormalMode).url)
      .section

  private val safetyAndSecurityDetails: Seq[SectionDetails] =
    userAnswers
      .get(AddSecurityDetailsPage)
      .map({
        case true =>
          Seq(
            taskListDsl
              .sectionName("declarationSummary.section.safetyAndSecurity")
              .ifDependentSectionCompleted(UserAnswersReader[TransportDetails])
              .ifCompleted(
                UserAnswersReader[SafetyAndSecurity],
                controllers.safetyAndSecurity.routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(lrn).url
              )
              .ifInProgress(
                AddCircumstanceIndicatorPage.reader,
                controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url
              )
              .ifNotStarted(controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url)
              .section
          )

        case _ => Seq.empty
      })
      .getOrElse(Seq.empty)

  private val sections: Seq[SectionDetails] =
    Seq(
      movementDetails,
      routeDetails,
      traderDetails,
      transportDetails
    ) ++ safetyAndSecurityDetails ++ Seq(
      itemDetails,
      goodsSummaryDetails,
      guaranteeDetails
    )
}

object TaskListViewModel {

  // TODO: This is a workaround till we remove UserAnswersParser
  implicit def fromUserAnswersParser[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersReader[A] =
    ReaderT[Option, UserAnswers, A](parser.run _)

  object Constants {
    val sections: String = "sections"
  }

  def apply(userAnswers: UserAnswers): TaskListViewModel = new TaskListViewModel(userAnswers)

  implicit val writes: Writes[TaskListViewModel] =
    taskListViewModel => Json.toJson(taskListViewModel.sections)

}
