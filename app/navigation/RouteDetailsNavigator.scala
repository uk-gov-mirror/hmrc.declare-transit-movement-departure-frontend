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

import controllers.routeDetails.{routes => routeDetailsRoutes}
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class RouteDetailsNavigator @Inject()() extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case CountryOfDispatchPage => ua => Some(routeDetailsRoutes.OfficeOfDepartureController.onPageLoad(ua.id, NormalMode))
    case OfficeOfDeparturePage => ua => Some(routeDetailsRoutes.DestinationCountryController.onPageLoad(ua.id, NormalMode))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case _ => _ => None
  }

}

