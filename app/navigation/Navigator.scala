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
    case AddSecurityDetailsPage => ua => routes.DeclarationSummaryController.onPageLoad(ua.id)
    case DeclarationTypePage => ua => routes.ProcedureTypeController.onPageLoad(ua.id, NormalMode)
    case ProcedureTypePage => ua => routes.ContainersUsedPageController.onPageLoad(ua.id, NormalMode)
    case ContainersUsedPage => ua => routes.DeclarationPlaceController.onPageLoad(ua.id, NormalMode)
    case DeclarationPlacePage => ua => routes.DeclarationForSomeoneElseController.onPageLoad(ua.id, NormalMode)
    case DeclarationForSomeoneElsePage => ua => isDeclarationForSomeoneElse(ua, NormalMode)
    case RepresentativeNamePage => ua => routes.RepresentativeCapacityController.onPageLoad(ua.id, NormalMode)
    case RepresentativeCapacityPage => ua => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)

    case IsPrincipalEoriKnownPage => ua => isPrincipalEoriKnownRoute(ua, NormalMode)
    case PrincipalNamePage => ua => routes.PrincipalAddressController.onPageLoad(ua.id, NormalMode) //TODO: come back to this when working on navigation
    case PrincipalAddressPage => ua => routes.AddConsignorController.onPageLoad(ua.id, NormalMode) //TODO: come back to this when working on navigation
    case WhatIsPrincipalEoriPage => ua => routes.AddConsignorController.onPageLoad(ua.id, NormalMode) //TODO: come back to this when working on navigation
    case AddConsignorPage => ua => addConsignorRoute(ua, NormalMode)
    case IsConsignorEoriKnownPage => ua => isConsignorEoriKnownRoute(ua, NormalMode)
    case ConsignorEoriPage => ua => routes.AddConsigneeController.onPageLoad(ua.id, NormalMode)
    case ConsignorNamePage => ua => routes.ConsignorAddressController.onPageLoad(ua.id, NormalMode)
    case ConsignorAddressPage => ua => routes.AddConsigneeController.onPageLoad(ua.id, NormalMode)
    case AddConsigneePage => ua => addConsigneeRoute(ua, NormalMode)
    case IsConsigneeEoriKnownPage => ua => isConsigneeEoriKnownRoute(ua, NormalMode)
    case ConsigneeNamePage => ua => routes.ConsigneeAddressController.onPageLoad(ua.id, NormalMode)
    case ConsigneeAddressPage => ua => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case WhatIsConsigneeEoriPage => ua => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case DeclarationForSomeoneElsePage => ua => isDeclarationForSomeoneElse(ua, CheckMode)
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

  private def isDeclarationForSomeoneElse(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(DeclarationForSomeoneElsePage), ua.get(RepresentativeNamePage), mode) match {
      case (Some(true), None, CheckMode) => routes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case (Some(true), _, NormalMode) => routes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case _ => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
  private def isPrincipalEoriKnownRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(IsPrincipalEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => routes.WhatIsPrincipalEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false), NormalMode) => routes.PrincipalNameController.onPageLoad(ua.id, NormalMode)
      case _ => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
  private def addConsignorRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(AddConsignorPage), mode) match {
      case (Some(true), NormalMode) => routes.IsConsignorEoriKnownController.onPageLoad(ua.id, NormalMode)
      case (Some(false), NormalMode) => routes.AddConsigneeController.onPageLoad(ua.id, NormalMode)
      case _ => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
  private def isConsignorEoriKnownRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(IsConsignorEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => routes.ConsignorEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false), NormalMode) => routes.ConsignorNameController.onPageLoad(ua.id, NormalMode)
      case _ => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
  private def addConsigneeRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(AddConsigneePage), mode) match {
      case (Some(true), NormalMode) => routes.IsConsigneeEoriKnownController.onPageLoad(ua.id, NormalMode)
      case (Some(false), NormalMode) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _ => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
  private def isConsigneeEoriKnownRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(IsConsigneeEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => routes.WhatIsConsigneeEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false), NormalMode) => routes.ConsigneeNameController.onPageLoad(ua.id, NormalMode)
      case _ => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

}

