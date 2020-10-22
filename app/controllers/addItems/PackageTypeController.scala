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

package controllers.addItems

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.PackageTypeFormProvider
import javax.inject.Inject
import models.reference.PackageType
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.AddItems
import pages.PackageTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.packageTypeList

import scala.concurrent.ExecutionContext

class PackageTypeController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  @AddItems navigator: Navigator,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: PackageTypeFormProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with NunjucksSupport
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getPackageTypes().flatMap {
        packageTypes =>
          val form = formProvider(packageTypes)

          val preparedForm: Form[PackageType] = request.userAnswers
            .get(PackageTypePage)
            .flatMap(packageTypes.getPackageType)
            .map(form.fill)
            .getOrElse(form)

          val json = Json.obj(
            "form"         -> preparedForm,
            "lrn"          -> lrn,
            "mode"         -> mode,
            "packageTypes" -> packageTypeList(preparedForm.value, packageTypes.packageTypeList)
          )

          renderer.render("packageType.njk", json).map(Ok(_))
      }
  }

}
