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

import controllers.safetyAndSecurity.routes
import javax.inject.{Inject, Singleton}
import models._
import pages.Page
import pages.safetyAndSecurity.{
  AddCarrierEoriPage,
  AddCarrierPage,
  AddSafetyAndSecurityConsigneeEoriPage,
  AddSafetyAndSecurityConsigneePage,
  AddSafetyAndSecurityConsignorEoriPage,
  AddSafetyAndSecurityConsignorPage,
  CarrierAddressPage,
  CarrierEoriPage,
  CarrierNamePage,
  SafetyAndSecurityConsigneeAddressPage,
  SafetyAndSecurityConsigneeEoriPage,
  SafetyAndSecurityConsigneeNamePage,
  SafetyAndSecurityConsignorAddressPage,
  SafetyAndSecurityConsignorEoriPage,
  SafetyAndSecurityConsignorNamePage
}
import play.api.mvc.Call

@Singleton
class SafetyAndSecurityTraderDetailsNavigator @Inject()() extends Navigator {

  // format: off

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSafetyAndSecurityConsignorPage => ua => Some(addSafetyAndSecurityConsignorRoute(ua, NormalMode))
    case AddSafetyAndSecurityConsignorEoriPage => ua => addSafetyAndSecurityConsignorEori(ua)
    case SafetyAndSecurityConsignorEoriPage => ua => Some(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsignorNamePage => ua => Some(routes.SafetyAndSecurityConsignorAddressController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsignorAddressPage => ua => Some(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.id, NormalMode))
    case AddSafetyAndSecurityConsigneePage => ua => addSafetyAndSecurityConsignee(ua)
    case AddSafetyAndSecurityConsigneeEoriPage => ua => addSafetyAndSecurityConsigneeEoriPage(ua)
    case SafetyAndSecurityConsigneeEoriPage => ua => Some(routes.AddCarrierController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsigneeNamePage => ua => Some(routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsigneeAddressPage => ua => Some(routes.AddCarrierController.onPageLoad(ua.id, NormalMode))
    case AddCarrierPage => ua => addCarrierPage(ua)
    case AddCarrierEoriPage => ua => addCarrierEori(ua)
    case CarrierEoriPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)) // CYA not implemented
    case CarrierNamePage => ua => Some(routes.CarrierAddressController.onPageLoad(ua.id, NormalMode))
    case CarrierAddressPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSafetyAndSecurityConsignorPage => ua => Some(addSafetyAndSecurityConsignorRoute(ua, CheckMode))

  }

  private def addSafetyAndSecurityConsignorRoute(ua: UserAnswers, mode:Mode): Call =
    (ua.get(AddSafetyAndSecurityConsignorPage), ua.get(AddSafetyAndSecurityConsignorEoriPage), mode) match {
      case (Some(true), _,NormalMode)   => routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false),_, NormalMode)  => routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.id, NormalMode)
      case (Some(false), _, CheckMode) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), Some(_), CheckMode) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), None, CheckMode) => routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(ua.id, CheckMode)
    }

  def addSafetyAndSecurityConsignorEori(ua: UserAnswers): Option[Call] =
    ua.get(AddSafetyAndSecurityConsignorEoriPage).map {
      case true   => routes.SafetyAndSecurityConsignorEoriController.onPageLoad(ua.id, NormalMode)
      case false  => routes.SafetyAndSecurityConsignorNameController.onPageLoad(ua.id, NormalMode)
    }

  def addSafetyAndSecurityConsignee(ua: UserAnswers): Option[Call] =
    ua.get(AddSafetyAndSecurityConsigneePage).map {
      case true   => routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(ua.id, NormalMode)
      case false  => routes.AddCarrierController.onPageLoad(ua.id, NormalMode)
    }

  def addSafetyAndSecurityConsigneeEoriPage(ua: UserAnswers): Option[Call] =
    ua.get(AddSafetyAndSecurityConsigneeEoriPage).map {
      case true   => routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(ua.id, NormalMode)
      case false  => routes.SafetyAndSecurityConsigneeNameController.onPageLoad(ua.id, NormalMode)
    }

  def addCarrierPage(ua: UserAnswers): Option[Call] =
    ua.get(AddCarrierPage).map {
      case true   => routes.AddCarrierEoriController.onPageLoad(ua.id, NormalMode)
      case false  => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
    }

  def addCarrierEori(ua: UserAnswers): Option[Call] =
    ua.get(AddCarrierEoriPage).map {
      case true   => routes.CarrierEoriController.onPageLoad(ua.id, NormalMode)
      case false  => routes.CarrierNameController.onPageLoad(ua.id, NormalMode)
    }
}
