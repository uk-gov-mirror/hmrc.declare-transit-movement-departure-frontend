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

package controllers.guaranteeDetails

import controllers.actions._
import forms.guaranteeDetails.GuaranteeTypeFormProvider
import models.{DependentSection, GuaranteeType, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.GuaranteeDetails
import pages.guaranteeDetails.GuaranteeTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GuaranteeTypeController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GuaranteeDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: GuaranteeTypeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(GuaranteeTypePage(index)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val json = Json.obj(
          "form"   -> preparedForm,
          "mode"   -> mode,
          "lrn"    -> lrn,
          "radios" -> GuaranteeType.radios(preparedForm)
        )

        renderer.render("guaranteeDetails/guaranteeType.njk", json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"   -> formWithErrors,
                "mode"   -> mode,
                "lrn"    -> lrn,
                "radios" -> GuaranteeType.radios(formWithErrors)
              )

              renderer.render("guaranteeDetails/guaranteeType.njk", json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(GuaranteeTypePage(index), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(GuaranteeTypePage(index), mode, updatedAnswers))
          )
    }
}
