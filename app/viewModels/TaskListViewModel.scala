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

import cats.data.{NonEmptyList, ReaderT}
import derivable.DeriveNumberOfItems
import models.journeyDomain.TransportDetails.InlandMode
import models.journeyDomain._
import models.{Index, NormalMode, SectionDetails, UserAnswers}
import pages.{CountryOfDispatchPage, InlandModePage, IsPrincipalEoriKnownPage, ItemDescriptionPage, ProcedureTypePage}
import play.api.libs.json._

class TaskListViewModel(userAnswers: UserAnswers) {

  // TODO: This is a workaround till we remove UserAnswersParser
  implicit def fromUserAnswersParser[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersReader[A] =
    ReaderT[Option, UserAnswers, A](parser.run _)

  private val lrn         = userAnswers.id
  private val taskListDsl = new TaskListDslCollectSectionName(userAnswers)

  private val movementDetails =
    taskListDsl
      .sectionName("declarationSummary.section.movementDetails")
      .ifCompleted(
        UserAnswersReader[MovementDetails],
        controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url
      )
      .ifInProgress(
        ProcedureTypePage.reader,
        controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url
      )
      .ifNotStarted(controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url)
      .section

  private val routeDetails =
    taskListDsl
      .sectionName("declarationSummary.section.routes")
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

  private val traderDetails =
    taskListDsl
      .sectionName("declarationSummary.section.tradersDetails")
      .ifCompleted(
        UserAnswersReader[TraderDetails],
        controllers.traderDetails.routes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url
      )
      .ifInProgress(
        IsPrincipalEoriKnownPage.reader,
        controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(userAnswers.id, NormalMode).url
      )
      .ifNotStarted(controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(userAnswers.id, NormalMode).url)
      .section

  private val itemsDetailsLastIndex: Index = userAnswers.get(DeriveNumberOfItems).fold(Index(0))(Index(_))

  private val itemDetails =
    taskListDsl
      .sectionName("declarationSummary.section.addItems")
      .ifCompleted(
        UserAnswersReader[NonEmptyList[ItemSection]],
        controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, itemsDetailsLastIndex).url
      )
      .ifInProgress(
        ItemDescriptionPage(Index(0)).reader,
        controllers.addItems.routes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(0), NormalMode).url
      )
      .ifNotStarted(controllers.addItems.routes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(0), NormalMode).url)
      .section

  def taskListSections: Seq[SectionDetails] =
    Seq(
      movementDetails,
      routeDetails,
      transportDetails,
      traderDetails,
      itemDetails
    )
}

object TaskListViewModel {

  object Constants {
    val sections: String = "sections"
  }

  def apply(userAnswers: UserAnswers): TaskListViewModel = new TaskListViewModel(userAnswers)

  implicit val writes: OWrites[TaskListViewModel] =
    taskListViewModel =>
      Json.obj(
        Constants.sections -> taskListViewModel.taskListSections
    )
}
