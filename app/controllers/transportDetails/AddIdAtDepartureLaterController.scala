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

package controllers.transportDetails

import controllers.actions._
import javax.inject.Inject
import models.{LocalReferenceNumber, Mode}
import pages.AddIdAtDepartureLaterPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.nunjucks.NunjucksSupport
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import navigation.annotations.TransportDetails

import scala.concurrent.ExecutionContext

class AddIdAtDepartureLaterController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 identify: IdentifierAction,
                                                 getData: DataRetrievalActionProvider,
                                                 requireData: DataRequiredAction,
                                                 @TransportDetails navigator: Navigator,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      val json = Json.obj("lrn" -> lrn,
        "nextPageUrl" -> navigator.nextPage(AddIdAtDepartureLaterPage, mode, request.userAnswers).url,
        "mode" -> mode
      )

      renderer.render("addIdAtDepartureLater.njk", json).map(Ok(_))
  }
}
