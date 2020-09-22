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

import controllers.actions._
import forms.ArrivalTimesAtOfficeFormProvider
import javax.inject.Inject
import models.{Index, LocalDateTimeWithAMPM, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.ArrivalTimesAtOfficePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._
import viewModels.DateTimeInput

import scala.concurrent.{ExecutionContext, Future}

class ArrivalTimesAtOfficeController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                sessionRepository: SessionRepository,
                                                @RouteDetails navigator: Navigator,
                                                identify: IdentifierAction,
                                                getData: DataRetrievalActionProvider,
                                                requireData: DataRequiredAction,
                                                formProvider: ArrivalTimesAtOfficeFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                renderer: Renderer
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  val form: Form[LocalDateTimeWithAMPM] = formProvider("") //TODO

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(ArrivalTimesAtOfficePage(index)) match {
        case Some(value) => form.fill(value)
        case None => form
      }

      loadPage(lrn, mode, preparedForm.value.map(_.amOrPm), preparedForm, Ok)
  }

  private def loadPage(lrn: LocalReferenceNumber,
                       mode: Mode,
                       selectAMPMValue: Option[String],
                       form: Form[LocalDateTimeWithAMPM],
                       status: Status
                      )(implicit request: Request[AnyContent]): Future[Result] = {
    val viewModel = DateTimeInput.localDateTime(form("value"))

    val json = Json.obj(
      "form" -> form,
      "mode" -> mode,
      "lrn" -> lrn,
      "amPmList" -> amPmAsJson(selectAMPMValue),
      "dateTime" -> viewModel
    )

    renderer.render("arrivalTimesAtOffice.njk", json).map(status(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {
          loadPage(lrn, mode, formWithErrors.data.get("value.amOrPm"), formWithErrors, BadRequest)
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ArrivalTimesAtOfficePage(index), value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ArrivalTimesAtOfficePage(index), mode, updatedAnswers))
      )
  }
}
