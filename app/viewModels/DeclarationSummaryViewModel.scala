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

import models.journeyDomain.MovementDetails
import models.{LocalReferenceNumber, SectionDetails, UserAnswers}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import utils.SectionsHelper
import models.journeyDomain.UserAnswersOptionalParser
import models.journeyDomain.TraderDetails
import config.ManageTransitMovementsService
import models.journeyDomain.RouteDetails
import models.journeyDomain.TransportDetails

class DeclarationSummaryViewModel(manageTransitMovementsService: ManageTransitMovementsService, userAnswers: UserAnswers) {

  val lrn: LocalReferenceNumber      = userAnswers.id
  val sections: Seq[SectionDetails]  = new SectionsHelper(userAnswers).getSections
  val backToTransitMovements: String = manageTransitMovementsService.service.fullServiceUrl

  val isDeclarationComplete: Boolean =
    (for {
      _ <- UserAnswersOptionalParser[MovementDetails].run(userAnswers)
      _ <- UserAnswersOptionalParser[RouteDetails].run(userAnswers)
      _ <- UserAnswersOptionalParser[TraderDetails].run(userAnswers)
      _ <- UserAnswersOptionalParser[TransportDetails].run(userAnswers)
    } yield true).getOrElse(false)

}

object DeclarationSummaryViewModel {

  def apply(manageTransitMovementsService: ManageTransitMovementsService, userAnswers: UserAnswers): DeclarationSummaryViewModel =
    new DeclarationSummaryViewModel(manageTransitMovementsService, userAnswers)

  def unapply(arg: DeclarationSummaryViewModel): Option[(LocalReferenceNumber, Seq[SectionDetails], String, Boolean)] =
    Some((arg.lrn, arg.sections, arg.backToTransitMovements, arg.isDeclarationComplete))

  implicit val writes: OWrites[DeclarationSummaryViewModel] =
    ((__ \ "lrn").write[LocalReferenceNumber] and
      (__ \ "sections").write[Seq[SectionDetails]] and
      (__ \ "backToTransitMovements").write[String] and
      (__ \ "isDeclarationComplete").write[Boolean])(unlift(unapply))
}
