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
import derivable.{DeriveNumberOfItems, DeriveNumberOfPackages}
import javax.inject.{Inject, Singleton}
import models._
import models.reference.PackageType.{bulkAndUnpackedCodes, bulkCodes, unpackedCodes}
import pages._
import pages.addItems._
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {
  //noinspection ScalaStyle
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                           => ua => Some(routes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index)                        => ua => Some(routes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index)                           => ua => addTotalNessMassRoute(index, ua, NormalMode)
    case TotalNetMassPage(index)                              => ua => Some(routes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index)                      => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case AddAnotherItemPage                                   => ua => Some(addAnotherPageRoute(ua))
    case ConfirmRemoveItemPage                                => ua => Some(removeItem(NormalMode)(ua))
    case CommodityCodePage(index)                             => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex)             => ua => packageType(itemIndex, packageIndex, ua) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex)         => ua => howManyPackages(itemIndex, packageIndex, ua)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua)
    case TotalPiecesPage(itemIndex, packageIndex)             => ua => Some(routes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddMarkPage(itemIndex, packageIndex)                 => ua => addMark(itemIndex, packageIndex, ua)
    case DeclareMarkPage(itemIndex, packageIndex)             => ua => Some(routes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex, packageIndex)       => ua => addAnotherPackage(itemIndex, packageIndex, ua)
  }

  //TODO: Need to refactor this code
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index) => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index) => ua => addTotalNessMassRoute(index, ua, CheckMode)
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index) => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TotalNetMassPage(index) => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
  }

  private def isCommodityKnownRoute(index:Index, ua:UserAnswers, mode:Mode): Option[Call] =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index)), mode) match {
      case (Some(true), _, NormalMode)    => Some(routes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _, NormalMode)   => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)) //todo  change when Trader Details Pages built
      case (Some(true), None, CheckMode)  => Some(routes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case _                              => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addTotalNessMassRoute(index: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode)    => Some(routes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None , _)          => Some(routes.TotalNetMassController.onPageLoad(ua.id, index, mode))
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

  private def removeItem(mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfItems) match {
      case None | Some(0) => routes.ItemDescriptionController.onPageLoad(ua.id, Index(0), mode)
      case _              => routes.AddAnotherItemController.onPageLoad(ua.id)
    }

  // TODO add smarter PackageTypePage type for easier matching
  def packageType(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkAndUnpackedCodes.contains(packageType) =>
        Some(routes.DeclareNumberOfPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case Some(_) =>
        Some(routes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def howManyPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType) =>
        Some(routes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType) =>
        Some(routes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(_), Some(_)) =>
        Some(routes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) =>
        Some(routes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType) =>
        Some(routes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType) =>
        Some(routes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addMark(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    ua.get(AddMarkPage(itemIndex, packageIndex)) match {
      case Some(true) =>
        Some(routes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case Some(false) =>
        Some(routes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackage(itemIndex: Index, packageIndex: Index, ua: UserAnswers) =
    ua.get(AddAnotherPackagePage(itemIndex, packageIndex)) match {
      case Some(true) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(routes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(nextPackageIndex), NormalMode))
      case Some(false) =>
        ??? //TODO hook into container journey
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }
  // format: on
}
