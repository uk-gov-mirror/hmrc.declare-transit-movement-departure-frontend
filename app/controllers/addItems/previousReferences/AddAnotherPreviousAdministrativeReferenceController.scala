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

package controllers.addItems.previousReferences

import controllers.actions._
import derivable.DeriveNumberOfPreviousAdministrativeReferences
import forms.AddAnotherPreviousAdministrativeReferenceFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.AddItems
import pages.addItems.AddAnotherPreviousAdministrativeReferencePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.AddItemsCheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class AddAnotherPreviousAdministrativeReferenceController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItems navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: AddAnotherPreviousAdministrativeReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        renderPage(lrn, index, referenceIndex, form).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, index, referenceIndex, formWithErrors).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherPreviousAdministrativeReferencePage(index, referenceIndex), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddAnotherPreviousAdministrativeReferencePage(index, referenceIndex), mode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, index: Index, referenceIndex: Index, form: Form[Boolean])(
    implicit request: DataRequest[AnyContent]): Future[Html] = {

    val cyaHelper             = new AddItemsCheckYourAnswersHelper(request.userAnswers)
    val numberOfReferences    = request.userAnswers.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)
    val indexList: Seq[Index] = List.range(0, numberOfReferences).map(Index(_))

    val referenceRows = indexList.map {
      index =>
        cyaHelper.previousAdministrativeReferenceRows(index, referenceIndex)
    }

    val singularOrPlural = if (numberOfReferences == 1) "singular" else "plural"
    val json = Json.obj(
      "form"          -> form,
      "lrn"           -> lrn,
      "pageTitle"     -> msg"addAnotherPreviousAdministrativeReference.title.$singularOrPlural".withArgs(numberOfReferences),
      "heading"       -> msg"addAnotherPreviousAdministrativeReference.heading.$singularOrPlural".withArgs(numberOfReferences),
      "referenceRows" -> referenceRows,
      "radios"        -> Radios.yesNo(form("value"))
    )

    renderer.render("addItems/addAnotherPreviousAdministrativeReference.njk", json)
  }
}
