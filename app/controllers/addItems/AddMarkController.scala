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

package controllers.addItems

import controllers.actions._
import forms.addItems.AddMarkFormProvider

import javax.inject.Inject
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.AddItems
import pages.addItems.AddMarkPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class AddMarkController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItems navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: AddMarkFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, packageIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val form = formProvider(packageIndex.display)

        val preparedForm = request.userAnswers.get(AddMarkPage(itemIndex, packageIndex)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val json = Json.obj(
          "form"         -> preparedForm,
          "mode"         -> mode,
          "lrn"          -> lrn,
          "radios"       -> Radios.yesNo(preparedForm("value")),
          "displayIndex" -> packageIndex.display
        )

        renderer.render("addItems/addMark.njk", json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, packageIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val form = formProvider(packageIndex.display)

        form
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"         -> formWithErrors,
                "mode"         -> mode,
                "lrn"          -> lrn,
                "radios"       -> Radios.yesNo(formWithErrors("value")),
                "displayIndex" -> packageIndex.display
              )

              renderer.render("addItems/addMark.njk", json).map(BadRequest(_))
            },
            value => {
              val userAnswers = request.userAnswers.get(AddMarkPage(itemIndex, packageIndex)).map(_ == value) match {
                case Some(true) => Future.successful(request.userAnswers)
                case _ =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(AddMarkPage(itemIndex, packageIndex), value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield updatedAnswers
              }

              userAnswers.map {
                ua =>
                  Redirect(navigator.nextPage(AddMarkPage(itemIndex, packageIndex), mode, ua))
              }
            }
          )
    }
}
