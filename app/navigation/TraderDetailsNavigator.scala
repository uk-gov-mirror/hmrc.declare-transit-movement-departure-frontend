/*
 * Copyright 2021 HM Revenue & Customs
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

import controllers.traderDetails.routes
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class TraderDetailsNavigator @Inject() () extends Navigator {

  /**
    * isPrincipalEoriKnownRoute
    * addConsignorRoute
    * isConsignorEoriKnownRoute
    * addConsigneeRoute
    * isConsigneeEoriKnownRoute
    */
  val normalRoutes: RouteMapping = {
    case IsPrincipalEoriKnownPage =>
      ua => Some(isPrincipalEoriKnownRoute(ua, NormalMode))
    case PrincipalNamePage       => reverseRouteToCall(NormalMode)(routes.PrincipalAddressController.onPageLoad(_, _))
    case PrincipalAddressPage    => reverseRouteToCall(NormalMode)(routes.AddConsignorController.onPageLoad(_, _))
    case WhatIsPrincipalEoriPage => reverseRouteToCall(NormalMode)(routes.AddConsignorController.onPageLoad(_, _))
    case AddConsignorPage =>
      ua => Some(addConsignorRoute(ua, NormalMode))
    case IsConsignorEoriKnownPage =>
      ua => Some(isConsignorEoriKnownRoute(ua, NormalMode))
    case ConsignorEoriPage    => reverseRouteToCall(NormalMode)(routes.ConsignorNameController.onPageLoad(_, _))
    case ConsignorNamePage    => reverseRouteToCall(NormalMode)(routes.ConsignorAddressController.onPageLoad(_, _))
    case ConsignorAddressPage => reverseRouteToCall(NormalMode)(routes.AddConsigneeController.onPageLoad(_, _))
    case AddConsigneePage =>
      ua => Some(addConsigneeRoute(ua, NormalMode))
    case IsConsigneeEoriKnownPage =>
      ua => Some(isConsigneeEoriKnownRoute(ua, NormalMode))
    case ConsigneeNamePage       => reverseRouteToCall(NormalMode)(routes.ConsigneeAddressController.onPageLoad(_, _))
    case WhatIsConsigneeEoriPage => reverseRouteToCall(NormalMode)(routes.ConsigneeNameController.onPageLoad(_, _))
    case ConsigneeAddressPage =>
      ua => Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  override def checkModeDefaultPage(userAnswers: UserAnswers): Call =
    routes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.id)

  override def checkRoutes: RouteMapping = {
    case IsPrincipalEoriKnownPage =>
      ua =>
        (ua.get(IsPrincipalEoriKnownPage), ua.get(WhatIsPrincipalEoriPage), ua.get(PrincipalNamePage)) match {
          case (Some(true), None, _)  => Some(routes.WhatIsPrincipalEoriController.onPageLoad(ua.id, CheckMode))
          case (Some(false), _, None) => Some(routes.PrincipalNameController.onPageLoad(ua.id, CheckMode))
          case (None, None, _)        => Some(routes.IsPrincipalEoriKnownController.onPageLoad(ua.id, NormalMode))
          case _                      => Some(checkModeDefaultPage(ua))
        }

    case PrincipalNamePage =>
      ua =>
        ua.get(PrincipalAddressPage) match {
          case Some(_) => Some(checkModeDefaultPage(ua))
          case None    => Some(routes.PrincipalAddressController.onPageLoad(ua.id, CheckMode))
        }

    case AddConsignorPage =>
      ua =>
        (ua.get(AddConsignorPage), ua.get(IsConsignorEoriKnownPage)) match {
          case (Some(true), None) => Some(routes.IsConsignorEoriKnownController.onPageLoad(ua.id, CheckMode))
          case (_, _)             => Some(checkModeDefaultPage(ua))
        }

    case IsConsignorEoriKnownPage =>
      ua =>
        (ua.get(IsConsignorEoriKnownPage), ua.get(ConsignorEoriPage)) match {
          case (Some(true), None) => Some(routes.ConsignorEoriController.onPageLoad(ua.id, CheckMode))
          case (_, _)             => Some(checkModeDefaultPage(ua))
        }

    case ConsignorEoriPage =>
      ua =>
        ua.get(ConsignorNamePage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsignorNameController.onPageLoad(ua.id, CheckMode))
        }

    case ConsignorNamePage =>
      ua =>
        ua.get(ConsignorAddressPage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsignorAddressController.onPageLoad(ua.id, CheckMode))
        }

    case AddConsigneePage =>
      ua =>
        (ua.get(AddConsigneePage), ua.get(IsConsigneeEoriKnownPage)) match {
          case (Some(true), None) => Some(routes.IsConsigneeEoriKnownController.onPageLoad(ua.id, CheckMode))
          case (_, _)             => Some(checkModeDefaultPage(ua))
        }

    case IsConsigneeEoriKnownPage =>
      ua =>
        (ua.get(IsConsigneeEoriKnownPage), ua.get(WhatIsConsigneeEoriPage)) match {
          case (Some(true), None) => Some(routes.WhatIsConsigneeEoriController.onPageLoad(ua.id, CheckMode))
          case (_, _)             => Some(checkModeDefaultPage(ua))
        }

    case WhatIsConsigneeEoriPage =>
      ua =>
        ua.get(ConsigneeNamePage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsigneeNameController.onPageLoad(ua.id, CheckMode))
        }

    case ConsigneeNamePage =>
      ua =>
        ua.get(ConsigneeAddressPage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsigneeAddressController.onPageLoad(ua.id, CheckMode))
        }
  }

  private def reverseRouteToCall(mode: Mode)(f: (LocalReferenceNumber, Mode) => Call): UserAnswers => Option[Call] =
    ua => Some(f(ua.id, mode))

  private def principalNamePageRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(PrincipalAddressPage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _       => routes.PrincipalAddressController.onPageLoad(ua.id, mode)
    }

  private def consignorEoriRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(ConsignorNamePage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _       => routes.ConsignorNameController.onPageLoad(ua.id, mode)
    }

  private def consignorNamePageRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(ConsignorAddressPage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _       => routes.ConsignorAddressController.onPageLoad(ua.id, mode)
    }

  private def consigneeEoriRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(ConsigneeNamePage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _       => routes.ConsigneeNameController.onPageLoad(ua.id, mode)
    }

  private def consigneeNamePageRoute(ua: UserAnswers, mode: Mode) =
    ua.get(ConsigneeAddressPage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _       => routes.ConsigneeAddressController.onPageLoad(ua.id, mode)
    }

  private def isTraderDetailsSectionPage(page: Page): Boolean =
    page match {
      case IsPrincipalEoriKnownPage | WhatIsPrincipalEoriPage | PrincipalNamePage | PrincipalAddressPage | IsConsignorEoriKnownPage | ConsignorEoriPage |
          ConsignorNamePage | ConsignorAddressPage | IsConsigneeEoriKnownPage | WhatIsConsigneeEoriPage | ConsigneeNamePage | ConsigneeAddressPage =>
        true
      case _ => false
    }

  private def isPrincipalEoriKnownRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(IsPrincipalEoriKnownPage) match {
      case Some(true)  => routes.WhatIsPrincipalEoriController.onPageLoad(ua.id, mode)
      case Some(false) => routes.PrincipalNameController.onPageLoad(ua.id, mode)
      case _           => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def addConsignorRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddConsignorPage), mode) match {
      case (Some(true), NormalMode)  => routes.IsConsignorEoriKnownController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode)   => routes.IsConsignorEoriKnownController.onPageLoad(ua.id, CheckMode)
      case (Some(false), NormalMode) => routes.ConsigneeForAllItemsController.onPageLoad(ua.id, NormalMode)
      case _                         => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def isConsignorEoriKnownRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(IsConsignorEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => routes.ConsignorEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode)  => routes.ConsignorEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(false), _)         => routes.ConsignorNameController.onPageLoad(ua.id, mode)
      case _                        => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def addConsigneeRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddConsigneePage), mode) match {
      case (Some(true), NormalMode)  => routes.IsConsigneeEoriKnownController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode)   => routes.IsConsigneeEoriKnownController.onPageLoad(ua.id, CheckMode)
      case (Some(false), NormalMode) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _                         => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def isConsigneeEoriKnownRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(IsConsigneeEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => routes.WhatIsConsigneeEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode)  => routes.WhatIsConsigneeEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(false), _)         => routes.ConsigneeNameController.onPageLoad(ua.id, mode)
      case _                        => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def consignorForAllRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(ConsignorForAllItemsPage), mode) match {
      case (Some(true), NormalMode)  => routes.ConsigneeForAllItemsController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode)   => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(false), NormalMode) => routes.AddConsignorController.onPageLoad(ua.id, NormalMode)
      case (Some(false), CheckMode)  => routes.AddConsignorController.onPageLoad(ua.id, CheckMode)
      case _                         => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def consigneeForAllRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(ConsigneeForAllItemsPage), mode) match {
      case (Some(false), _) => routes.AddConsigneeController.onPageLoad(ua.id, mode)
      case _                => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
}
