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

import controllers.traderDetails.routes
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class TraderDetailsNavigator @Inject()() extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case IsPrincipalEoriKnownPage =>
      ua =>
        Some(isPrincipalEoriKnownRoute(ua, NormalMode))
    case PrincipalNamePage =>
      ua =>
        Some(routes.PrincipalAddressController.onPageLoad(ua.id, NormalMode))
    case PrincipalAddressPage =>
      ua =>
        Some(routes.AddConsignorController.onPageLoad(ua.id, NormalMode))
    case WhatIsPrincipalEoriPage =>
      ua =>
        Some(routes.AddConsignorController.onPageLoad(ua.id, NormalMode))
    case AddConsignorPage =>
      ua =>
        Some(addConsignorRoute(ua, NormalMode))
    case IsConsignorEoriKnownPage =>
      ua =>
        Some(isConsignorEoriKnownRoute(ua, NormalMode))
    case ConsignorEoriPage =>
      ua =>
        Some(routes.AddConsigneeController.onPageLoad(ua.id, NormalMode))
    case ConsignorNamePage =>
      ua =>
        Some(routes.ConsignorAddressController.onPageLoad(ua.id, NormalMode))
    case ConsignorAddressPage =>
      ua =>
        Some(routes.AddConsigneeController.onPageLoad(ua.id, NormalMode))
    case AddConsigneePage =>
      ua =>
        Some(addConsigneeRoute(ua, NormalMode))
    case IsConsigneeEoriKnownPage =>
      ua =>
        Some(isConsigneeEoriKnownRoute(ua, NormalMode))
    case ConsigneeNamePage =>
      ua =>
        Some(routes.ConsigneeAddressController.onPageLoad(ua.id, NormalMode))
    case ConsigneeAddressPage =>
      ua =>
        Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case WhatIsConsigneeEoriPage =>
      ua =>
        Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case IsPrincipalEoriKnownPage =>
      ua =>
        Some(isPrincipalEoriKnownRoute(ua, CheckMode))
    case PrincipalNamePage =>
      ua =>
        Some(principalNamePageRoute(ua, CheckMode))
    case ConsignorNamePage =>
      ua =>
        Some(consignorNamePageRoute(ua, CheckMode))
    case ConsigneeNamePage =>
      ua =>
        Some(consigneeNamePageRoute(ua, CheckMode))
    case IsConsignorEoriKnownPage =>
      ua =>
        Some(isConsignorEoriKnownRoute(ua, CheckMode))
    case IsConsigneeEoriKnownPage =>
      ua =>
        Some(isConsigneeEoriKnownRoute(ua, CheckMode))

    case WhatIsPrincipalEoriPage =>
      ua =>
        Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case ConsignorEoriPage =>
      ua =>
        Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case WhatIsConsigneeEoriPage =>
      ua =>
        Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case page if isTraderDetailsSectionPage(page) =>
      ua =>
        Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case AddConsignorPage =>
      ua =>
        Some(addConsignorRoute(ua, CheckMode))
    case AddConsigneePage =>
      ua =>
        Some(addConsigneeRoute(ua, CheckMode))
  }

  private def principalNamePageRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(PrincipalAddressPage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _       => routes.PrincipalAddressController.onPageLoad(ua.id, mode)
    }

  private def consignorNamePageRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(ConsignorAddressPage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case _       => routes.ConsignorAddressController.onPageLoad(ua.id, mode)
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
      case (Some(false), NormalMode) => routes.AddConsigneeController.onPageLoad(ua.id, NormalMode)
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

}
