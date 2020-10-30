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

import controllers.routes
import controllers.addItems.{routes => addItemsRoutes}
import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import javax.inject.{Inject, Singleton}
import models._
import pages._
import pages.addItems._
import pages.addItems.traderDetails._
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                  => ua => Some(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index)               => ua => Some(addItemsRoutes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index)                  => ua=>  addTotalNessMassRoute(index, ua,  NormalMode)
    case TotalNetMassPage(index)                     => ua => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index)             => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case CommodityCodePage(index)                    => ua => Some(addItemsRoutes.AddItemsSameConsignorForAllItemsController.onPageLoad(ua.id,index, NormalMode))
    case AddItemsSameConsignorForAllItemsPage(index) => ua => addItemsSameConsignorForAllItems(ua, index, NormalMode)
    case TraderDetailsConsignorEoriKnownPage(index)  => ua => consignorEoriKnown(ua, index, NormalMode)
    case TraderDetailsConsignorEoriNumberPage(index) => ua => consignorEoriNumber(ua, index, NormalMode)
    case TraderDetailsConsignorNamePage(index)       => ua => consignorName(ua, index, NormalMode)
    case TraderDetailsConsignorAddressPage(index)    => ua => consignorAddress(ua, index, NormalMode)
    case AddItemsSameConsigneeForAllItemsPage(index) => ua => addItemsSameConsigneeForAllItems(ua, index, NormalMode)
    case TraderDetailsConsigneeEoriKnownPage(index)  => ua => consigneeEoriKnown(ua, index, NormalMode)
    case TraderDetailsConsigneeEoriNumberPage(index) => ua => consigneeEoriNumber(ua, index, NormalMode)
    case TraderDetailsConsigneeNamePage(index)       => ua => consigneeName(ua, index, NormalMode)
    case TraderDetailsConsigneeAddressPage(index)    => ua => consigneeAddress(ua, index, NormalMode)
  }

  //TODO: Need to refactor this code
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                  => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index)               => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index)                  => ua => addTotalNessMassRoute(index, ua,  CheckMode)
    case IsCommodityCodeKnownPage(index)             => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index)                    => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    case TotalNetMassPage(index)                     => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    case AddItemsSameConsignorForAllItemsPage(index) => ua => addItemsSameConsignorForAllItems(ua, index, CheckMode)
    case TraderDetailsConsignorEoriKnownPage(index)  => ua => consignorEoriKnown(ua, index, CheckMode)
    case TraderDetailsConsignorEoriNumberPage(index) => ua => consignorEoriNumber(ua, index, CheckMode)
    case TraderDetailsConsignorNamePage(index)       => ua => consignorName(ua, index, CheckMode)
    case TraderDetailsConsignorAddressPage(index)    => ua => consignorAddress(ua, index, CheckMode)
    case AddItemsSameConsigneeForAllItemsPage(index) => ua => addItemsSameConsigneeForAllItems(ua, index, CheckMode)
    case TraderDetailsConsigneeEoriKnownPage(index)  => ua => consigneeEoriKnown(ua, index, CheckMode)
    case TraderDetailsConsigneeEoriNumberPage(index) => ua => consigneeEoriNumber(ua, index, CheckMode)
    case TraderDetailsConsigneeNamePage(index)       => ua => consigneeName(ua, index, CheckMode)
    case TraderDetailsConsigneeAddressPage(index)    => ua => consigneeAddress(ua, index, CheckMode)
  }

  private def consigneeAddress(ua: UserAnswers, index: Index, mode: Mode) =
    Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))

  private def consigneeName(ua: UserAnswers, index: Index, mode: Mode) =
    Some(traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(ua.id, index, mode))

  private def consigneeEoriNumber(ua: UserAnswers, index: Index, mode: Mode) =
    Some(addItemsRoutes.AddItemsSameConsigneeForAllItemsController.onPageLoad(ua.id, index, mode))

  private def consigneeEoriKnown(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(TraderDetailsConsigneeEoriKnownPage(index)) match {
      case Some(true) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(ua.id, index, mode))
      case Some(false) => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.id, index, mode))
      case _ => Some(routes.SessionExpiredController.onPageLoad())
    }

  private def addItemsSameConsigneeForAllItems(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddItemsSameConsigneeForAllItemsPage(index)), ua.get(AddItemsSameConsignorForAllItemsPage(index))) match {
      case (Some(true), Some(true)) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id))
      case (Some(false), _) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (_, Some(false)) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(routes.SessionExpiredController.onPageLoad())
    }

  private def consignorAddress(ua: UserAnswers, index: Index, mode: Mode) =
    Some(addItemsRoutes.AddItemsSameConsigneeForAllItemsController.onPageLoad(ua.id, index, mode))

  private def consignorName(ua: UserAnswers, index: Index, mode: Mode) =
    Some(traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(ua.id, index, mode))

  private def consignorEoriNumber(ua: UserAnswers, index: Index, mode: Mode) =
      Some(addItemsRoutes.AddItemsSameConsigneeForAllItemsController.onPageLoad(ua.id, index, mode))

  private def consignorEoriKnown(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(TraderDetailsConsignorEoriKnownPage(index)) match {
      case Some(true) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(ua.id, index, mode))
      case Some(false) => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, mode))
      case _ => Some(routes.SessionExpiredController.onPageLoad())
    }

  private def addItemsSameConsignorForAllItems(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddItemsSameConsignorForAllItemsPage(index)), ua.get(TraderDetailsConsignorEoriKnownPage(index))) match {
      case (Some(true), _) => Some(addItemsRoutes.AddItemsSameConsigneeForAllItemsController.onPageLoad(ua.id, index, mode))
      case (Some(false), _) if mode == NormalMode => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case (Some(false), None) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case _ if mode == CheckMode=> Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(routes.SessionExpiredController.onPageLoad())
    }

  private def isCommodityKnownRoute(index:Index, ua:UserAnswers, mode:Mode) =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index)), mode) match {
      case (Some(true), _, NormalMode)       => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _, NormalMode)      => Some(addItemsRoutes.AddItemsSameConsignorForAllItemsController.onPageLoad(ua.id, index, NormalMode)) //todo  change when Trader Details Pages built
      case (Some(true), None, CheckMode)    => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case _ => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addTotalNessMassRoute(index:Index, ua:UserAnswers, mode:Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode)    => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None , _)           => Some(addItemsRoutes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _                               => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }
    // format: on
}
