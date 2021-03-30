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

package controllers.safetyAndSecurity

import connectors.ReferenceDataConnector
import controllers.actions._
import controllers.{routes => mainRoutes}
import javax.inject.Inject
import models.{DependentSection, LocalReferenceNumber, NormalMode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.SafetyAndSecurityCheckYourAnswersViewModel
import viewModels.sections.Section

import scala.concurrent.ExecutionContext

class SafetyAndSecurityCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  referenceDataConnector: ReferenceDataConnector,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        referenceDataConnector.getCountryList() flatMap {
          countries =>
            val sections: Seq[Section] = SafetyAndSecurityCheckYourAnswersViewModel(request.userAnswers, countries)

            val json = Json.obj(
              "lrn"                           -> lrn,
              "sections"                      -> Json.toJson(sections),
              "addAnotherCountryOfRoutingUrl" -> routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, NormalMode).url,
              "nextPageUrl"                   -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
            )

            renderer.render("safetyAndSecurity/SafetyAndSecurityCheckYourAnswers.njk", json).map(Ok(_))
        }

    }
}
