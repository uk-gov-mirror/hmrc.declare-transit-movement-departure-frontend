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

package controllers.transportDetails

import connectors.ReferenceDataConnector
import controllers.actions._
import controllers.{routes => mainRoutes}
import javax.inject.Inject
import models.{CountryList, LocalReferenceNumber, TransportModeList, UserAnswers}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.TransportDetailsCheckYourAnswersHelper
import viewModels.TransportDetailsCheckYourAnswersViewModel
import viewModels.sections.Section

import scala.concurrent.{ExecutionContext, Future}

class TransportDetailsCheckYourAnswersController @Inject()(
                                                            override val messagesApi: MessagesApi,
                                                            identify: IdentifierAction,
                                                            getData: DataRetrievalActionProvider,
                                                            requireData: DataRequiredAction,
                                                            val controllerComponents: MessagesControllerComponents,
                                                            referenceDataConnector: ReferenceDataConnector,
                                                            renderer: Renderer
                                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      referenceDataConnector.getCountryList().flatMap {
        countryList =>
          referenceDataConnector.getTransportModes().flatMap {
            transportModeList =>

              val sections: Seq[Section] = TransportDetailsCheckYourAnswersViewModel(request.userAnswers, countryList, transportModeList).sections

              //val sections: Seq[Section] = createSections(request.userAnswers, countryList, transportModeList)

              val json = Json.obj("lrn" -> lrn,
                "sections" -> Json.toJson(sections)
              )

              renderer.render("transportDetailsCheckYourAnswers.njk", json).map(Ok(_))

          }
      }

  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      Future.successful(Redirect(mainRoutes.DeclarationSummaryController.onPageLoad(lrn)))
  }

  //TODO: These can be built in TransportDetailsCheckYourAnswersViewModel
  private def createSections(userAnswers: UserAnswers, countryList: CountryList, transportModeList: TransportModeList)(implicit messages: Messages): Seq[Section] = {
    val checkYourAnswersHelper = new TransportDetailsCheckYourAnswersHelper(userAnswers)

    Seq(Section(
      Seq(
        checkYourAnswersHelper.inlandMode(transportModeList),
        checkYourAnswersHelper.addIdAtDeparture,
        checkYourAnswersHelper.idAtDeparture,
        checkYourAnswersHelper.nationalityAtDeparture(countryList),
        checkYourAnswersHelper.changeAtBorder,
        checkYourAnswersHelper.modeAtBorder(transportModeList),
        checkYourAnswersHelper.idCrossingBorder,
        checkYourAnswersHelper.modeCrossingBorder(transportModeList),
        checkYourAnswersHelper.nationalityCrossingBorder(countryList)
      ).flatten
    ))
  }
}
