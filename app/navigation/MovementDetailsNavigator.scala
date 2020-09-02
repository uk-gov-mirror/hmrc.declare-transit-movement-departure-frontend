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

import controllers.movementDetails.routes
import controllers.{routes => mainRoutes}
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class MovementDetailsNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DeclarationTypePage => ua => routes.ProcedureTypeController.onPageLoad(ua.id, NormalMode)
    case ProcedureTypePage => ua => routes.ContainersUsedPageController.onPageLoad(ua.id, NormalMode)
    case ContainersUsedPage => ua => routes.DeclarationPlaceController.onPageLoad(ua.id, NormalMode)
    case DeclarationPlacePage => ua => routes.DeclarationForSomeoneElseController.onPageLoad(ua.id, NormalMode)
    case DeclarationForSomeoneElsePage => ua => isDeclarationForSomeoneElse(ua, NormalMode)
    case RepresentativeNamePage => ua => routes.RepresentativeCapacityController.onPageLoad(ua.id, NormalMode)
    case RepresentativeCapacityPage => ua => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case _ => _ => mainRoutes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case DeclarationForSomeoneElsePage => ua => isDeclarationForSomeoneElse(ua, CheckMode)
    case page if isMovementDetailsSectionPage(page) => ua => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case _ => ua => mainRoutes.CheckYourAnswersController.onPageLoad(ua.id)
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  private def isMovementDetailsSectionPage(page: Page): Boolean = {
     page match {
       case DeclarationTypePage| ProcedureTypePage | ContainersUsedPage |
            DeclarationPlacePage | DeclarationForSomeoneElsePage | RepresentativeNamePage | RepresentativeCapacityPage=> true
       case _ => false
     }
  }

  private def isDeclarationForSomeoneElse(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(DeclarationForSomeoneElsePage), ua.get(RepresentativeNamePage), mode) match {
      case (Some(true), None, CheckMode) => routes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case (Some(true), _, NormalMode) => routes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case _ => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
}

