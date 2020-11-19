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

package controllers.routeDetails

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.routeDetails.OfficeOfDepartureFormProvider
import javax.inject.Inject
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._
import navigation.annotations.RouteDetails
import pages.routeDetails.OfficeOfDeparturePage

import scala.concurrent.{ExecutionContext, Future}

class OfficeOfDepartureController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: OfficeOfDepartureFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCustomsOffices flatMap {
        customsOffices =>
          val form = formProvider(customsOffices)
          val preparedForm = request.userAnswers
            .get(OfficeOfDeparturePage)
            .flatMap(customsOffices.getCustomsOffice)
            .map(form.fill)
            .getOrElse(form)

          val json = Json.obj(
            "form"           -> preparedForm,
            "lrn"            -> lrn,
            "customsOffices" -> getCustomsOfficesAsJson(preparedForm.value, customsOffices.customsOffices),
            "mode"           -> mode
          )

          renderer.render("officeOfDeparture.njk", json).map(Ok(_))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCustomsOffices flatMap {
        customsOffices =>
          val form = formProvider(customsOffices)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => {
                val json = Json.obj(
                  "form"           -> formWithErrors,
                  "lrn"            -> lrn,
                  "customsOffices" -> getCustomsOfficesAsJson(form.value, customsOffices.customsOffices),
                  "mode"           -> mode
                )

                renderer.render("officeOfDeparture.njk", json).map(BadRequest(_))
              },
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(OfficeOfDeparturePage, value.id))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(OfficeOfDeparturePage, mode, updatedAnswers))
            )
      }
  }

}
