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

package utils

import controllers.goodsSummary.{routes => goodsSummaryRoutes}
import controllers.movementDetails.{routes => movementDetailsRoutes}
import controllers.routeDetails.{routes => routeDetailsRoutes}
import controllers.traderDetails.{routes => traderDetailsRoutes}
import controllers.transportDetails.{routes => transportDetailsRoutes}
import models.ProcedureType.{Normal, Simplified}
import models.Status.{Completed, InProgress, NotStarted}
import models.domain.SealDomain
import models.{Index, NormalMode, SectionDetails, Status, UserAnswers}
import pages.{IsPrincipalEoriKnownPage, RepresentativeNamePage, _}

class SectionsHelper(userAnswers: UserAnswers) {

  def getSections: Seq[SectionDetails] = {

    val mandatorySections = Seq(
      movementDetailsSection,
      routesSection,
      transportSection,
      tradersDetailsSection,
      goodsSummarySection,
      guaranteeSection
    )

    if (userAnswers.get(AddSecurityDetailsPage).contains(true)) {
      mandatorySections :+ safetyAndSecuritySection
    } else {
      mandatorySections
    }
  }

  private def getIncompletePage(startPage: String, pages: Seq[(Option[_], String)]): Option[(String, Status)] =
    pages.collectFirst {
      case (page, url) if page.isEmpty && url == startPage => (url, NotStarted)
      case (page, url) if page.isEmpty && url != startPage => (url, InProgress)
    }

  private def movementDetailsSection: SectionDetails = {
    val startPage: String                  = movementDetailsRoutes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (movementDetailsRoutes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed)
    val (page, status)                     = getIncompletePage(startPage, movementDetailsPages).getOrElse(cyaPageAndStatus)

    SectionDetails("declarationSummary.section.movementDetails", page, status)
  }

  private def routesSection: SectionDetails = {
    val startPage: String                  = routeDetailsRoutes.CountryOfDispatchController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (routeDetailsRoutes.RouteDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed)
    val (page, status)                     = getIncompletePage(startPage, routeDetailsPages).getOrElse(cyaPageAndStatus)

    SectionDetails("declarationSummary.section.routes", page, status)
  }

  private def transportSection: SectionDetails = {
    val startPage: String                  = transportDetailsRoutes.InlandModeController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (transportDetailsRoutes.TransportDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed)
    val (page, status)                     = getIncompletePage(startPage, transportDetailsPage).getOrElse(cyaPageAndStatus)

    SectionDetails("declarationSummary.section.transport", page, status)
  }

  private def tradersDetailsSection: SectionDetails = {
    val startPage: String                  = traderDetailsRoutes.IsPrincipalEoriKnownController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed)
    val (page, status)                     = getIncompletePage(startPage, traderDetailsPage).getOrElse(cyaPageAndStatus)

    SectionDetails("declarationSummary.section.tradersDetails", page, status)
  }

  private def goodsSummarySection: SectionDetails = {
    val startPage: String                  = goodsSummaryRoutes.DeclarePackagesController.onPageLoad(userAnswers.id, NormalMode).url
    val cyaPageAndStatus: (String, Status) = (goodsSummaryRoutes.GoodsSummaryCheckYourAnswersController.onPageLoad(userAnswers.id).url, Completed)
    val (page, status)                     = getIncompletePage(startPage, goodsSummaryPages).getOrElse(cyaPageAndStatus)

    SectionDetails("declarationSummary.section.goodsSummary", page, status)
  }

  private def guaranteeSection: SectionDetails =
    SectionDetails("declarationSummary.section.guarantee", "", NotStarted)

  private def safetyAndSecuritySection: SectionDetails =
    SectionDetails("declarationSummary.section.safetyAndSecurity", "", NotStarted)

  private val transportDetailsPage: Seq[(Option[_], String)] = {
    val lrn = userAnswers.id

    val addIdAtDeparturePages: Seq[(Option[Object], String)] = if (userAnswers.get(AddIdAtDeparturePage).contains(true)) {
      Seq(userAnswers.get(IdAtDeparturePage) -> transportDetailsRoutes.IdAtDepartureController.onPageLoad(lrn, NormalMode).url)
    } else {
      Seq.empty
    }

    val changeAtBorderPages: Seq[(Option[Object], String)] = if (userAnswers.get(ChangeAtBorderPage).contains(true)) {

      Seq(
        userAnswers.get(ModeAtBorderPage)              -> transportDetailsRoutes.ModeAtBorderController.onPageLoad(lrn, NormalMode).url,
        userAnswers.get(IdCrossingBorderPage)          -> transportDetailsRoutes.IdCrossingBorderController.onPageLoad(lrn, NormalMode).url,
        userAnswers.get(ModeCrossingBorderPage)        -> transportDetailsRoutes.ModeCrossingBorderController.onPageLoad(lrn, NormalMode).url,
        userAnswers.get(NationalityCrossingBorderPage) -> transportDetailsRoutes.NationalityCrossingBorderController.onPageLoad(lrn, NormalMode).url
      )

    } else {
      Seq.empty
    }

    Seq(
      userAnswers.get(InlandModePage)             -> transportDetailsRoutes.InlandModeController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(AddIdAtDeparturePage)       -> transportDetailsRoutes.AddIdAtDepartureController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(ChangeAtBorderPage)         -> transportDetailsRoutes.ChangeAtBorderController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(NationalityAtDeparturePage) -> transportDetailsRoutes.NationalityAtDepartureController.onPageLoad(lrn, NormalMode).url,
    ) ++ addIdAtDeparturePages ++ changeAtBorderPages

  }

  private val movementDetailsPages: Seq[(Option[_], String)] = {
    val lrn = userAnswers.id

    val declareForSomeoneElseDiversionPages: Seq[(Option[Object], String)] = if (userAnswers.get(DeclarationForSomeoneElsePage).contains(true)) {
      Seq(
        userAnswers.get(RepresentativeNamePage)     -> movementDetailsRoutes.RepresentativeNameController.onPageLoad(lrn, NormalMode).url,
        userAnswers.get(RepresentativeCapacityPage) -> movementDetailsRoutes.RepresentativeCapacityController.onPageLoad(lrn, NormalMode).url
      )
    } else {
      Seq.empty
    }

    Seq(
      userAnswers.get(DeclarationTypePage)           -> movementDetailsRoutes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(ContainersUsedPage)            -> movementDetailsRoutes.ContainersUsedPageController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DeclarationPlacePage)          -> movementDetailsRoutes.DeclarationPlaceController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DeclarationForSomeoneElsePage) -> movementDetailsRoutes.DeclarationForSomeoneElseController.onPageLoad(lrn, NormalMode).url
    ) ++ declareForSomeoneElseDiversionPages

  }

  private val routeDetailsPages: Seq[(Option[_], String)] = {
    val lrn   = userAnswers.id
    val index = Index(0)
    val arrivalTimeAtTransit: Seq[(Option[Object], String)] = if (userAnswers.get(AddSecurityDetailsPage).contains(true)) {
      Seq(userAnswers.get(ArrivalTimesAtOfficePage(index)) -> routeDetailsRoutes.ArrivalTimesAtOfficeController.onPageLoad(lrn, index, NormalMode).url)
    } else {
      Seq.empty
    }

    Seq(
      userAnswers.get(CountryOfDispatchPage)              -> routeDetailsRoutes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(OfficeOfDeparturePage)              -> routeDetailsRoutes.OfficeOfDepartureController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DestinationCountryPage)             -> routeDetailsRoutes.DestinationCountryController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(DestinationOfficePage)              -> routeDetailsRoutes.DestinationOfficeController.onPageLoad(lrn, NormalMode).url,
      userAnswers.get(AddAnotherTransitOfficePage(index)) -> routeDetailsRoutes.AddAnotherTransitOfficeController.onPageLoad(lrn, index, NormalMode).url,
    ) ++ arrivalTimeAtTransit

  }

  private val traderDetailsPage: Seq[(Option[_], String)] = {
    val lrn = userAnswers.id

    val isPrincipalEoriKnowDiversionPages = userAnswers.get(IsPrincipalEoriKnownPage) match {
      case Some(true) => Seq(userAnswers.get(WhatIsPrincipalEoriPage) -> traderDetailsRoutes.WhatIsPrincipalEoriController.onPageLoad(lrn, NormalMode).url)
      case Some(false) =>
        Seq(
          userAnswers.get(PrincipalNamePage)    -> traderDetailsRoutes.PrincipalNameController.onPageLoad(lrn, NormalMode).url,
          userAnswers.get(PrincipalAddressPage) -> traderDetailsRoutes.PrincipalAddressController.onPageLoad(lrn, NormalMode).url
        )
      case _ => Seq.empty
    }

    val isConsignorEoriKnownPage = userAnswers.get(IsConsignorEoriKnownPage) match {
      case Some(true) =>
        Seq(userAnswers.get(ConsignorEoriPage) -> traderDetailsRoutes.ConsignorEoriController.onPageLoad(lrn, NormalMode).url)
      case Some(false) =>
        Seq(
          userAnswers.get(ConsignorNamePage)    -> traderDetailsRoutes.ConsignorNameController.onPageLoad(lrn, NormalMode).url,
          userAnswers.get(ConsignorAddressPage) -> traderDetailsRoutes.ConsignorAddressController.onPageLoad(lrn, NormalMode).url
        )
      case _ => Seq.empty
    }

    val isConsigneeEoriKnownPage = userAnswers.get(IsConsigneeEoriKnownPage) match {
      case Some(true) => Seq(userAnswers.get(WhatIsConsigneeEoriPage) -> traderDetailsRoutes.WhatIsConsigneeEoriController.onPageLoad(lrn, NormalMode).url)
      case Some(false) =>
        Seq(
          userAnswers.get(ConsigneeNamePage)    -> traderDetailsRoutes.ConsigneeNameController.onPageLoad(lrn, NormalMode).url,
          userAnswers.get(ConsigneeAddressPage) -> traderDetailsRoutes.ConsigneeAddressController.onPageLoad(lrn, NormalMode).url
        )
      case _ => Seq.empty
    }
    val addConsigneeDiversionPage = if (userAnswers.get(AddConsigneePage).contains(true)) {
      Seq(userAnswers.get(IsConsigneeEoriKnownPage) -> traderDetailsRoutes.IsConsigneeEoriKnownController.onPageLoad(lrn, NormalMode).url)

    } else {
      Seq.empty
    }

    val addConsignorPage: Seq[(Option[Boolean], String)] = Seq(
      userAnswers.get(AddConsignorPage) -> traderDetailsRoutes.AddConsignorController.onPageLoad(lrn, NormalMode).url)
    val addConsigneePage: Seq[(Option[Boolean], String)] = Seq(
      userAnswers.get(AddConsigneePage) -> traderDetailsRoutes.AddConsigneeController.onPageLoad(lrn, NormalMode).url)

    val addConsignorPageDiversionPage: Seq[(Option[Boolean], String)] = if (userAnswers.get(AddConsignorPage).contains(true)) {
      Seq(userAnswers.get(IsConsignorEoriKnownPage) -> traderDetailsRoutes.IsConsignorEoriKnownController.onPageLoad(lrn, NormalMode).url)

    } else {
      addConsigneePage
    }

    Seq(
      userAnswers.get(IsPrincipalEoriKnownPage) -> traderDetailsRoutes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url,
    ) ++ isPrincipalEoriKnowDiversionPages ++ addConsignorPage ++ addConsignorPageDiversionPage ++ isConsignorEoriKnownPage ++ addConsigneePage ++ addConsigneeDiversionPage ++ isConsigneeEoriKnownPage
  }

  private val goodsSummaryPages: Seq[(Option[_], String)] = {
    val lrn       = userAnswers.id
    val sealIndex = Index(0)

    val declarePackagesDiversionPages: Seq[(Option[_], String)] = userAnswers.get(DeclarePackagesPage) match {
      case Some(true)  => Seq(userAnswers.get(TotalPackagesPage) -> goodsSummaryRoutes.TotalPackagesController.onPageLoad(lrn, NormalMode).url)
      case Some(false) => Seq(userAnswers.get(TotalGrossMassPage) -> goodsSummaryRoutes.TotalGrossMassController.onPageLoad(lrn, NormalMode).url)
      case _           => Seq.empty
    }

    val addCustomsApprovedLocationDiversionPages: Seq[(Option[String], String)] = userAnswers.get(AddCustomsApprovedLocationPage) match {
      case Some(true) =>
        Seq(userAnswers.get(CustomsApprovedLocationPage) -> goodsSummaryRoutes.CustomsApprovedLocationController.onPageLoad(lrn, NormalMode).url)
      case _ => Seq.empty
    }

    val sealsInformationDiversionPages: Seq[(Option[SealDomain], String)] = if (userAnswers.get(SealsInformationPage).contains(true)) {
      Seq(userAnswers.get(SealIdDetailsPage(sealIndex)) -> goodsSummaryRoutes.SealIdDetailsController.onPageLoad(lrn, sealIndex, NormalMode).url)
    } else {
      Seq.empty
    }

    val addSealsPages: Seq[(Option[SealDomain], String)] = if (userAnswers.get(AddSealsPage).contains(true)) {
      Seq(userAnswers.get(SealIdDetailsPage(sealIndex)) -> goodsSummaryRoutes.SealIdDetailsController.onPageLoad(lrn, sealIndex, NormalMode).url)
    } else {
      Seq.empty
    }

    val simplifiedPages: Seq[(Option[Any], String)] = {
      userAnswers.get(ProcedureTypePage) match {
        case Some(Simplified) =>
          Seq(
            userAnswers.get(AuthorisedLocationCodePage) -> goodsSummaryRoutes.AuthorisedLocationCodeController.onPageLoad(lrn, NormalMode).url,
            userAnswers.get(ControlResultDateLimitPage) -> goodsSummaryRoutes.ControlResultDateLimitController.onPageLoad(lrn, NormalMode).url
          )
        case Some(Normal) =>
          userAnswers.get(AddCustomsApprovedLocationPage) match {
            case Some(true) =>
              Seq(
                userAnswers.get(AddCustomsApprovedLocationPage) -> goodsSummaryRoutes.AddCustomsApprovedLocationController.onPageLoad(lrn, NormalMode).url,
                userAnswers.get(CustomsApprovedLocationPage)    -> goodsSummaryRoutes.CustomsApprovedLocationController.onPageLoad(lrn, NormalMode).url
              )
            case _ =>
              Seq(userAnswers.get(AddCustomsApprovedLocationPage) -> goodsSummaryRoutes.AddCustomsApprovedLocationController.onPageLoad(lrn, NormalMode).url)
          }
        case _ => Seq.empty
      }
    }

    Seq(
      userAnswers.get(DeclarePackagesPage) -> goodsSummaryRoutes.DeclarePackagesController.onPageLoad(lrn, NormalMode).url,
      //   userAnswers.get(TotalPackagesPage)   -> goodsSummaryRoutes.TotalPackagesController.onPageLoad(lrn, NormalMode).url,
      //userAnswers.get(TotalGrossMassPage) -> goodsSummaryRoutes.TotalGrossMassController.onPageLoad(lrn, NormalMode).url,
      //userAnswers.get(AddSealsPage) -> goodsSummaryRoutes.AddSealsController.onPageLoad(lrn, NormalMode).url,
    ) ++ declarePackagesDiversionPages ++ addCustomsApprovedLocationDiversionPages ++ addSealsPages ++ simplifiedPages ++ sealsInformationDiversionPages

  }
}
