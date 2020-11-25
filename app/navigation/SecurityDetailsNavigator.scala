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

import controllers.addItems.securityDetails.routes
import javax.inject.{Inject, Singleton}
import models._
import pages.Page
import pages.addItems.securityDetails._
import play.api.mvc.Call

@Singleton
class SecurityDetailsNavigator @Inject()() extends Navigator {

  // format: off
  //todo -update when Safety and Security section built
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TransportChargesPage(index) => ua => Some(routes.CommercialReferenceNumberController.onPageLoad(ua.id, index, NormalMode))//todo update when Safety and security section built
    case CommercialReferenceNumberPage(index) => ua => Some(routes.AddDangerousGoodsCodeController.onPageLoad(ua.id, index, NormalMode))
    case AddDangerousGoodsCodePage(index) => ua => addDangerousGoodsCodeRoute(ua, index, NormalMode)
    case DangerousGoodsCodePage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TransportChargesPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case CommercialReferenceNumberPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddDangerousGoodsCodePage(index) => ua => addDangerousGoodsCodeRoute(ua, index, CheckMode)
    case DangerousGoodsCodePage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))

  }

   private def addDangerousGoodsCodeRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddDangerousGoodsCodePage(index)), ua.get(DangerousGoodsCodePage(index)), mode) match {
      case (Some(true), _, NormalMode) => Some(routes.DangerousGoodsCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _, NormalMode) =>
        Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)) //todo -update when Security Trader Details section done
      case (Some(true), None, CheckMode)    => Some(routes.DangerousGoodsCodeController.onPageLoad(ua.id, index, CheckMode))
      case (Some(true), Some(_), CheckMode) => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false), _, CheckMode)      => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }
  // format: on
}
