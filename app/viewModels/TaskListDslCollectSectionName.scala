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

import models.Status.{Completed, InProgress, NotStarted}
import models.{SectionDetails, Status, UserAnswers}
import models.journeyDomain.UserAnswersReader
import cats.implicits._

private[viewModels] class TaskListDslCollectSectionName(userAnswers: UserAnswers) {

  def sectionName(sectionName: String): TaskListDslSectionNameStage =
    new TaskListDslSectionNameStage(userAnswers)(sectionName)

}

private[viewModels] class TaskListDslSectionNameStage(userAnswers: UserAnswers)(sectionName: String) {

  def ifCompleted[A, B](readerIfCompleted: UserAnswersReader[A], urlIfCompleted: String): TaskListDslIfCompletedStage[A] =
    new TaskListDslIfCompletedStage[A](userAnswers)(sectionName, readerIfCompleted, urlIfCompleted)

}

private[viewModels] class TaskListDslIfCompletedStage[A](userAnswers: UserAnswers)(sectionName: String,
                                                                                   readerIfCompleted: UserAnswersReader[A],
                                                                                   urlIfCompleted: String) {

  def ifInProgress[B](readerIfInProgress: UserAnswersReader[B], urlIfInProgress: String): TaskListDslIfInProgressStage[A, B] =
    new TaskListDslIfInProgressStage(userAnswers)(sectionName, readerIfCompleted, urlIfCompleted, readerIfInProgress, urlIfInProgress)

}

private[viewModels] class TaskListDslIfInProgressStage[A, B](userAnswers: UserAnswers)(sectionName: String,
                                                                                       readerIfCompleted: UserAnswersReader[A],
                                                                                       urlIfCompleted: String,
                                                                                       readerIfInProgress: UserAnswersReader[B],
                                                                                       urlIfInProgress: String) {

  def ifNotStarted(urlIfNotStarted: String): TaskListDsl[A, B] =
    new TaskListDsl(userAnswers)(sectionName, readerIfCompleted, urlIfCompleted, readerIfInProgress, urlIfInProgress, urlIfNotStarted)
}

private[viewModels] class TaskListDsl[A, B](userAnswers: UserAnswers)(
  sectionName: String,
  readerIfCompleted: UserAnswersReader[A],
  urlIfCompleted: String,
  readerIfInProgress: UserAnswersReader[B],
  urlIfInProgress: String,
  urlIfNotStarted: String
) {

  val section: SectionDetails = {
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
