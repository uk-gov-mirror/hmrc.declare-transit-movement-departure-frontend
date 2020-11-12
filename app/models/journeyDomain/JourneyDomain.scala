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

package models.journeyDomain

import models.UserAnswers
import models.messages.DeclarationRequest

case class JourneyDomain(
  preTaskList: Unit, // TODO
  movementDetails: MovementDetails,
  routeDetails: RouteDetails,
  transportDetails: TransportDetails,
  traderDetails: TraderDetails,
  itemDetails: Seq[Unit], // TODO
  goodsSummary: GoodsSummary,
  guarantee: GuaranteeDetails
)

object JourneyDomain {

  implicit val userAnswersReader: UserAnswersReader[JourneyDomain] = ???

  def convert(journeyDomain: JourneyDomain): DeclarationRequest = ???

  // TODO: remove. We will use UserAnswersReader[JourneyDomain].run instead
  def parse(userAnswers: UserAnswers): Option[(MovementDetails, RouteDetails, TraderDetails, TransportDetails)] =
    for {
      movementDetails  <- UserAnswersOptionalParser[MovementDetails].run(userAnswers)
      routeDetails     <- UserAnswersOptionalParser[RouteDetails].run(userAnswers)
      traderDetails    <- UserAnswersOptionalParser[TraderDetails].run(userAnswers)
      transportDetails <- UserAnswersOptionalParser[TransportDetails].run(userAnswers)
    } yield (movementDetails, routeDetails, traderDetails, transportDetails)

}
