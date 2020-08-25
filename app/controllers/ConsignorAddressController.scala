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
import forms.ConsignorAddressFormProvider
import javax.inject.Inject
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import pages.{ConsignorAddressPage, ConsignorNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class ConsignorAddressController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            sessionRepository: SessionRepository,
                                            navigator: Navigator,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalActionProvider,
                                            requireData: DataRequiredAction,
                                            formProvider: ConsignorAddressFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            renderer: Renderer
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(ConsignorNamePage) match {
        case Some(consignorName) =>
          val preparedForm = request.userAnswers.get(ConsignorAddressPage) match {
            case Some(value) => formProvider().fill(value)
            case None => formProvider()
          }

          val json = Json.obj(
            "form" -> preparedForm,
            "lrn" -> lrn,
            "mode" -> mode,
            "consignorName" -> consignorName
          )

          renderer.render("consignorAddress.njk", json).map(Ok(_))
        case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))

      }
  }

      def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
        implicit request =>

          form.bindFromRequest().fold(
            formWithErrors => {

              val json = Json.obj(
                "form" -> formWithErrors,
                "lrn" -> lrn,
                "mode" -> mode
              )

              renderer.render("consignorAddress.njk", json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ConsignorAddressPage, value))
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ConsignorAddressPage, mode, updatedAnswers))
          )
      }
  }
