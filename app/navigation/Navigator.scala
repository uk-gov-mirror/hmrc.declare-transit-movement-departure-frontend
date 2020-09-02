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
import controllers.routes
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case LocalReferenceNumberPage => ua => routes.AddSecurityDetailsController.onPageLoad(ua.id, NormalMode)
    case AddSecurityDetailsPage => ua => routes.DeclarationSummaryController.onPageLoad(ua.id)
    case CountryOfDispatchPage => ua => routeDetailsRoutes.OfficeOfDepartureController.onPageLoad(ua.id, NormalMode)
    case PrincipalNamePage => ua => routes.PrincipalAddressController.onPageLoad(ua.id, NormalMode) //TODO: come back to this when working on navigation
    case ConsignorNamePage => ua => routes.ConsignorAddressController.onPageLoad(ua.id, NormalMode)
    case ConsigneeNamePage => ua => routes.ConsigneeAddressController.onPageLoad(ua.id, NormalMode)
    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = (_ => ua => routes.CheckYourAnswersController.onPageLoad(ua.id))

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}

