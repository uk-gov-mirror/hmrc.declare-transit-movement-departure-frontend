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
import derivable.DeriveNumberOfOfficeOfTransits
import javax.inject.Inject
import models.reference.CountryCode
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, NormalMode}
import pages.DestinationCountryPage
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.MessageInterpolators
import utils.RouteDetailsCheckYourAnswersHelper
import viewModels.sections.Section

import scala.concurrent.{ExecutionContext, Future}

class RouteDetailsCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(DestinationCountryPage) match {
        case Some(countryCode) =>
          createSections(countryCode) flatMap {
            sections =>
              val json = Json.obj(
                "lrn"                    -> lrn,
                "sections"               -> Json.toJson(sections),
                "addOfficesOfTransitUrl" -> routes.AddTransitOfficeController.onPageLoad(lrn, NormalMode).url,
                "submitUrl"              -> routes.RouteDetailsCheckYourAnswersController.onSubmit(lrn).url
              )
              renderer.render("routeDetailsCheckYourAnswers.njk", json).map(Ok(_))
          }
        case _ =>
          Logger.info("DestinationCountryPage has no data")
          Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      Future.successful(Redirect(mainRoutes.DeclarationSummaryController.onPageLoad(lrn)))
  }

  private def createSections(countryCode: CountryCode)(implicit hc: HeaderCarrier, request: DataRequest[AnyContent]): Future[Seq[Section]] = {
    val checkYourAnswersHelper = new RouteDetailsCheckYourAnswersHelper(request.userAnswers)

    referenceDataConnector.getCountryList() flatMap {
      countryList =>
        referenceDataConnector.getCustomsOffices() flatMap {
          customsOfficeList =>
            referenceDataConnector.getTransitCountryList() flatMap {
              destCountryList =>
                referenceDataConnector.getCustomsOfficesOfTheCountry(countryCode) flatMap {
                  destOfficeList =>
                    officeOfTransitSections(checkYourAnswersHelper) map {
                      officeOfTransitSection =>
                        val section: Section = Section(
                          Seq(
                            checkYourAnswersHelper.countryOfDispatch(countryList),
                            checkYourAnswersHelper.officeOfDeparture(customsOfficeList),
                            checkYourAnswersHelper.destinationCountry(destCountryList),
                            checkYourAnswersHelper.destinationOffice(destOfficeList)
                          ).flatten)

                        Seq(section, officeOfTransitSection)
                    }
                }
            }
        }
    }
  }

  private def officeOfTransitSections(routesCYAHelper: RouteDetailsCheckYourAnswersHelper)(implicit hc: HeaderCarrier,
                                                                                           request: DataRequest[AnyContent]): Future[Section] =
    referenceDataConnector.getOfficeOfTransitList() map {
      officeOfTransitList =>
        val numberOfTransitOffices = request.userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)
        val index: Seq[Index]      = List.range(0, numberOfTransitOffices).map(Index(_))
        val rows = index.flatMap {
          index =>
            Seq(
              routesCYAHelper.addAnotherTransitOffice(index, officeOfTransitList),
              routesCYAHelper.arrivalTimesAtOffice(index)
            ).flatten
        }
        Section(msg"officesOfTransit.checkYourAnswersLabel", rows)
    }

}
