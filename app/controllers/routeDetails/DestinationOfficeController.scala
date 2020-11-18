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
import controllers.{routes => mainRoutes}
import forms.routeDetails.DestinationOfficeFormProvider
import javax.inject.Inject
import models.reference.{CountryCode, CustomsOffice}
import models.requests.DataRequest
import models.{CustomsOfficeList, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.{DestinationCountryPage, DestinationOfficePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._

import scala.concurrent.{ExecutionContext, Future}

class DestinationOfficeController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: DestinationOfficeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(DestinationCountryPage) match {
        case Some(countryCode) =>
          getCustomsOfficeAndCountryName(countryCode) flatMap {
            case (customsOffices, countryName) =>
              val form: Form[CustomsOffice] = formProvider(customsOffices, countryName)

              val preparedForm: Form[CustomsOffice] = request.userAnswers
                .get(DestinationOfficePage)
                .flatMap(customsOffices.getCustomsOffice)
                .map(form.fill)
                .getOrElse(form)

              val json = Json.obj(
                "form"           -> preparedForm,
                "lrn"            -> lrn,
                "customsOffices" -> getCustomsOfficesAsJson(preparedForm.value, customsOffices.customsOffices),
                "countryName"    -> countryName,
                "mode"           -> mode
              )
              renderer.render("destinationOffice.njk", json).map(Ok(_))
          }

        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(DestinationCountryPage) match {
        case Some(countryCode) =>
          getCustomsOfficeAndCountryName(countryCode) flatMap {
            case (customsOffices, countryName) =>
              val form = formProvider(customsOffices, countryName)

              form
                .bindFromRequest()
                .fold(
                  formWithErrors => {
                    val json = Json.obj(
                      "form"           -> formWithErrors,
                      "lrn"            -> lrn,
                      "customsOffices" -> getCustomsOfficesAsJson(formWithErrors.value, customsOffices.customsOffices),
                      "countryName"    -> countryName,
                      "mode"           -> mode
                    )
                    renderer.render("destinationOffice.njk", json).map(BadRequest(_))
                  },
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(DestinationOfficePage, value.id))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(DestinationOfficePage, mode, updatedAnswers))
                )
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }

  private def getCustomsOfficeAndCountryName(countryCode: CountryCode)(implicit request: DataRequest[AnyContent]): Future[(CustomsOfficeList, String)] =
    referenceDataConnector.getCustomsOfficesOfTheCountry(countryCode) flatMap {
      customsOffices =>
        referenceDataConnector.getTransitCountryList() map {
          countryList =>
            val countryName = countryList.getCountry(countryCode).fold(countryCode.code)(_.description)
            (customsOffices, countryName)
        }
    }

}
