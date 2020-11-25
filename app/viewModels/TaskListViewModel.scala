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

import models.Status._
import models.journeyDomain._
import models.{NormalMode, SectionDetails, Status, UserAnswers}
import pages.{CountryOfDispatchPage, ProcedureTypePage}
import play.api.libs.json._
import cats.implicits._
import cats.data.ReaderT

class TaskListDsl(userAnswers: UserAnswers) {

  def sectionName(sectionName: String): TaskListDslSectionNameStage =
    new TaskListDslSectionNameStage(userAnswers)(sectionName)

}

class TaskListDslSectionNameStage(userAnswers: UserAnswers)(sectionName: String) {

  def ifCompleted[A, B](readerIfCompleted: UserAnswersReader[A], urlIfCompleted: String): TaskListDslIfCompletedStage[A] =
    new TaskListDslIfCompletedStage[A](userAnswers)(sectionName, readerIfCompleted, urlIfCompleted)

}

class TaskListDslIfCompletedStage[A](userAnswers: UserAnswers)(sectionName: String, readerIfCompleted: UserAnswersReader[A], urlIfCompleted: String) {

  def ifInProgress[B](readerIfInProgress: UserAnswersReader[B], urlIfInProgress: String): TaskListDslIfInProgressStage[A, B] =
    new TaskListDslIfInProgressStage(userAnswers)(sectionName, readerIfCompleted, urlIfCompleted, readerIfInProgress, urlIfInProgress)

}

class TaskListDslIfInProgressStage[A, B](userAnswers: UserAnswers)(sectionName: String,
                                                                   readerIfCompleted: UserAnswersReader[A],
                                                                   urlIfCompleted: String,
                                                                   readerIfInProgress: UserAnswersReader[B],
                                                                   urlIfInProgress: String) {

  def ifNotStarted(urlIfNotStarted: String): TaskListDslAllInfoStage[A, B] =
    new TaskListDslAllInfoStage(userAnswers)(sectionName, readerIfCompleted, urlIfCompleted, readerIfInProgress, urlIfInProgress, urlIfNotStarted)
}

class TaskListDslAllInfoStage[A, B](userAnswers: UserAnswers)(
  sectionName: String,
  readerIfCompleted: UserAnswersReader[A],
  urlIfCompleted: String,
  readerIfInProgress: UserAnswersReader[B],
  urlIfInProgress: String,
  urlIfNotStarted: String
) {

  def section: SectionDetails = {
    val completed = readerIfCompleted
      .map[(String, Status)](
        _ => (urlIfCompleted, Completed)
      )

    val inProgress = readerIfInProgress
      .map[(String, Status)](
        _ => (urlIfInProgress, InProgress)
      )

    val (onwardRoute, status) = completed
      .orElse(inProgress)
      .run(userAnswers)
      .getOrElse((urlIfNotStarted, NotStarted))

    SectionDetails(sectionName, onwardRoute, status)
  }
}

class TaskListViewModel(userAnswers: UserAnswers) {

  // TODO: This is a workaround till we remove UserAnswersParser
  implicit def fromUserAnswersParser[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersReader[A] =
    ReaderT[Option, UserAnswers, A](parser.run _)

  private val lrn         = userAnswers.id
  private val taskListDsl = new TaskListDsl(userAnswers)

  def taskListSections: Seq[SectionDetails] = {

    val movementDetails =
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

    val routeDetails =
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

    Seq(
      movementDetails,
      routeDetails
    )
  }
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
