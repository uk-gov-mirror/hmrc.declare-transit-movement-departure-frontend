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

package navigation

import controllers.routeDetails.routes
import derivable.DeriveNumberOfOfficeOfTransits
import javax.inject.{Inject, Singleton}
import models._
import pages._
import pages.routeDetails.{
  AddAnotherTransitOfficePage,
  AddTransitOfficePage,
  ArrivalTimesAtOfficePage,
  ConfirmRemoveOfficeOfTransitPage,
  CountryOfDispatchPage,
  DestinationCountryPage,
  DestinationOfficePage,
  OfficeOfDeparturePage
}
import play.api.mvc.Call

@Singleton
class RouteDetailsNavigator @Inject()() extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case CountryOfDispatchPage =>
      ua =>
        Some(routes.OfficeOfDepartureController.onPageLoad(ua.id, NormalMode))
    case OfficeOfDeparturePage =>
      ua =>
        Some(routes.DestinationCountryController.onPageLoad(ua.id, NormalMode))
    case DestinationCountryPage =>
      ua =>
        Some(routes.DestinationOfficeController.onPageLoad(ua.id, NormalMode))
    case DestinationOfficePage =>
      ua =>
        Some(routes.AddAnotherTransitOfficeController.onPageLoad(ua.id, Index(0), NormalMode))
    case AddAnotherTransitOfficePage(index) =>
      ua =>
        Some(redirectToAddTransitOfficeNextPage(ua, index, NormalMode))
    case AddTransitOfficePage =>
      ua =>
        Some(addOfficeOfTransit(NormalMode, ua))
    case ArrivalTimesAtOfficePage(_) =>
      ua =>
        Some(routes.AddTransitOfficeController.onPageLoad(ua.id, NormalMode))
    case ConfirmRemoveOfficeOfTransitPage =>
      ua =>
        Some(removeOfficeOfTransit(NormalMode)(ua))

  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case page if isRouteDetailsSectionPage(page) =>
      ua =>
        Some(routes.RouteDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case _ =>
      _ =>
        None
  }

  def redirectToAddTransitOfficeNextPage(ua: UserAnswers, index: Index, mode: Mode): Call =
    ua.get(AddSecurityDetailsPage) match {
      case Some(isSelected) if isSelected => routes.ArrivalTimesAtOfficeController.onPageLoad(ua.id, index, mode)
      case _                              => routes.AddTransitOfficeController.onPageLoad(ua.id, mode)
    }

  private def isRouteDetailsSectionPage(page: Page): Boolean =
    page match {
      case CountryOfDispatchPage | OfficeOfDeparturePage | DestinationCountryPage | DestinationOfficePage | AddAnotherTransitOfficePage(_) |
          ArrivalTimesAtOfficePage(_) =>
        true
      case _ => false
    }

  private def addOfficeOfTransit(mode: Mode, userAnswers: UserAnswers): Call = {
    val count                     = userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)
    val maxNumberOfOfficesAllowed = 5
    userAnswers.get(AddTransitOfficePage) match {
      case Some(true) if count <= maxNumberOfOfficesAllowed =>
        val index = Index(count)
        routes.AddAnotherTransitOfficeController.onPageLoad(userAnswers.id, index, mode)
      case _ =>
        routes.RouteDetailsCheckYourAnswersController.onPageLoad(userAnswers.id)
    }
  }

  private def removeOfficeOfTransit(mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfOfficeOfTransits) match {
      case None | Some(0) => routes.AddAnotherTransitOfficeController.onPageLoad(ua.id, Index(0), mode)
      case _              => routes.AddTransitOfficeController.onPageLoad(ua.id, mode)
    }
}
