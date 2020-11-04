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

import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import controllers.addItems.routes
import controllers.{routes => mainRoutes}
import derivable.{DeriveNumberOfItems, DeriveNumberOfPackages, DeriveNumberOfPreviousAdministrativeReferences}
import javax.inject.{Inject, Singleton}
import models._
import models.reference.CountryCode
import models.reference.PackageType.{bulkAndUnpackedCodes, bulkCodes, unpackedCodes}
import pages._
import pages.addItems._
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                           => ua => Some(routes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index)                        => ua => Some(routes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index)                           => ua => addTotalNetMassRoute(index, ua, NormalMode)
    case TotalNetMassPage(index)                              => ua => Some(routes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index)                      => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case AddAnotherItemPage                                   => ua => Some(addAnotherItemRoute(ua))
    case ConfirmRemoveItemPage                                => ua => Some(removeItem(NormalMode)(ua))
    case CommodityCodePage(index)                             => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex)             => ua => packageType(itemIndex, packageIndex, ua, NormalMode) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex)         => ua => howManyPackages(itemIndex, packageIndex, ua, NormalMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, NormalMode)
    case TotalPiecesPage(itemIndex, packageIndex)             => ua => Some(routes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddMarkPage(itemIndex, packageIndex)                 => ua => addMark(itemIndex, packageIndex, ua, NormalMode)
    case DeclareMarkPage(itemIndex, _)                        => ua => Some(routes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex)                     => ua => addAnotherPackage(itemIndex, ua, NormalMode)
    case RemovePackagePage(itemIndex)                         => ua => Some(removePackage(itemIndex, NormalMode)(ua))
    case AddExtraInformationPage(itemIndex, referenceIndex)   => ua => addExtraInformationRoute(itemIndex, referenceIndex, ua)
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex, referenceIndex)   => ua => addAnotherPreviousAdministrativeReferenceRoute(itemIndex, referenceIndex, ua)
    case ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex)     => ua => Some(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, index, referenceIndex,  NormalMode))
    case DummyPage(itemIndex, packageIndex)                   => ua => directToPreviousReferencesPage(itemIndex, packageIndex, ua, NormalMode) //TODO replace dummy page with add another document page
    case AddAdministrativeReferencePage(itemIndex) => ua => addAdministrativeReferencePage(itemIndex, ua, NormalMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.id, itemIndex, referenceIndex, NormalMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, NormalMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, NormalMode)
    case ExtraInformationPage(itemIndex, _)    => ua => Some(previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex)   => ua => addAnotherPreviousAdministrativeReferenceRoute(itemIndex, ua, NormalMode)
  }

  //TODO: Need to refactor this code
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                           => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index)                        => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index)                           => ua => addTotalNetMassRoute(index, ua, CheckMode)
    case IsCommodityCodeKnownPage(index)                      => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index)                             => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TotalNetMassPage(index)                              => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex)             => ua => packageType(itemIndex, packageIndex, ua, CheckMode) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex)         => ua => howManyPackages(itemIndex, packageIndex, ua, CheckMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, CheckMode)
    case TotalPiecesPage(itemIndex, packageIndex)             => ua => Some(routes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, CheckMode))
    case AddMarkPage(itemIndex, packageIndex)                 => ua => addMark(itemIndex, packageIndex, ua, CheckMode)
    case DeclareMarkPage(itemIndex, packageIndex)             => ua => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case AddAnotherPackagePage(itemIndex)                     => ua => addAnotherPackage(itemIndex, ua, CheckMode)
    case RemovePackagePage(itemIndex)                         => ua => Some(removePackage(itemIndex, CheckMode)(ua))
    case ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex)     => ua => Some(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, index, referenceIndex,  CheckMode))
    case RemovePackagePage(itemIndex)                         => ua => Some(routes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, CheckMode))
    case AddAdministrativeReferencePage(itemIndex)            => ua =>  addAdministrativeReferencePage(itemIndex, ua, CheckMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.id, itemIndex, referenceIndex, CheckMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, CheckMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, CheckMode)
    case ExtraInformationPage(itemIndex, referenceIndex)    => ua =>  Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))

  }

  private def addExtraInformationRoute(index:Index, referenceIndex:Index, ua:UserAnswers) =
    ua.get(AddExtraInformationPage(index, referenceIndex)) match  {
      case Some(true) => ???
      case Some(false) =>  Some(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, index, referenceIndex, NormalMode))
    }

    private def isCommodityKnownRoute(index:Index, ua:UserAnswers, mode:Mode): Option[Call] =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index)), mode) match {
      case (Some(true), _, NormalMode)    => Some(routes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), _, NormalMode)   => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)) //todo  change when Trader Details Pages built
      case (Some(true), None, CheckMode)  => Some(routes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case _                              => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addTotalNetMassRoute(index: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode)    => Some(routes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None , _)          => Some(routes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _                               => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addAnotherItemRoute(userAnswers: UserAnswers): Call = {
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
  def packageType(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode) =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkAndUnpackedCodes.contains(packageType.code) =>
        Some(routes.DeclareNumberOfPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case Some(_) =>
        Some(routes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def howManyPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(routes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(routes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(_), Some(_)) =>
        Some(routes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) =>
        Some(routes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(routes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(routes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addMark(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddMarkPage(itemIndex, packageIndex)), mode) match {
      case (Some(true), _)            => Some(routes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), NormalMode)  => Some(routes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, mode))
      case (Some(false), CheckMode)   => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case _                          => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackage(itemIndex: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddAnotherPackagePage(itemIndex)), mode) match {
      case (Some(true), _) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(routes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(nextPackageIndex), mode))
      case (Some(false), CheckMode) =>
        Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case (Some(false), NormalMode) =>
        ??? //TODO hook into container journey
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def removePackage(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPackages(itemIndex)) match {
      case None|Some(0) => routes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(0), mode)
      case _            => routes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, mode)
    }

  def directToPreviousReferencesPage(itemIndex: Index, referenceIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val nonEUCountries = Seq(CountryCode("AD"), CountryCode("IS"), CountryCode("LI"), CountryCode("NO"), CountryCode("SM"), CountryCode("SJ"), CountryCode("CH"))
    val declarationTypes = Seq(DeclarationType.Option2, DeclarationType.Option4)
    val isNonEUCountry: Boolean = ua.get(CountryOfDispatchPage).fold(false)(code => nonEUCountries.contains(code))
    val isAllowedDeclarationType: Boolean = ua.get(DeclarationTypePage).fold(false)(declarationTypes.contains(_))
    (isNonEUCountry, isAllowedDeclarationType) match {
      case (true, true) => Some(previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, referenceIndex, mode))
      case _ => Some(previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, mode))
    }
  }

  private def addAdministrativeReferencePage(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val referenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAdministrativeReferencePage(itemIndex)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, Index(referenceIndex), mode)
      case _ if mode == CheckMode => routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex)
      case _ => ??? //TODO must go to 'Has the user selected yes for safety and security?'
    }
  }

  private def addExtraInformationPage(ua: UserAnswers, itemIndex: Index, referenceIndex: Index, mode: Mode): Option[Call] =
    ua.get(AddExtraInformationPage(itemIndex, referenceIndex)) map {
      case true =>
        previousReferencesRoutes.ExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, mode)
      case false =>
        previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, mode)
    }


  private def addAnotherPreviousAdministrativeReferenceRoute(index: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val newReferenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)
    ua.get(AddAnotherPreviousAdministrativeReferencePage(index)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, index, Index(newReferenceIndex), mode)
      case false if mode == NormalMode => ??? //TODO must go to 'Has the user selected yes for safety and security?'
      case _ => routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)
    }
  }

  // format: on
}
