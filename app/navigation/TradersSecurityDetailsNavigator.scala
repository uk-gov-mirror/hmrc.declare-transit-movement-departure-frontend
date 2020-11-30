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

import controllers.addItems.traderSecurityDetails.routes
import javax.inject.{Inject, Singleton}
import models._
import pages.Page
import pages.addItems.traderSecurityDetails.{
  AddSecurityConsigneesEoriPage,
  AddSecurityConsignorsEoriPage,
  SecurityConsignorAddressPage,
  SecurityConsignorEoriPage,
  SecurityConsignorNamePage
}
import pages.safetyAndSecurity.AddSafetyAndSecurityConsigneePage
import play.api.mvc.Call

@Singleton
class TradersSecurityDetailsNavigator @Inject()() extends Navigator {

  // format: off
  //todo -update when Security Trader Details section done
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSecurityConsignorsEoriPage(index) => ua => addSecurityConsignorsEoriRoute(ua, index)
    case SecurityConsignorNamePage(index) => ua => Some(routes.SecurityConsignorAddressController.onPageLoad(ua.id, index, NormalMode))
    case SecurityConsignorEoriPage(index) => ua => securityConsignorEoriRoute(ua, index)
    case SecurityConsignorAddressPage(index) => ua => securityConsignorEoriRoute(ua, index)

  }


  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???

  private def securityConsignorEoriRoute(ua: UserAnswers, index: Index) = 
    ua.get(AddSafetyAndSecurityConsigneePage) match {
      case Some(true) => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case Some(false) => Some(routes.AddSecurityConsigneesEoriController.onPageLoad(ua.id, index, NormalMode))
    }
  
  private def addSecurityConsignorsEoriRoute(ua: UserAnswers, index: Index) = 
    ua.get(AddSecurityConsignorsEoriPage(index)) match {
      case Some(true) => Some(routes.SecurityConsignorEoriController.onPageLoad(ua.id, index, NormalMode))
      case Some(false) => Some(routes.SecurityConsignorNameController.onPageLoad(ua.id, index, NormalMode))
    }
  
  // format: on
}
