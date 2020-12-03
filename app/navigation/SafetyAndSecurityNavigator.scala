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
import derivable.DeriveNumberOfCountryOfRouting
import javax.inject.{Inject, Singleton}
import models._
import pages.safetyAndSecurity.{PlaceOfUnloadingCodePage, _}
import pages.{ModeAtBorderPage, Page}
import play.api.mvc.Call

@Singleton
class SafetyAndSecurityNavigator @Inject()() extends Navigator {

  // format: off

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddCircumstanceIndicatorPage             => ua => addCircumstanceIndicator(ua, NormalMode)
    case CircumstanceIndicatorPage                => ua => Some(routes.AddTransportChargesPaymentMethodController.onPageLoad(ua.id, NormalMode))
    case AddTransportChargesPaymentMethodPage     => ua => addTransportChargesPaymentMethod(ua, NormalMode)
    case TransportChargesPaymentMethodPage        => ua => Some(routes.AddCommercialReferenceNumberController.onPageLoad(ua.id, NormalMode))
    case AddCommercialReferenceNumberPage         => ua => addCommercialReferenceNumber(ua, NormalMode)
    case AddCommercialReferenceNumberAllItemsPage => ua => addCommercialReferenceNumberAllItems(ua, NormalMode)
    case CommercialReferenceNumberAllItemsPage    => ua => commercialReferenceNumberAllItems(ua)
    case AddConveyanceReferenceNumberPage         => ua => addConveyancerReferenceNumber(ua, NormalMode)
    case ConveyanceReferenceNumberPage            => ua => conveyanceReferenceNumber(ua)
    case AddPlaceOfUnloadingCodePage              => ua => addPlaceOfUnloadingCodePage(ua, NormalMode)
    case PlaceOfUnloadingCodePage                 => ua => placeOfUnloadingCode(ua)
    case CountryOfRoutingPage(_)                  => ua => Some(routes.AddAnotherCountryOfRoutingController.onPageLoad(ua.id, NormalMode))
    case AddAnotherCountryOfRoutingPage           => ua => addAnotherCountryOfRouting(ua, NormalMode)
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddCircumstanceIndicatorPage             => ua => addCircumstanceIndicator(ua, CheckMode)
    case CircumstanceIndicatorPage                => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddTransportChargesPaymentMethodPage     => ua => addTransportChargesPaymentMethod(ua, CheckMode)
    case TransportChargesPaymentMethodPage        => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddCommercialReferenceNumberPage         => ua => addCommercialReferenceNumber(ua, CheckMode)
    case AddCommercialReferenceNumberAllItemsPage => ua => addCommercialReferenceNumberAllItems(ua, CheckMode)
    case CommercialReferenceNumberAllItemsPage    => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddConveyanceReferenceNumberPage         => ua => addConveyancerReferenceNumber(ua, CheckMode)
    case ConveyanceReferenceNumberPage            => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddPlaceOfUnloadingCodePage              => ua => addPlaceOfUnloadingCodePage(ua, CheckMode)
    case PlaceOfUnloadingCodePage                 => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case CountryOfRoutingPage(_)                  => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddAnotherCountryOfRoutingPage           => ua => addAnotherCountryOfRouting(ua, CheckMode)
  }

  def addCircumstanceIndicator(ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(AddCircumstanceIndicatorPage).map {
      case true   => routes.CircumstanceIndicatorController.onPageLoad(ua.id, mode)
      case false if mode == NormalMode => routes.AddTransportChargesPaymentMethodController.onPageLoad(ua.id, NormalMode)
      case false  => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
    }

  def addTransportChargesPaymentMethod(ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(AddTransportChargesPaymentMethodPage).map {
      case true   => routes.TransportChargesPaymentMethodController.onPageLoad(ua.id, mode)
      case false if mode == NormalMode => routes.AddCommercialReferenceNumberController.onPageLoad(ua.id, NormalMode)
      case false  => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
    }

  def addCommercialReferenceNumber(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddCommercialReferenceNumberPage), ua.get(ModeAtBorderPage)) match {
      case (Some(true), _)                        => Some(routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(ua.id, mode))
      case (Some(false), _) if mode == CheckMode  => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(false), Some("4") | Some("40"))  => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false), _)                       => Some(routes.AddConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                                      => None
    }

  def addCommercialReferenceNumberAllItems(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddCommercialReferenceNumberAllItemsPage), ua.get(ModeAtBorderPage)) match {
      case (Some(true), _)                        => Some(routes.CommercialReferenceNumberAllItemsController.onPageLoad(ua.id, mode))
      case (Some(false), _) if mode == CheckMode  => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(false), Some("4") | Some("40"))  => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false), _)                       => Some(routes.AddConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                                      => None
    }

  def commercialReferenceNumberAllItems(ua: UserAnswers): Option[Call] =
    ua.get(ModeAtBorderPage) match {
      case Some("4") | Some("40") => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case Some(_)                => Some(routes.AddConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                      => None
    }

  def addConveyancerReferenceNumber(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddConveyanceReferenceNumberPage), ua.get(CircumstanceIndicatorPage)) match {
      case (Some(true), _)            => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, mode))
      case (Some(false), _) if mode == CheckMode  => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(false) , Some("E"))  => Some(routes.AddPlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case (Some(false) , _)          => Some(routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode ))
      case _                          => None
    }

  def conveyanceReferenceNumber(ua: UserAnswers): Option[Call] =
    (ua.get(CircumstanceIndicatorPage), ua.get(ConveyanceReferenceNumberPage)) match {
      case (Some("E"), Some(_)) => Some(routes.AddPlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case (_,         Some(_)) => Some(routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case _                    => None
    }

  def addPlaceOfUnloadingCodePage(ua: UserAnswers, mode: Mode): Option[Call] = {
    val totalNumberOfCountriesOfRouting = ua.get(DeriveNumberOfCountryOfRouting).getOrElse(0)

    ua.get(AddPlaceOfUnloadingCodePage).map {
      case true => routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, mode)
      case false if mode == CheckMode => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case false =>
        if (totalNumberOfCountriesOfRouting == 0) {
          routes.CountryOfRoutingController.onPageLoad(ua.id, Index(0), NormalMode)
        } else {
          routes.AddAnotherCountryOfRoutingController.onPageLoad(ua.id, NormalMode)
        }
    }
  }

  def addAnotherCountryOfRouting(ua: UserAnswers, mode: Mode): Option[Call] = {
    val totalNumberOfCountriesOfRouting = ua.get(DeriveNumberOfCountryOfRouting)
    
    ua.get(AddAnotherCountryOfRoutingPage).map {
      case true   => routes.CountryOfRoutingController.onPageLoad(ua.id, Index(totalNumberOfCountriesOfRouting.getOrElse(0)), mode)
      case false if mode == CheckMode => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case false  => routes.AddSafetyAndSecurityConsignorController.onPageLoad(ua.id, NormalMode)
    }
  }

  def placeOfUnloadingCode(ua: UserAnswers): Option[Call] = {
    val totalNumberOfCountriesOfRouting = ua.get(DeriveNumberOfCountryOfRouting).getOrElse(0)

    ua.get(PlaceOfUnloadingCodePage).map {
      _ =>
        if (totalNumberOfCountriesOfRouting == 0) {
          routes.CountryOfRoutingController.onPageLoad(ua.id, Index(0), NormalMode)
        } else {
          routes.AddAnotherCountryOfRoutingController.onPageLoad(ua.id, NormalMode)
        }
    }
  }

  // format: on
}
