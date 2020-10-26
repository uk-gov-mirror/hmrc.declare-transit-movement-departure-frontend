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
import controllers.routes
import javax.inject.{Inject, Singleton}
import models._
import models.reference.PackageType.{bulkAndUnpackedCodes, bulkCodes, unpackedCodes}
import pages._
import pages.addItems._
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index) => ua => Some(addItemsRoutes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index) => ua => addTotalNessMassRoute(index, ua, NormalMode)
    case TotalNetMassPage(index) => ua => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case CommodityCodePage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex) => ua => packageType(itemIndex, packageIndex, ua) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex) => ua => howManyPackages(itemIndex, packageIndex, ua)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua)
    case TotalPiecesPage(itemIndex, packageIndex) => ua => Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddMarkPage(itemIndex, packageIndex) => ua => addMark(itemIndex, packageIndex, ua)
    case DeclareMarkPage(itemIndex, packageIndex) => ua => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
  }

  //TODO: Need to refactor this code
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index) => ua => addTotalNessMassRoute(index, ua, CheckMode)
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TotalNetMassPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
  }

  def isCommodityKnownRoute(index: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index)), mode) match {
      case (Some(true), _, NormalMode) => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _, NormalMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)) //todo  change when Trader Details Pages built
      case (Some(true), None, CheckMode) => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case _ => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  def addTotalNessMassRoute(index: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode) => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None, _) => Some(addItemsRoutes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _ => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  // TODO add smarter PackageTypePage type for easier matching
  def packageType(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkAndUnpackedCodes.contains(packageType) =>
        Some(addItemsRoutes.DeclareNumberOfPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case Some(_) =>
        Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(routes.SessionExpiredController.onPageLoad())
    }

  def howManyPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(_), Some(_)) =>
        Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(routes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) =>
        Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(routes.SessionExpiredController.onPageLoad())
    }

  def addMark(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    ua.get(AddMarkPage(itemIndex, packageIndex)) match {
      case Some(true)   =>
        Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case Some(false)  =>
        Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _            =>
        Some(routes.SessionExpiredController.onPageLoad())
    }



  // format: on
}
