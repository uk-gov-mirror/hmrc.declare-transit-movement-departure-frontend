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

package controllers.movementDetails

import controllers.actions._
import controllers.{routes => mainRoutes}
import javax.inject.Inject
import models.{LocalReferenceNumber, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.MovementDetailsCheckYourAnswersHelper
import viewModels.sections.Section

import scala.concurrent.ExecutionContext

class MovementDetailsCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val sections: Seq[Section] = createSections(request.userAnswers)
      val json = Json.obj(
        "lrn"         -> lrn,
        "sections"    -> Json.toJson(sections),
        "nextPageUrl" -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
      )

      renderer.render("movementDetailsCheckYourAnswers.njk", json).map(Ok(_))
  }

  private def createSections(userAnswers: UserAnswers): Seq[Section] = {
    val checkYourAnswersHelper = new MovementDetailsCheckYourAnswersHelper(userAnswers)

    Seq(
      Section(
        Seq(
          checkYourAnswersHelper.declarationType,
          checkYourAnswersHelper.preLodgeDeclarationPage,
          checkYourAnswersHelper.containersUsedPage,
          checkYourAnswersHelper.declarationPlace,
          checkYourAnswersHelper.declarationForSomeoneElse,
          checkYourAnswersHelper.representativeName,
          checkYourAnswersHelper.representativeCapacity
        ).flatten
      ))
  }
}
