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

import controllers.movementDetails.{routes => movementDetailsRoutes}
import controllers.routeDetails.{routes => routeDetailsRoutes}
import controllers.traderDetails.{routes => traderDetailsRoutes}
import controllers.transportDetails.{routes => transportDetailsRoutes}
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
    case DeclarationTypePage => ua => movementDetailsRoutes.ProcedureTypeController.onPageLoad(ua.id, NormalMode)
    case ProcedureTypePage => ua => movementDetailsRoutes.ContainersUsedPageController.onPageLoad(ua.id, NormalMode)
    case ContainersUsedPage => ua => movementDetailsRoutes.DeclarationPlaceController.onPageLoad(ua.id, NormalMode)
    case DeclarationPlacePage => ua => movementDetailsRoutes.DeclarationForSomeoneElseController.onPageLoad(ua.id, NormalMode)
    case DeclarationForSomeoneElsePage => ua => isDeclarationForSomeoneElse(ua, NormalMode)
    case RepresentativeNamePage => ua => movementDetailsRoutes.RepresentativeCapacityController.onPageLoad(ua.id, NormalMode)
    case RepresentativeCapacityPage => ua => movementDetailsRoutes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case CountryOfDispatchPage => ua => routeDetailsRoutes.OfficeOfDepartureController.onPageLoad(ua.id, NormalMode)
    case OfficeOfDeparturePage => ua => routeDetailsRoutes.DestinationCountryController.onPageLoad(ua.id, NormalMode)
    case IsPrincipalEoriKnownPage => ua => isPrincipalEoriKnownRoute(ua, NormalMode)
    case PrincipalNamePage => ua => traderDetailsRoutes.PrincipalAddressController.onPageLoad(ua.id, NormalMode)
    case PrincipalAddressPage => ua => traderDetailsRoutes.AddConsignorController.onPageLoad(ua.id, NormalMode)
    case WhatIsPrincipalEoriPage => ua => traderDetailsRoutes.AddConsignorController.onPageLoad(ua.id, NormalMode)
    case AddConsignorPage => ua => addConsignorRoute(ua, NormalMode)
    case IsConsignorEoriKnownPage => ua => isConsignorEoriKnownRoute(ua, NormalMode)
    case ConsignorEoriPage => ua => traderDetailsRoutes.AddConsigneeController.onPageLoad(ua.id, NormalMode)
    case ConsignorNamePage => ua => traderDetailsRoutes.ConsignorAddressController.onPageLoad(ua.id, NormalMode)
    case ConsignorAddressPage => ua => traderDetailsRoutes.AddConsigneeController.onPageLoad(ua.id, NormalMode)
    case AddConsigneePage => ua => addConsigneeRoute(ua, NormalMode)
    case IsConsigneeEoriKnownPage => ua => isConsigneeEoriKnownRoute(ua, NormalMode)
    case ConsigneeNamePage => ua => traderDetailsRoutes.ConsigneeAddressController.onPageLoad(ua.id, NormalMode)
    case ConsigneeAddressPage => ua => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case WhatIsConsigneeEoriPage => ua => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case AddIdAtDepartureLaterPage => ua => transportDetailsRoutes.NationalityAtDepartureController.onPageLoad(ua.id, NormalMode)
    case InlandModePage => ua => transportDetailsRoutes.AddIdAtDepartureController.onPageLoad(ua.id, NormalMode)
    case AddIdAtDepartureLaterPage => ua => transportDetailsRoutes.NationalityAtDepartureController.onPageLoad(ua.id, NormalMode)
    case IdAtDeparturePage => ua => transportDetailsRoutes.NationalityAtDepartureController.onPageLoad(ua.id, NormalMode)
    case NationalityAtDeparturePage => ua => transportDetailsRoutes.ChangeAtBorderController.onPageLoad(ua.id, NormalMode)
    case AddIdAtDeparturePage => ua => addIdAtDepartureRoute(ua, NormalMode)
    case ChangeAtBorderPage => ua => changeAtBorderRoute(ua, NormalMode)
    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case DeclarationForSomeoneElsePage => ua => isDeclarationForSomeoneElse(ua, CheckMode)
    case page if isMovementDetailsSectionPage(page) => ua => movementDetailsRoutes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case IsPrincipalEoriKnownPage => ua => isPrincipalEoriKnownRoute(ua, CheckMode)
    case PrincipalNamePage => ua => principalNamePageRoute(ua, CheckMode)
    case ConsignorNamePage => ua => consignorNamePageRoute(ua, CheckMode)
    case ConsigneeNamePage => ua => consigneeNamePageRoute(ua, CheckMode)
    case IsConsignorEoriKnownPage => ua => isConsignorEoriKnownRoute(ua, CheckMode)
    case IsConsigneeEoriKnownPage => ua => isConsigneeEoriKnownRoute(ua, CheckMode)

    case WhatIsPrincipalEoriPage => ua => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case ConsignorEoriPage => ua => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case WhatIsConsigneeEoriPage => ua => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case page if isMovementDetailsSectionPage(page) => ua => movementDetailsRoutes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case page if isTraderDetailsSectionPage(page) => ua => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    case AddConsignorPage => ua => addConsignorRoute(ua, CheckMode)
    case AddConsigneePage => ua => addConsigneeRoute(ua, CheckMode)
    case _ => ua => routes.CheckYourAnswersController.onPageLoad(ua.id)
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  def addIdAtDepartureRoute (ua: UserAnswers, mode: Mode): Call = {
    ua.get(AddIdAtDeparturePage) match {
      case Some(true) => transportDetailsRoutes.IdAtDepartureController.onPageLoad(ua.id, mode)
      case Some(false) => transportDetailsRoutes.AddIdAtDepartureLaterController.onPageLoad(ua.id)
      case _ => transportDetailsRoutes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

  def changeAtBorderRoute (ua: UserAnswers, mode: Mode): Call = {
    ua.get(ChangeAtBorderPage) match {
      case Some(true) => transportDetailsRoutes.ModeAtBorderController.onPageLoad(ua.id, mode)
      case _ => transportDetailsRoutes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }


  private def isMovementDetailsSectionPage(page: Page): Boolean = {
    page match {
      case DeclarationTypePage | ProcedureTypePage | ContainersUsedPage |
           DeclarationPlacePage | DeclarationForSomeoneElsePage | RepresentativeNamePage | RepresentativeCapacityPage => true
      case _ => false
    }
  }

  private def principalNamePageRoute(ua: UserAnswers, mode: Mode) = {
    ua.get(PrincipalAddressPage) match {
      case Some(_) => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _ => traderDetailsRoutes.PrincipalAddressController.onPageLoad(ua.id, mode)
    }
  }

  private def consignorNamePageRoute(ua: UserAnswers, mode: Mode) = {
    ua.get(ConsignorAddressPage) match {
      case Some(_) => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _ => traderDetailsRoutes.ConsignorAddressController.onPageLoad(ua.id, mode)
    }
  }

  private def consigneeNamePageRoute(ua: UserAnswers, mode: Mode) = {
    ua.get(ConsigneeAddressPage) match {
      case Some(_) => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _ => traderDetailsRoutes.ConsigneeAddressController.onPageLoad(ua.id, mode)
    }
  }

  private def isTraderDetailsSectionPage(page: Page): Boolean = {
    page match {
      case IsPrincipalEoriKnownPage | WhatIsPrincipalEoriPage | PrincipalNamePage | PrincipalAddressPage |
           IsConsignorEoriKnownPage | ConsignorEoriPage | ConsignorNamePage | ConsignorAddressPage |
           IsConsigneeEoriKnownPage | WhatIsConsigneeEoriPage | ConsigneeNamePage | ConsigneeAddressPage => true
      case _ => false
    }
  }

  private def isDeclarationForSomeoneElse(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(DeclarationForSomeoneElsePage), ua.get(RepresentativeNamePage), mode) match {
      case (Some(true), None, CheckMode) => movementDetailsRoutes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case (Some(true), _, NormalMode) => movementDetailsRoutes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case _ => movementDetailsRoutes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

  private def isPrincipalEoriKnownRoute(ua: UserAnswers, mode: Mode): Call = {
    ua.get(IsPrincipalEoriKnownPage) match {
      case Some(true) => traderDetailsRoutes.WhatIsPrincipalEoriController.onPageLoad(ua.id, mode)
      case Some(false) => traderDetailsRoutes.PrincipalNameController.onPageLoad(ua.id, mode)
      case _ => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

  private def addConsignorRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(AddConsignorPage), mode) match {
      case (Some(true), NormalMode) => traderDetailsRoutes.IsConsignorEoriKnownController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode) => traderDetailsRoutes.IsConsignorEoriKnownController.onPageLoad(ua.id, CheckMode)
      case (Some(false), NormalMode) => traderDetailsRoutes.AddConsigneeController.onPageLoad(ua.id, NormalMode)
      case _ => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

  private def isConsignorEoriKnownRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(IsConsignorEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => traderDetailsRoutes.ConsignorEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode) => traderDetailsRoutes.ConsignorEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(false), _) => traderDetailsRoutes.ConsignorNameController.onPageLoad(ua.id, mode)
      case _ => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

  private def addConsigneeRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(AddConsigneePage), mode) match {
      case (Some(true), NormalMode) => traderDetailsRoutes.IsConsigneeEoriKnownController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode) => traderDetailsRoutes.IsConsigneeEoriKnownController.onPageLoad(ua.id, CheckMode)
      case (Some(false), NormalMode) => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _ => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

  private def isConsigneeEoriKnownRoute(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(IsConsigneeEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => traderDetailsRoutes.WhatIsConsigneeEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode) => traderDetailsRoutes.WhatIsConsigneeEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(false), _) => traderDetailsRoutes.ConsigneeNameController.onPageLoad(ua.id, mode)
      case _ => traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

}

