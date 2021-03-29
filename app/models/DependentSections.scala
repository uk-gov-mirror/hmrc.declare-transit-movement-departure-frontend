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

package models

import cats.implicits._
import models.journeyDomain.{MovementDetails, RouteDetails, SafetyAndSecurity, TraderDetails, TransportDetails, UserAnswersReader}
import pages.AddSecurityDetailsPage
import viewModels.TaskListViewModel

object DependentSections {

  import TaskListViewModel.fromUserAnswersParser

  val movementDetails: UserAnswersReader[MovementDetails]     = UserAnswersReader[MovementDetails]
  val routeDetails: UserAnswersReader[RouteDetails]           = UserAnswersReader[RouteDetails]
  val safetyAndSecurity: UserAnswersReader[SafetyAndSecurity] = UserAnswersReader[SafetyAndSecurity]
  val traderDetails: UserAnswersReader[TraderDetails]         = UserAnswersReader[TraderDetails]
  val transportDetails: UserAnswersReader[TransportDetails]   = UserAnswersReader[TransportDetails]

  def itemsDependentSections(userAnswers: UserAnswers): UserAnswersReader[_] = {
    val commonSection = for {
      _            <- movementDetails
      _            <- traderDetails
      routeDetails <- routeDetails
    } yield routeDetails

    if (userAnswers.get(AddSecurityDetailsPage).contains(true)) {
      for {
        _     <- commonSection
        sAndS <- safetyAndSecurity
      } yield sAndS
    } else {
      commonSection
    }
  }

}
