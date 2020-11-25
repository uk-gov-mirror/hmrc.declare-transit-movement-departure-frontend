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
import pages.ProcedureTypePage
import play.api.libs.json._
import cats.implicits._
import cats.data.ReaderT

class TaskListDsl(userAnswers: UserAnswers) {

  class IntermediateDslCompletedStage[A](readerIfCompleted: UserAnswersReader[A]) {

    def ifInProgress[B](readerIfInProgress: UserAnswersReader[B]): TaskListFullDsl[A, B] =
      new TaskListFullDsl(userAnswers)(readerIfCompleted, readerIfInProgress)

  }

  def ifCompleted[A, B](readerIfCompleted: UserAnswersReader[A]): IntermediateDslCompletedStage[A] =
    new IntermediateDslCompletedStage[A](readerIfCompleted)
}

class TaskListFullDsl[A, B](userAnswers: UserAnswers)(readerIfCompleted: UserAnswersReader[A], readerIfInProgress: UserAnswersReader[B]) {

  def run: (String, Status) = {
    val completed = readerIfCompleted
      .map[(String, Status)](
        _ => (controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed)
      )

    val inProgress = readerIfInProgress
      .map[(String, Status)](
        _ => (controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url, InProgress)
      )

    completed
      .orElse(inProgress)
      .run(userAnswers)
      .getOrElse((controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url, NotStarted))

  }

}

class TaskListViewModel(userAnswers: UserAnswers) {

  // TODO: This is a workaround till we remove UserAnswersParser
  implicit def fromUserAnswersParser[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersReader[A] =
    ReaderT[Option, UserAnswers, A](parser.run _)

  private val lrn       = userAnswers.id
  private val statusDsl = new TaskListDsl(userAnswers)

  def taskListSections: Seq[SectionDetails] = {
    val (onwardRoute, status) =
      statusDsl
        .ifCompleted(UserAnswersReader[MovementDetails])
        .ifInProgress(ProcedureTypePage.reader)
        .run

    val movementDetails = SectionDetails("declarationSummary.section.movementDetails", onwardRoute, status)

    Seq(
      movementDetails
    )
  }
}

object TaskListViewModel {

  private val inProgressStartedReader: UserAnswersReader[Status] = InProgress.pure[UserAnswersReader].widen[Status]
  private val notStartedReader: UserAnswersReader[Status]        = NotStarted.pure[UserAnswersReader].widen[Status]

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
