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
import controllers.traderDetails.{routes => traderDetailsRoutes}
import controllers.transportDetails.{routes => transportDetailsRoutes}
import controllers.routes
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class CommonNavigator @Inject()() extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case LocalReferenceNumberPage => ua => Some(routes.AddSecurityDetailsController.onPageLoad(ua.id, NormalMode))
    case AddSecurityDetailsPage => ua => Some(routes.DeclarationSummaryController.onPageLoad(ua.id))
    case CountryOfDispatchPage => ua => Some(routeDetailsRoutes.OfficeOfDepartureController.onPageLoad(ua.id, NormalMode))
    case OfficeOfDeparturePage => ua => Some(routeDetailsRoutes.DestinationCountryController.onPageLoad(ua.id, NormalMode))
    case IsPrincipalEoriKnownPage => ua => Some(isPrincipalEoriKnownRoute(ua, NormalMode))
    case PrincipalNamePage => ua => Some(traderDetailsRoutes.PrincipalAddressController.onPageLoad(ua.id, NormalMode))
    case PrincipalAddressPage => ua => Some(traderDetailsRoutes.AddConsignorController.onPageLoad(ua.id, NormalMode))
    case WhatIsPrincipalEoriPage => ua => Some(traderDetailsRoutes.AddConsignorController.onPageLoad(ua.id, NormalMode))
    case AddConsignorPage => ua => Some(addConsignorRoute(ua, NormalMode))
    case IsConsignorEoriKnownPage => ua => Some(isConsignorEoriKnownRoute(ua, NormalMode))
    case ConsignorEoriPage => ua => Some(traderDetailsRoutes.AddConsigneeController.onPageLoad(ua.id, NormalMode))
    case ConsignorNamePage => ua => Some(traderDetailsRoutes.ConsignorAddressController.onPageLoad(ua.id, NormalMode))
    case ConsignorAddressPage => ua => Some(traderDetailsRoutes.AddConsigneeController.onPageLoad(ua.id, NormalMode))
    case AddConsigneePage => ua => Some(addConsigneeRoute(ua, NormalMode))
    case IsConsigneeEoriKnownPage => ua => Some(isConsigneeEoriKnownRoute(ua, NormalMode))
    case ConsigneeNamePage => ua => Some(traderDetailsRoutes.ConsigneeAddressController.onPageLoad(ua.id, NormalMode))
    case ConsigneeAddressPage => ua => Some(traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case WhatIsConsigneeEoriPage => ua => Some(traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case AddIdAtDepartureLaterPage => ua => Some(transportDetailsRoutes.NationalityAtDepartureController.onPageLoad(ua.id, NormalMode))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case IsPrincipalEoriKnownPage => ua => Some(isPrincipalEoriKnownRoute(ua, CheckMode))
    case PrincipalNamePage => ua => Some(principalNamePageRoute(ua, CheckMode))
    case ConsignorNamePage => ua => Some(consignorNamePageRoute(ua, CheckMode))
    case ConsigneeNamePage => ua => Some(consigneeNamePageRoute(ua, CheckMode))
    case IsConsignorEoriKnownPage => ua => Some(isConsignorEoriKnownRoute(ua, CheckMode))
    case IsConsigneeEoriKnownPage => ua => Some(isConsigneeEoriKnownRoute(ua, CheckMode))

    case WhatIsPrincipalEoriPage => ua => Some(traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case ConsignorEoriPage => ua => Some(traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case WhatIsConsigneeEoriPage => ua => Some(traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case page if isTraderDetailsSectionPage(page) => ua => Some(traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case AddConsignorPage => ua => Some(addConsignorRoute(ua, CheckMode))
    case AddConsigneePage => ua => Some(addConsigneeRoute(ua, CheckMode))
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

