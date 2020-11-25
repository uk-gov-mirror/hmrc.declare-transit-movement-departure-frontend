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

package controllers.addItems.traderSecurityDetails

import akka.util.Helpers.Requiring
import controllers.actions._
import forms.addItems.traderSecurityDetails.SecurityConsigneeAddressFormProvider
import javax.inject.Inject
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.AddItems
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import pages.addItems.traderSecurityDetails.{SecurityConsigneeAddressPage, SecurityConsigneeNamePage}
import utils.countryJsonList
import controllers.{routes => mainRoutes}
import connectors.ReferenceDataConnector
import models.reference.{Country, CountryCode}

import scala.concurrent.{ExecutionContext, Future}

class SecurityConsigneeAddressController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItems navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: SecurityConsigneeAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/traderSecurityDetails/securityConsigneeAddress.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCountryList() flatMap {
        countries =>
          request.userAnswers.get(SecurityConsigneeNamePage(index)) match {
            case Some(consigneeName) =>
              val preparedForm = request.userAnswers.get(SecurityConsigneeAddressPage(index)) match {
                case Some(value) => formProvider(countries, consigneeName).fill(value)
                case None        => formProvider(countries, consigneeName)
              }

              val json = Json.obj(
                "form"          -> preparedForm,
                "lrn"           -> lrn,
                "index"         -> index.display,
                "mode"          -> mode,
                "consigneeName" -> consigneeName,
                "countries"     -> countryJsonList(preparedForm.value.map(_.country), countries.fullList)
              )

              renderer.render(template, json).map(Ok(_))
          }
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(SecurityConsigneeNamePage(index)) match {
        case Some(consigneeName) =>
          referenceDataConnector.getCountryList() flatMap {
            countries =>
              formProvider(countries, consigneeName)
                .bindFromRequest()
                .fold(
                  formWithErrors => {
                    val countryValue: Option[Country] =
                      formWithErrors.data.get("country").flatMap {
                        country =>
                          countries.getCountry(CountryCode(country))
                      }

                    val json = Json.obj(
                      "form"          -> formWithErrors,
                      "lrn"           -> lrn,
                      "mode"          -> mode,
                      "index"         -> index.display,
                      "consigneeName" -> consigneeName,
                      "countries"     -> countryJsonList(countryValue, countries.fullList)
                    )

                    renderer.render(template, json).map(BadRequest(_))
                  },
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(SecurityConsigneeAddressPage(index), value))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(SecurityConsigneeAddressPage(index), mode, updatedAnswers))
                )
          }
      }
  }
}
