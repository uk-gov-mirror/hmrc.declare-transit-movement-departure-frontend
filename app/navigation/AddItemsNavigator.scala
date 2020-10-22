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

import controllers.addItems.{routes => addItemsRoutes}
import javax.inject.{Inject, Singleton}
import models._
import pages._
import pages.addItems._
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index) => ua => Some(addItemsRoutes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index) => ua=>   addTotalNessMassRoute(index, ua,  NormalMode)
    case TotalNetMassPage(index) => ua => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case CommodityCodePage(index) => ua =>  Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))

  }

  //TODO: Need to refactor this code
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index) => ua => addTotalNessMassRoute(index, ua,  CheckMode)
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index) => ua =>  Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))


  }

  def isCommodityKnownRoute(index:Index, ua:UserAnswers, mode:Mode) =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index))) match {
      case (Some(true), _)       => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _)      => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)) //todo  change when Trader Details Pages built
      case (Some(true), None)    => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case _ => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }


  def addTotalNessMassRoute(index:Index, ua:UserAnswers, mode:Mode) = 
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode)    => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None, _)           => Some(addItemsRoutes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _                               => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }
    // format: on
}
