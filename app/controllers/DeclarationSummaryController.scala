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

package controllers

import controllers.actions._
import javax.inject.Inject
import models.{DeclarationType, LocalReferenceNumber, NormalMode, Section, UserAnswers, Status => SectionStatus}
import pages.{DeclarationTypePage, Page, ProcedureTypePage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

class DeclarationSummaryController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      val sections = getSections(lrn: LocalReferenceNumber, request.userAnswers)
      val json = Json.obj("lrn" -> lrn, "sections" -> sections)

      renderer.render("declarationSummary.njk", json).map(Ok(_))
  }

  def getSections(userAnswers: UserAnswers): Seq[Section] = {
    Seq(
      getMovementDetailsSection(userAnswers)
    )
  }

  def getIncompletePage(userAnswers: UserAnswers): Call = {
    routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode)
  }

  def movementDetailsPages(lrn: LocalReferenceNumber): Seq[(Page, String)] = {
    Seq(
      DeclarationTypePage -> routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url,
      ProcedureTypePage -> routes.ProcedureTypeController.onPageLoad(lrn, NormalMode).url,
      DeclarationTypePage -> routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url,
      DeclarationTypePage -> routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url,
    )
  }

  private def getMovementDetailsSection(userAnswers: UserAnswers) = {
    Section("declarationSummary.section.addMovementDetails",
      getIncompletePage(userAnswers),
      SectionStatus.NotStarted)
  }
}
