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

import controllers.goodsSummary.routes
import javax.inject.{Inject, Singleton}
import models.ProcedureType.{Normal, Simplified}
import models._
import navigation.annotations.MovementDetails
import pages._
import play.api.mvc.Call

@Singleton
class GoodsSummaryNavigator @Inject()() extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case DeclarePackagesPage => ua => Some(declarePackageRoute(ua))
    case TotalPackagesPage => ua => Some(routes.TotalGrossMassController.onPageLoad(ua.id, NormalMode))
    case TotalGrossMassPage => ua => Some(totalGrossMassRoute(ua))
    case AuthorisedLocationCodePage => ua => Some(routes.ControlResultDateLimitController.onPageLoad(ua.id, NormalMode))
    case AddCustomsApprovedLocationPage => ua => Some(addCustomsApprovedLocationRoute(ua))
    case ControlResultDateLimitPage => ua => Some(routes.AddSealsController.onPageLoad(ua.id, NormalMode))
    case CustomsApprovedLocationPage => ua => Some(routes.AddSealsController.onPageLoad(ua.id, NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {???}


  def declarePackageRoute(ua: UserAnswers): Call = {
    ua.get(DeclarePackagesPage) match {
      case Some(true) => routes.TotalPackagesController.onPageLoad(ua.id, NormalMode)
      case Some(false) => routes.TotalGrossMassController.onPageLoad(ua.id, NormalMode)
    }
  }

  def totalGrossMassRoute(ua: UserAnswers): Call = {
    ua.get(ProcedureTypePage) match {
      case Some(Normal) => routes.AddCustomsApprovedLocationController.onPageLoad(ua.id, NormalMode)
      case Some(Simplified) => routes.AuthorisedLocationCodeController.onPageLoad(ua.id, NormalMode)
    }
  }

  def addCustomsApprovedLocationRoute(ua: UserAnswers): Call = {
    ua.get(AddCustomsApprovedLocationPage) match {
      case Some(true) => routes.CustomsApprovedLocationController.onPageLoad(ua.id, NormalMode)
      case Some(false) => routes.AddSealsController.onPageLoad(ua.id, NormalMode)
    }
  }
}

