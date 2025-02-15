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

package controllers.addItems.specialMentions

import controllers.actions._
import forms.addItems.specialMentions.AddSpecialMentionFormProvider

import javax.inject.Inject
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SpecialMentions
import pages.addItems.specialMentions.AddSpecialMentionPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class AddSpecialMentionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SpecialMentions navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: AddSpecialMentionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/specialMentions/addSpecialMention.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(AddSpecialMentionPage(itemIndex)) match {
          case None        => formProvider(itemIndex)
          case Some(value) => formProvider(itemIndex).fill(value)
        }

        val json = Json.obj(
          "form"      -> preparedForm,
          "mode"      -> mode,
          "lrn"       -> lrn,
          "radios"    -> Radios.yesNo(preparedForm("value")),
          "itemIndex" -> itemIndex.display
        )

        renderer.render(template, json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        formProvider(itemIndex)
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"      -> formWithErrors,
                "mode"      -> mode,
                "lrn"       -> lrn,
                "radios"    -> Radios.yesNo(formWithErrors("value")),
                "itemIndex" -> itemIndex.display
              )

              renderer.render(template, json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddSpecialMentionPage(itemIndex), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddSpecialMentionPage(itemIndex), mode, updatedAnswers))
          )
    }
}
