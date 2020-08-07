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

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject()() {
  private val normalRoutes: Page => UserAnswers => Call = {
    case LocalReferenceNumberPage => ua => routes.AddSecurityDetailsController.onPageLoad(ua.id, NormalMode)
    case DeclarationTypePage => ua => routes.ProcedureTypeController.onPageLoad(ua.id, NormalMode)
    case ProcedureTypePage => ua => routes.ContainersUsedPageController.onPageLoad(ua.id, NormalMode)
    case ContainersUsedPage => ua => routes.DeclarationPlaceController.onPageLoad(ua.id, NormalMode)
    case DeclarationPlacePage => ua => routes.DeclarationForSomeoneElseController.onPageLoad(ua.id, NormalMode)
    case DeclarationForSomeoneElsePage => ua => routes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
    case RepresentativeNamePage => ua => routes.RepresentativeCapacityController.onPageLoad(ua.id, NormalMode)
    case RepresentativeCapacityPage => ua => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case page if isMovementDetailsSectionPage(page) => ua => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case _ => ua => routes.CheckYourAnswersController.onPageLoad(ua.id)
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
}

