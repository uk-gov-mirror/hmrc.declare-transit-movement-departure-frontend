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

import controllers.addItems.routes
import controllers.{routes => mainRoutes}
import derivable.DeriveNumberOfItems
import javax.inject.{Inject, Singleton}
import models._
import pages._
import pages.addItems._
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(routes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index) => ua => Some(routes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index) => ua=>   addTotalNetMassRoute(index, ua,  NormalMode)
    case TotalNetMassPage(index) => ua => Some(routes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case CommodityCodePage(index) => ua =>  Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    case AddAnotherItemPage => ua => Some(addAnotherPageRoute(ua))

  }

  //TODO: Need to refactor this code
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index) => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index) => ua => addTotalNetMassRoute(index, ua,  CheckMode)
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index) => ua =>  Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    case TotalNetMassPage(index) => ua =>  Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))


  }

  private def isCommodityKnownRoute(index:Index, ua:UserAnswers, mode:Mode): Option[Call] =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index)), mode) match {
      case (Some(true), _, NormalMode)       => Some(routes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _, NormalMode)      => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)) //todo  change when Trader Details Pages built
      case (Some(true), None, CheckMode)    => Some(routes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case _ => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addTotalNetMassRoute(index:Index, ua:UserAnswers, mode:Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode)    => Some(routes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None , _)           => Some(routes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _                               => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }
    // format: on

  private def addAnotherPageRoute(userAnswers: UserAnswers): Call = {
    val count = userAnswers.get(DeriveNumberOfItems).getOrElse(0)
    userAnswers.get(AddAnotherItemPage) match {
      case Some(true) =>
        routes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(count), NormalMode)
      case _ =>
        mainRoutes.DeclarationSummaryController.onPageLoad(userAnswers.id)
    }
  }
}
