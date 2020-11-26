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

import controllers.safetyAndSecurity.routes
import javax.inject.{Inject, Singleton}
import models._
import pages.safetyAndSecurity._
import pages.{ModeAtBorderPage, Page}
import play.api.mvc.Call

@Singleton
class SafetyAndSecurityNavigator @Inject()() extends Navigator {

  // format: off

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddCircumstanceIndicatorPage             => ua => addCircumstanceIndicator(ua)
    case CircumstanceIndicatorPage                => ua => Some(routes.AddTransportChargesPaymentMethodController.onPageLoad(ua.id, NormalMode))
    case AddTransportChargesPaymentMethodPage     => ua => addTransportChargesPaymentMethod(ua)
    case TransportChargesPaymentMethodPage        => ua => Some(routes.AddCommercialReferenceNumberController.onPageLoad(ua.id, NormalMode))
    case AddCommercialReferenceNumberPage         => ua => addCommercialReferenceNumber(ua)
    case AddCommercialReferenceNumberAllItemsPage => ua => addCommercialReferenceNumberAllItems(ua)
    case AddConveyancerReferenceNumberPage        => ua => addConveyancerReferenceNumber(ua)
    case ConveyanceReferenceNumberPage            => ua => conveyanceReferenceNumber(ua)
    case AddPlaceOfUnloadingCodePage              => ua => addPlaceOfUnloadingCodePage(ua)
    case PlaceOfUnloadingCodePage                 => ua => Some(routes.CountryOfRoutingController.onPageLoad(ua.id, NormalMode))
    case CountryOfRoutingPage                     => ua => Some(routes.AddAnotherCountryOfRoutingController.onPageLoad(ua.id, NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???



  def addCircumstanceIndicator(ua: UserAnswers): Option[Call] =
    ua.get(AddCircumstanceIndicatorPage).map {
      case true   => routes.CircumstanceIndicatorController.onPageLoad(ua.id, NormalMode)
      case false  => routes.AddTransportChargesPaymentMethodController.onPageLoad(ua.id, NormalMode)
    }

  def addTransportChargesPaymentMethod(ua: UserAnswers): Option[Call] =
    ua.get(AddTransportChargesPaymentMethodPage).map {
      case true   => routes.TransportChargesPaymentMethodController.onPageLoad(ua.id, NormalMode)
      case false  => routes.AddCommercialReferenceNumberController.onPageLoad(ua.id, NormalMode)
    }

  def addCommercialReferenceNumber(ua: UserAnswers): Option[Call] =
    (ua.get(AddCommercialReferenceNumberPage), ua.get(ModeAtBorderPage)) match {
      case (Some(true), _)                        => Some(routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(ua.id, NormalMode))
      case (Some(false), Some("4") | Some("40"))  => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false), _)                       => Some(routes.AddConveyancerReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                                      => None
    }

  def addCommercialReferenceNumberAllItems(ua: UserAnswers): Option[Call] =
    (ua.get(AddCommercialReferenceNumberAllItemsPage), ua.get(ModeAtBorderPage)) match {
      case (Some(true), _)                        => Some(routes.CommercialReferenceNumberAllItemsController.onPageLoad(ua.id, NormalMode))
      case (Some(false), Some("4") | Some("40"))  => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false), _)                       => Some(routes.AddConveyancerReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                                      => None
    }

  def addConveyancerReferenceNumber(ua: UserAnswers): Option[Call] =
    (ua.get(AddConveyancerReferenceNumberPage), ua.get(CircumstanceIndicatorPage)) match {
      case (Some(true), _)            => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false) , Some("E"))  => Some(routes.AddPlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case (Some(false) , Some(_))    => Some(routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode ))
      case _                          => None
    }

  def conveyanceReferenceNumber(ua: UserAnswers): Option[Call] =
    (ua.get(CircumstanceIndicatorPage), ua.get(ConveyanceReferenceNumberPage)) match {
      case (Some("E"), Some(_)) => Some(routes.AddPlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case (Some(_),   Some(_)) => Some(routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case _                    => None
    }

  def addPlaceOfUnloadingCodePage(ua: UserAnswers): Option[Call] =
    ua.get(AddPlaceOfUnloadingCodePage).map {
      case true   => routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode)
      case false  => routes.CountryOfRoutingController.onPageLoad(ua.id, NormalMode)
    }

  
  // format: on
}
