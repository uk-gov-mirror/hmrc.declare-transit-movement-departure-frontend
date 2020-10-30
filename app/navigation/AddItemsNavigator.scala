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

import controllers.{routes => mainRoutes}
import controllers.addItems.{routes => addItemsRoutes}
import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import derivable.{DeriveNumberOfItems, DeriveNumberOfPackages}
import javax.inject.{Inject, Singleton}
import models._
import models.reference.PackageType.{bulkAndUnpackedCodes, bulkCodes, unpackedCodes}
import pages._
import pages.addItems._
import pages.addItems.traderDetails._
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                           => ua => Some(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index)                        => ua => Some(addItemsRoutes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index)                           => ua => addTotalNessMassRoute(index, ua, NormalMode)
    case TotalNetMassPage(index)                              => ua => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index)                      => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case AddAnotherItemPage                                   => ua => Some(addAnotherPageRoute(ua))
    case ConfirmRemoveItemPage                                => ua => Some(removeItem(NormalMode)(ua))
    case CommodityCodePage(index)                             => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex)             => ua => packageType(itemIndex, packageIndex, ua, NormalMode) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex)         => ua => howManyPackages(itemIndex, packageIndex, ua, NormalMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, NormalMode)
    case TotalPiecesPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddMarkPage(itemIndex, packageIndex)                 => ua => addMark(itemIndex, packageIndex, ua, NormalMode)
    case DeclareMarkPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex, packageIndex)       => ua => addAnotherPackage(itemIndex, packageIndex, ua, NormalMode)
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
    case ItemDescriptionPage(index)                           => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index)                        => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index)                           => ua => addTotalNessMassRoute(index, ua, CheckMode)
    case IsCommodityCodeKnownPage(index)                      => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index)                             => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TotalNetMassPage(index)                              => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex)             => ua => packageType(itemIndex, packageIndex, ua, CheckMode) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex)         => ua => howManyPackages(itemIndex, packageIndex, ua, CheckMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, CheckMode)
    case TotalPiecesPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, CheckMode))
    case AddMarkPage(itemIndex, packageIndex)                 => ua => addMark(itemIndex, packageIndex, ua, CheckMode)
    case DeclareMarkPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case AddAnotherPackagePage(itemIndex, packageIndex)       => ua => addAnotherPackage(itemIndex, packageIndex, ua, CheckMode)
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
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }


  private def addItemsSameConsigneeForAllItems(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddItemsSameConsigneeForAllItemsPage(index)), ua.get(AddItemsSameConsignorForAllItemsPage(index))) match {
      case (Some(true), Some(true)) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (Some(false), _) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (_, Some(false)) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
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
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def addItemsSameConsignorForAllItems(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddItemsSameConsignorForAllItemsPage(index)), ua.get(TraderDetailsConsignorEoriKnownPage(index))) match {
      case (Some(true), _) => Some(addItemsRoutes.AddItemsSameConsigneeForAllItemsController.onPageLoad(ua.id, index, mode))
      case (Some(false), _) if mode == NormalMode => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case (Some(false), None) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case _ if mode == CheckMode=> Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def isCommodityKnownRoute(index:Index, ua:UserAnswers, mode:Mode) =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index)), mode) match {
      case (Some(true), _, NormalMode)       => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _, NormalMode)      => Some(addItemsRoutes.AddItemsSameConsignorForAllItemsController.onPageLoad(ua.id, index, NormalMode)) //todo  change when Trader Details Pages built
      case (Some(true), None, CheckMode)    => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case _ => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addTotalNessMassRoute(index: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode)    => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None , _)          => Some(addItemsRoutes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _                               => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addAnotherPageRoute(userAnswers: UserAnswers): Call = {
    val count = userAnswers.get(DeriveNumberOfItems).getOrElse(0)
    userAnswers.get(AddAnotherItemPage) match {
      case Some(true) =>
        addItemsRoutes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(count), NormalMode)
      case _ =>
        mainRoutes.DeclarationSummaryController.onPageLoad(userAnswers.id)
    }
  }

  private def removeItem(mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfItems) match {
      case None | Some(0) => addItemsRoutes.ItemDescriptionController.onPageLoad(ua.id, Index(0), mode)
      case _              => addItemsRoutes.AddAnotherItemController.onPageLoad(ua.id)
    }

  // TODO add smarter PackageTypePage type for easier matching
  def packageType(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkAndUnpackedCodes.contains(packageType) =>
        Some(addItemsRoutes.DeclareNumberOfPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case Some(_) =>
        Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def howManyPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call]  =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(_), Some(_)) =>
        Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call]  =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) =>
        Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addMark(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode: Option[Call]  =
    (ua.get(AddMarkPage(itemIndex, packageIndex)), mode) match {
      case (Some(true), _)            => Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), NormalMode)  => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), CheckMode)   => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case _                          => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackage(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddAnotherPackagePage(itemIndex, packageIndex)), mode) match {
      case (Some(true), _) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(nextPackageIndex), mode))
      case (Some(false), CheckMode) =>
        Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case (Some(false), NormalMode) =>
        ??? //TODO hook into container journey
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }
  // format: on
}
