/*
 * Copyright 2021 HM Revenue & Customs
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

package generators

import java.time.{LocalDate, LocalDateTime}
import cats.data.{NonEmptyList, NonEmptyMap}
import models.DeclarationType.{Option2, Option4}
import models._
import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetails, GoodSummarySimplifiedDetails}
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.journeyDomain.ItemsSecurityTraderDetails.{SecurityPersonalInformation, SecurityTraderEori}
import models.journeyDomain.MovementDetails.{
  DeclarationForSelf,
  DeclarationForSomeoneElse,
  DeclarationForSomeoneElseAnswer,
  NormalMovementDetails,
  SimplifiedMovementDetails
}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain.PreviousReferences.nonEUCountries
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.SafetyAndSecurity.SecurityTraderDetails
import models.journeyDomain.TraderDetails.{PersonalInformation, RequiredDetails, TraderEori, TraderInformation}
import models.journeyDomain.TransportDetails.DetailsAtBorder.{NewDetailsAtBorder, SameDetailsAtBorder}
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, NonSpecialMode, Rail}
import models.journeyDomain.TransportDetails.ModeCrossingBorder.{ModeExemptNationality, ModeWithNationality}
import models.journeyDomain.TransportDetails.{DetailsAtBorder, InlandMode, ModeCrossingBorder}
import models.journeyDomain._
import models.reference.{SpecialMention => _, _}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait JourneyModelGenerators {
  self: Generators =>

  val maxNumberOfItemsLength = 2

  implicit def arbitraryJourneyDomain: Arbitrary[JourneyDomain] = Arbitrary(Gen.oneOf(arbitrarySimplifiedJourneyDomain, arbitraryNormalJourneyDomain))

  lazy val arbitrarySimplifiedJourneyDomain: Gen[JourneyDomain] =
    for {
      preTaskList <- arbitrary[PreTaskListDetails]
      simplifiedTaskList = preTaskList.copy(procedureType = ProcedureType.Simplified)
      movementDetails <- arbitrary[SimplifiedMovementDetails]
      isSecurityDetailsRequired = preTaskList.addSecurityDetails
      routeDetails      <- arbitraryRouteDetails(isSecurityDetailsRequired).arbitrary
      transportDetails  <- arbitrary[TransportDetails]
      traderDetails     <- arbitraryTraderDetails(simplifiedTaskList.procedureType).arbitrary
      safetyAndSecurity <- arbitrary[SafetyAndSecurity]
      itemDetails       <- genItemSection(movementDetails.containersUsed, isSecurityDetailsRequired, safetyAndSecurity, movementDetails, routeDetails)
      goodsummarydetailsType = arbitrary[GoodSummarySimplifiedDetails]
      goodsSummary <- arbitraryGoodsSummary(isSecurityDetailsRequired)(Arbitrary(goodsummarydetailsType)).arbitrary
      guarantees   <- nonEmptyListOf[GuaranteeDetails](3)
    } yield
      JourneyDomain(
        simplifiedTaskList,
        movementDetails,
        routeDetails,
        transportDetails,
        traderDetails,
        NonEmptyList(itemDetails, List(itemDetails)),
        goodsSummary,
        guarantees,
        if (isSecurityDetailsRequired) Some(safetyAndSecurity) else None
      )

  lazy val arbitraryNormalJourneyDomain: Gen[JourneyDomain] =
    for {
      preTaskList <- arbitrary[PreTaskListDetails]
      simplifiedTaskList = preTaskList.copy(procedureType = ProcedureType.Normal)
      movementDetails <- arbitrary[NormalMovementDetails]
      isSecurityDetailsRequired = preTaskList.addSecurityDetails
      routeDetails      <- arbitraryRouteDetails(isSecurityDetailsRequired).arbitrary
      transportDetails  <- arbitrary[TransportDetails]
      traderDetails     <- arbitraryTraderDetails(simplifiedTaskList.procedureType).arbitrary
      safetyAndSecurity <- arbitrary[SafetyAndSecurity]
      itemDetails       <- genItemSection(movementDetails.containersUsed, isSecurityDetailsRequired, safetyAndSecurity, movementDetails, routeDetails)
      goodsummarydetailsType = arbitrary[GoodSummaryNormalDetails]
      goodsSummary <- arbitraryGoodsSummary(isSecurityDetailsRequired)(Arbitrary(goodsummarydetailsType)).arbitrary
      guarantees   <- nonEmptyListOf[GuaranteeDetails](3)
    } yield
      JourneyDomain(
        simplifiedTaskList,
        movementDetails,
        routeDetails,
        transportDetails,
        traderDetails,
        NonEmptyList(itemDetails, List(itemDetails)),
        goodsSummary,
        guarantees,
        if (isSecurityDetailsRequired) Some(safetyAndSecurity) else None
      )

  implicit lazy val arbitrarySecurityDetails: Arbitrary[SafetyAndSecurity] = {

    val carrierAddress   = Arbitrary(arbitrary[CarrierAddress].map(Address.prismAddressToCarrierAddress.reverseGet))
    val consignorAddress = Arbitrary(arbitrary[ConsignorAddress].map(Address.prismAddressToConsignorAddress.reverseGet))
    val consigneeAddress = Arbitrary(arbitrary[ConsigneeAddress].map(Address.prismAddressToConsigneeAddress.reverseGet))

    Arbitrary {
      for {
        addCircumstanceIndicator   <- Gen.option(nonEmptyString)
        paymentMethod              <- Gen.option(nonEmptyString)
        commercialReference        <- Gen.option(nonEmptyString)
        convenyanceReferenceNumber <- Gen.option(nonEmptyString)
        placeOfUnloading           <- Gen.option(nonEmptyString)
        consignorAddress           <- Gen.option(arbitrarySecurityTraderDetails(consignorAddress).arbitrary)
        consigneeAddress           <- Gen.option(arbitrarySecurityTraderDetails(consigneeAddress).arbitrary)
        carrierAddress             <- Gen.option(arbitrarySecurityTraderDetails(carrierAddress).arbitrary)
        itineraries                <- nonEmptyListOf[Itinerary](5)
      } yield
        SafetyAndSecurity(
          addCircumstanceIndicator,
          paymentMethod,
          commercialReference,
          convenyanceReferenceNumber,
          placeOfUnloading,
          consignorAddress,
          consigneeAddress,
          carrierAddress,
          itineraries
        )
    }
  }

  implicit lazy val arbitraryItinerary: Arbitrary[Itinerary] =
    Arbitrary {
      for {
        countryCode <- arbitrary[CountryCode]
      } yield Itinerary(countryCode)
    }

  def genSecurityDetails(modeAtBorder: Gen[String]): Gen[SafetyAndSecurity] = {

    val carrierAddress   = Arbitrary(arbitrary[CarrierAddress].map(Address.prismAddressToCarrierAddress.reverseGet))
    val consignorAddress = Arbitrary(arbitrary[ConsignorAddress].map(Address.prismAddressToConsignorAddress.reverseGet))
    val consigneeAddress = Arbitrary(arbitrary[ConsigneeAddress].map(Address.prismAddressToConsigneeAddress.reverseGet))

    val genConvenyanceReferenceNumber: Gen[Option[String]] = modeAtBorder.flatMap {
      case "4" | "40" => nonEmptyString.map(Some(_))
      case _          => Gen.option(nonEmptyString)
    }

    for {
      addCircumstanceIndicator   <- Gen.option(nonEmptyString)
      paymentMethod              <- Gen.option(nonEmptyString)
      commercialReference        <- Gen.option(nonEmptyString)
      convenyanceReferenceNumber <- genConvenyanceReferenceNumber
      placeOfUnloading           <- Gen.option(nonEmptyString)
      consignorAddress           <- Gen.option(arbitrarySecurityTraderDetails(consignorAddress).arbitrary)
      consigneeAddress           <- Gen.option(arbitrarySecurityTraderDetails(consigneeAddress).arbitrary)
      carrierAddress             <- Gen.option(arbitrarySecurityTraderDetails(carrierAddress).arbitrary)
      itineraries                <- nonEmptyListOf[Itinerary](5)
    } yield
      SafetyAndSecurity(
        addCircumstanceIndicator,
        paymentMethod,
        commercialReference,
        convenyanceReferenceNumber,
        placeOfUnloading,
        consignorAddress,
        consigneeAddress,
        carrierAddress,
        itineraries
      )
  }

  implicit def arbitrarySecurityTraderDetails(implicit arbAddress: Arbitrary[Address]): Arbitrary[SecurityTraderDetails] =
    Arbitrary(Gen.oneOf(Arbitrary.arbitrary[SafetyAndSecurity.PersonalInformation], Arbitrary.arbitrary[SafetyAndSecurity.TraderEori]))

  implicit lazy val arbitrarySafetyAndSecurityTraderEori: Arbitrary[SafetyAndSecurity.TraderEori] =
    Arbitrary(Arbitrary.arbitrary[EoriNumber].map(SafetyAndSecurity.TraderEori))

  implicit def arbitrarySafetyAndSecurityPersonalInformation(implicit arbAddress: Arbitrary[Address]): Arbitrary[SafetyAndSecurity.PersonalInformation] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
      } yield
        SafetyAndSecurity.PersonalInformation(name, Address(address.line1, "", "", Some(Country(CountryCode("GB"), "")))) // TODO update when actual address
    }

  implicit lazy val arbitraryModeCrossingBorder: Arbitrary[ModeCrossingBorder] =
    Arbitrary(
      Gen.oneOf(
        arbitrary[ModeExemptNationality],
        arbitrary[ModeWithNationality]
      )
    )

  implicit lazy val arbitraryModeExemptNationality: Arbitrary[ModeExemptNationality] =
    Arbitrary {
      for {
        codeMode <- Gen.oneOf(Mode5or7.Constants.codes ++ Rail.Constants.codes)
      } yield ModeExemptNationality(codeMode)
    }

  implicit lazy val arbitraryModeWithNationality: Arbitrary[ModeWithNationality] = {

    val codeList = Mode5or7.Constants.codes ++ Rail.Constants.codes

    Arbitrary {
      for {
        cc       <- arbitrary[CountryCode]
        codeMode <- arbitrary[Int].suchThat(!codeList.contains(_))
      } yield ModeWithNationality(cc, codeMode)
    }
  }

  implicit lazy val arbitraryDetailsAtBorder: Arbitrary[DetailsAtBorder] =
    Arbitrary(
      Gen.oneOf(
        arbitrary[SameDetailsAtBorder.type],
        arbitrary[NewDetailsAtBorder]
      )
    )

  implicit lazy val arbitrarySameDetailsAtBorder: Arbitrary[SameDetailsAtBorder.type] =
    Arbitrary(Gen.const(SameDetailsAtBorder))

  implicit lazy val arbitraryNewDetailsAtBorder: Arbitrary[NewDetailsAtBorder] =
    Arbitrary {
      for {
        mode               <- genNumberString
        idCrossing         <- stringsWithMaxLength(stringMaxLength)
        modeCrossingBorder <- arbitrary[ModeCrossingBorder]
      } yield
        NewDetailsAtBorder(
          mode,
          idCrossing,
          modeCrossingBorder
        )
    }

  implicit lazy val arbitraryInlandMode: Arbitrary[InlandMode] =
    Arbitrary(
      Gen.oneOf(
        arbitrary[Rail],
        arbitrary[Mode5or7],
        arbitrary[NonSpecialMode]
      )
    )

  implicit lazy val arbitraryRail: Arbitrary[Rail] =
    Arbitrary {
      for {
        code        <- Gen.oneOf(Rail.Constants.codes).map(_.toInt)
        departureId <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield Rail(code, departureId)
    }

  implicit lazy val arbitraryMode5or7: Arbitrary[Mode5or7] =
    Arbitrary {
      for {
        code <- Gen.oneOf(Mode5or7.Constants.codes).map(_.toInt)
      } yield Mode5or7(code)
    }

  implicit lazy val arbitraryNonSpecialMode: Arbitrary[NonSpecialMode] =
    Arbitrary {
      for {
        code                   <- Gen.const(42)
        nationalityAtDeparture <- arbitrary[CountryCode]
        departureId            <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield
        NonSpecialMode(
          code,
          Some(nationalityAtDeparture),
          departureId
        )
    }

  implicit lazy val arbitraryTransportDetails: Arbitrary[TransportDetails] =
    Arbitrary {
      for {
        inlandMode      <- arbitrary[InlandMode]
        detailsAtBorder <- arbitrary[DetailsAtBorder]
      } yield
        TransportDetails(
          inlandMode,
          detailsAtBorder
        )
    }

  implicit def arbitraryTraderDetails(procedureType: ProcedureType): Arbitrary[TraderDetails] = {
    val pricipalAddress  = Arbitrary(arbitrary[PrincipalAddress].map(Address.prismAddressToPrincipalAddress.reverseGet))
    val consignorAddress = Arbitrary(arbitrary[ConsignorAddress].map(Address.prismAddressToConsignorAddress.reverseGet))
    val consigneeAddress = Arbitrary(arbitrary[ConsigneeAddress].map(Address.prismAddressToConsigneeAddress.reverseGet))

    Arbitrary {
      for {
        principalTraderDetails <- arbitraryRequiredDetails(pricipalAddress, procedureType).arbitrary
        consignor              <- Gen.option(arbitraryTraderInformation(consignorAddress).arbitrary)
        consignee              <- Gen.option(arbitraryTraderInformation(consigneeAddress).arbitrary)
      } yield TraderDetails(principalTraderDetails, consignor, consignee)
    }
  }

  implicit val arbitraryItemSecurityTraderDetails: Arbitrary[ItemsSecurityTraderDetails] = {
    val consignorAddress = Arbitrary(arbitrary[ConsignorAddress].map(Address.prismAddressToConsignorAddress.reverseGet))
    val consigneeAddress = Arbitrary(arbitrary[ConsigneeAddress].map(Address.prismAddressToConsigneeAddress.reverseGet))

    Arbitrary {
      for {
        methodOfPayment           <- Gen.option(nonEmptyString)
        commercialReferenceNumber <- Gen.option(nonEmptyString)
        dangerousGoodsCode        <- Gen.option(nonEmptyString)
        consignor                 <- Gen.option(arbitraryItemsSecurityTraderDetails(consignorAddress).arbitrary)
        consignee                 <- Gen.option(arbitraryItemsSecurityTraderDetails(consigneeAddress).arbitrary)
      } yield ItemsSecurityTraderDetails(methodOfPayment, commercialReferenceNumber, dangerousGoodsCode, consignor, consignee)
    }
  }

  implicit def arbitraryItemsSecurityTraderDetails(implicit arbAddress: Arbitrary[Address]): Arbitrary[ItemsSecurityTraderDetails.SecurityTraderDetails] =
    Arbitrary(Gen.oneOf(Arbitrary.arbitrary[SecurityPersonalInformation], Arbitrary.arbitrary[SecurityTraderEori]))

  implicit lazy val arbitraryItemsSecurityTraderEori: Arbitrary[SecurityTraderEori] =
    Arbitrary(Arbitrary.arbitrary[EoriNumber].map(SecurityTraderEori(_)))

  implicit def arbitraryItemsSecurityPersonalInformation(implicit arbAddress: Arbitrary[Address]): Arbitrary[SecurityPersonalInformation] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
      } yield SecurityPersonalInformation(name, address)
    }

  implicit def arbitraryRequiredDetails(implicit arbAddress: Arbitrary[Address], procedureType: ProcedureType): Arbitrary[RequiredDetails] =
    if (procedureType == ProcedureType.Simplified) {
      Arbitrary(Arbitrary.arbitrary[TraderEori])
    } else {
      Arbitrary(Gen.oneOf(Arbitrary.arbitrary[PersonalInformation], Arbitrary.arbitrary[TraderEori]))
    }

  implicit lazy val arbitraryTraderEori: Arbitrary[TraderEori] =
    Arbitrary(Arbitrary.arbitrary[EoriNumber].map(TraderEori(_)))

  implicit def arbitraryPersonalInformation(implicit arbAddress: Arbitrary[Address]): Arbitrary[PersonalInformation] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
      } yield PersonalInformation(name, address)
    }

  implicit def arbitraryTraderInformation(implicit arbAddress: Arbitrary[Address]): Arbitrary[TraderInformation] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
        eori    <- Gen.option(arbitrary[EoriNumber])
      } yield TraderInformation(name, address, eori)
    }

  implicit def arbitraryItemRequiredDetails(
    implicit
    arbAddress: Arbitrary[Address]): Arbitrary[models.journeyDomain.ItemTraderDetails.RequiredDetails] =
    Arbitrary {
      for {
        name    <- stringsWithMaxLength(stringMaxLength)
        address <- arbAddress.arbitrary
        eori    <- Gen.option(arbitrary[EoriNumber])
      } yield models.journeyDomain.ItemTraderDetails.RequiredDetails(name, address, eori)
    }

  implicit def arbitraryItemSection: Arbitrary[ItemSection] =
    Arbitrary {
      for {
        containersUsed            <- arbitrary[Boolean]
        isSecurityDetailsRequired <- arbitrary[Boolean]
        addDocument               <- arbitrary[Boolean]
        otherIndicator            <- nonEmptyString
        circumstanceIndicator <- if (isSecurityDetailsRequired) { Gen.oneOf(CircumstanceIndicator.conditionalIndicators :+ otherIndicator).map(Some(_)) } else
          Gen.const(None)
        itemSection <- genItemSectionOld(containersUsed, addDocument, circumstanceIndicator)
      } yield itemSection
    }

  def genItemSection(
    containersUsed: Boolean,
    addSafetyAndSecurity: Boolean,
    safetyAndSecurity: SafetyAndSecurity,
    movementDetails: MovementDetails,
    routeDetails: RouteDetails
  ): Gen[ItemSection] = {
    val consignorAddress = Arbitrary(arbitrary[ConsignorAddress].map(Address.prismAddressToConsignorAddress.reverseGet))
    val consigneeAddress = Arbitrary(arbitrary[ConsigneeAddress].map(Address.prismAddressToConsigneeAddress.reverseGet))

    val isDocumentTypeMandatory = addSafetyAndSecurity &&
      safetyAndSecurity.commercialReferenceNumber.isDefined

    val isPreviousReferenceMandatory: Boolean = (movementDetails.declarationType, routeDetails.countryOfDispatch) match {
      case (Option2 | Option4, code) if nonEUCountries.contains(code) => true
      case _                                                          => false
    }

    for {
      itemDetail                <- arbitrary[ItemDetails]
      itemConsignor             <- Gen.option(arbitraryItemRequiredDetails(consignorAddress).arbitrary)
      itemConsignee             <- Gen.option(arbitraryItemRequiredDetails(consigneeAddress).arbitrary)
      packages                  <- nonEmptyListOf[Packages](1)
      containers                <- if (containersUsed) { nonEmptyListOf[Container](1).map(Some(_)) } else Gen.const(None)
      specialMentions           <- Gen.option(nonEmptyListOf[SpecialMention](1))
      producedDocuments         <- if (isDocumentTypeMandatory) { nonEmptyListOf[ProducedDocument](1).map(Some(_)) } else Gen.const(None)
      methodOfPayment           <- arbitrary[String]
      commercialReferenceNumber <- arbitrary[String]
      previousReferences        <- if (isPreviousReferenceMandatory) nonEmptyListOf[PreviousReferences](1).map(Some(_)) else Gen.const(None)
      itemSecurityTraderDetails <- if (addSafetyAndSecurity) arbitrary[ItemsSecurityTraderDetails].map {
        itemsSecurityTraderDetails =>
          {
            val setMethodOfPayment = safetyAndSecurity.paymentMethod match {
              case None    => Some(methodOfPayment)
              case Some(_) => None
            }

            val setCommercialReferenceNumber = safetyAndSecurity.commercialReferenceNumber match {
              case None    => Some(commercialReferenceNumber)
              case Some(_) => None
            }

            Some(itemsSecurityTraderDetails.copy(methodOfPayment = setMethodOfPayment, commercialReferenceNumber = setCommercialReferenceNumber))
          }
      } else Gen.const(None)
    } yield
      ItemSection(itemDetail,
                  itemConsignor,
                  itemConsignee,
                  packages,
                  containers,
                  specialMentions,
                  producedDocuments,
                  itemSecurityTraderDetails,
                  previousReferences)
  }

  def genItemSectionOld(
    containersUsed: Boolean               = false,
    addDocument: Boolean                  = false,
    circumstanceIndicator: Option[String] = None
  ): Gen[ItemSection] = {

    val consignorAddress = Arbitrary(arbitrary[ConsignorAddress].map(Address.prismAddressToConsignorAddress.reverseGet))
    val consigneeAddress = Arbitrary(arbitrary[ConsigneeAddress].map(Address.prismAddressToConsigneeAddress.reverseGet))

    val documentTypeIsMandatory = circumstanceIndicator.fold(addDocument)(CircumstanceIndicator.conditionalIndicators.contains(_))

    for {
      itemDetail                <- arbitrary[ItemDetails]
      itemConsignor             <- Gen.option(arbitraryItemRequiredDetails(consignorAddress).arbitrary)
      itemConsignee             <- Gen.option(arbitraryItemRequiredDetails(consigneeAddress).arbitrary)
      packages                  <- nonEmptyListOf[Packages](1)
      containers                <- if (containersUsed) { nonEmptyListOf[Container](1).map(Some(_)) } else Gen.const(None)
      specialMentions           <- Gen.option(nonEmptyListOf[SpecialMention](1))
      producedDocuments         <- if (documentTypeIsMandatory) { nonEmptyListOf[ProducedDocument](1).map(Some(_)) } else Gen.const(None)
      previousReferences        <- Gen.option(nonEmptyListOf[PreviousReferences](1))
      itemSecurityTraderDetails <- Gen.option(arbitrary[ItemsSecurityTraderDetails])

    } yield
      ItemSection(itemDetail,
                  itemConsignor,
                  itemConsignee,
                  packages,
                  containers,
                  specialMentions,
                  producedDocuments,
                  itemSecurityTraderDetails,
                  previousReferences)
  }

  implicit lazy val arbitraryPreTaskListDetails: Arbitrary[PreTaskListDetails] =
    Arbitrary {
      for {
        lrn                <- arbitrary[LocalReferenceNumber]
        procedureType      <- arbitrary[ProcedureType]
        addSecurityDetails <- arbitrary[Boolean]
      } yield PreTaskListDetails(lrn, procedureType, addSecurityDetails)
    }

  implicit lazy val arbitraryGuaranteeDetails: Arbitrary[GuaranteeDetails] =
    Arbitrary(Gen.oneOf(arbitrary[GuaranteeReference], arbitrary[GuaranteeOther]))

  implicit lazy val arbitraryGuaranteeOther: Arbitrary[GuaranteeOther] =
    Arbitrary {
      for {
        guaranteeType  <- Arbitrary.arbitrary[GuaranteeType]
        otherReference <- nonEmptyString
      } yield GuaranteeOther(guaranteeType, otherReference)
    }

  implicit lazy val arbitraryGuaranteeReference: Arbitrary[GuaranteeReference] =
    Arbitrary {
      for {
        guaranteeType            <- Arbitrary.arbitrary[GuaranteeType]
        guaranteeReferenceNumber <- nonEmptyString
        liabilityAmount          <- nonEmptyString
        accessCode               <- nonEmptyString
      } yield GuaranteeReference(guaranteeType, guaranteeReferenceNumber, liabilityAmount, accessCode)
    }

  implicit lazy val arbitraryPackages: Arbitrary[Packages] =
    Arbitrary(Gen.oneOf(arbitrary[UnpackedPackages], arbitrary[BulkPackages], arbitrary[OtherPackages]))

  implicit lazy val arbitraryUnpackedPackages: Arbitrary[UnpackedPackages] =
    Arbitrary {
      for {
        packageType         <- arbitraryUnPackedPackageType.arbitrary
        howManyPackagesPage <- Gen.option(Gen.choose(1, 10))
        totalPieces         <- Gen.choose(1, 10)
        markOrNumber        <- Gen.option(nonEmptyString)
      } yield UnpackedPackages(packageType, howManyPackagesPage, totalPieces, markOrNumber)
    }

  implicit lazy val arbitraryBulkPackage: Arbitrary[BulkPackages] =
    Arbitrary {
      for {
        bulkPackage         <- arbitraryBulkPackageType.arbitrary
        howManyPackagesPage <- Gen.option(Gen.choose(1, 10))
        markOrNumber        <- Gen.option(nonEmptyString)
      } yield BulkPackages(bulkPackage, howManyPackagesPage, markOrNumber)
    }

  implicit lazy val arbitraryOtherPackage: Arbitrary[OtherPackages] =
    Arbitrary {
      for {
        code                <- nonEmptyString
        description         <- nonEmptyString
        howManyPackagesPage <- Gen.choose(1, 10)
        markOrNumber        <- nonEmptyString
      } yield OtherPackages(PackageType(code, description), howManyPackagesPage, markOrNumber)
    }

  implicit lazy val arbitraryItemDetails: Arbitrary[ItemDetails] =
    Arbitrary {
      for {
        itemDescription <- nonEmptyString
        totalGrossMass  <- genNumberString
        totalNetMass    <- Gen.option(genNumberString)
        commodityCode   <- Gen.option(nonEmptyString)
      } yield ItemDetails(itemDescription, totalGrossMass, totalNetMass, commodityCode)
    }

  implicit lazy val arbitrarySpecialMention: Arbitrary[SpecialMention] =
    Arbitrary {
      for {
        specialMentionType <- nonEmptyString
        additionalInfo     <- nonEmptyString
      } yield SpecialMention(specialMentionType, additionalInfo)
    }

  implicit lazy val arbitraryProducedDocument: Arbitrary[ProducedDocument] =
    Arbitrary {
      for {
        documentType      <- nonEmptyString
        documentReference <- nonEmptyString
        extraInformation  <- Gen.option(nonEmptyString)
      } yield ProducedDocument(documentType, documentReference, extraInformation)
    }

  implicit lazy val arbitraryContainer: Arbitrary[Container] =
    Arbitrary {
      for {
        containerNumber <- nonEmptyString
      } yield Container(containerNumber)
    }

  implicit lazy val arbitraryDeclarationForSelf: Arbitrary[DeclarationForSelf.type] =
    Arbitrary(Gen.const(DeclarationForSelf))

  implicit lazy val arbitraryDeclarationForSomeoneElse: Arbitrary[DeclarationForSomeoneElse] =
    Arbitrary {
      for {
        companyName <- stringsWithMaxLength(stringMaxLength)
        capacity    <- arbitrary[RepresentativeCapacity]
      } yield DeclarationForSomeoneElse(companyName, capacity)
    }

  implicit lazy val arbitraryDeclarationForSomeoneElseAnswer: Arbitrary[DeclarationForSomeoneElseAnswer] =
    Arbitrary(Gen.oneOf(arbitrary[DeclarationForSelf.type], arbitrary[DeclarationForSomeoneElse]))

  implicit lazy val arbitrarySimplifiedMovementDetails: Arbitrary[SimplifiedMovementDetails] =
    Arbitrary {
      for {
        declarationType           <- arbitrary[DeclarationType]
        containersUsed            <- arbitrary[Boolean]
        declarationPlacePage      <- stringsWithMaxLength(stringMaxLength)
        declarationForSomeoneElse <- arbitrary[DeclarationForSomeoneElseAnswer]
      } yield
        SimplifiedMovementDetails(
          declarationType,
          containersUsed,
          declarationPlacePage,
          declarationForSomeoneElse
        )
    }

  implicit lazy val arbitraryNormalMovementDetails: Arbitrary[NormalMovementDetails] =
    Arbitrary {
      for {
        declarationType           <- arbitrary[DeclarationType]
        preLodge                  <- arbitrary[Boolean]
        containersUsed            <- arbitrary[Boolean]
        declarationPlacePage      <- stringsWithMaxLength(stringMaxLength)
        declarationForSomeoneElse <- arbitrary[DeclarationForSomeoneElseAnswer]
      } yield
        MovementDetails.NormalMovementDetails(
          declarationType,
          preLodge,
          containersUsed,
          declarationPlacePage,
          declarationForSomeoneElse
        )
    }

  implicit def arbitraryMovementDetails(procedureType: ProcedureType): Arbitrary[MovementDetails] =
    if (procedureType == ProcedureType.Normal) {
      Arbitrary(arbitrary[NormalMovementDetails])
    } else {
      Arbitrary(arbitrary[SimplifiedMovementDetails])
    }

  val genTransitInformationWithoutArrivalTime =
    for {
      transitOffice <- stringsWithMaxLength(stringMaxLength)
    } yield
      TransitInformation(
        transitOffice,
        None
      )

  val genTransitInformationWithArrivalTime =
    for {
      transitOffice <- stringsWithMaxLength(stringMaxLength)
      arrivalTime   <- arbitrary[LocalDateTime]
    } yield
      TransitInformation(
        transitOffice,
        Some(arrivalTime)
      )

  implicit lazy val arbitraryTransitInformation: Arbitrary[TransitInformation] =
    Arbitrary(Gen.oneOf(genTransitInformationWithoutArrivalTime, genTransitInformationWithArrivalTime))

  // TODO: refactor this. Remove parameter, make all transit informations consistent with security flag and create generator.
  implicit def arbitraryRouteDetails(safetyAndSecurityFlag: Boolean): Arbitrary[RouteDetails] =
    Arbitrary {
      for {
        countryOfDispatch  <- arbitrary[CountryCode]
        officeOfDeparture  <- arbitrary[CustomsOffice]
        destinationCountry <- arbitrary[CountryCode]
        destinationOffice  <- arbitrary[CustomsOffice]
        transitInformation <- transitInformation(safetyAndSecurityFlag)
      } yield
        RouteDetails(
          countryOfDispatch,
          officeOfDeparture,
          destinationCountry,
          destinationOffice,
          transitInformation
        )
    }

  private def transitInformation(safetyAndSecurityFlag: Boolean): Gen[NonEmptyList[TransitInformation]] =
    for {
      transitInformation <- Gen.listOfN(2, arbitrary[TransitInformation])
      dateTime           <- arbitrary[LocalDateTime]
      updatedTransitInformation = {
        if (safetyAndSecurityFlag) {
          transitInformation.map(_.copy(arrivalTime = Some(dateTime)))
        } else {
          transitInformation.map(_.copy(arrivalTime = None))
        }
      }
    } yield NonEmptyList(updatedTransitInformation.head, updatedTransitInformation.tail)

  implicit lazy val arbitraryGoodSummarySimplifiedDetails: Arbitrary[GoodSummarySimplifiedDetails] =
    Arbitrary {
      for {
        authorisedLocationCode <- stringsWithMaxLength(stringMaxLength)
        controlResultDateLimit <- arbitrary[LocalDate]
      } yield GoodSummarySimplifiedDetails(authorisedLocationCode, controlResultDateLimit)
    }

  implicit lazy val arbitraryGoodSummaryNormalDetails: Arbitrary[GoodSummaryNormalDetails] =
    Arbitrary {
      for {
        customsApprovedLocation <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield GoodSummaryNormalDetails(customsApprovedLocation)
    }

  implicit lazy val arbitraryGoodSummaryDetails: Arbitrary[GoodSummaryDetails] =
    Arbitrary {
      Gen.oneOf(arbitrary[GoodSummaryNormalDetails], arbitrary[GoodSummarySimplifiedDetails])
    }

  implicit def arbitraryGoodsSummary(safetyAndSecurity: Boolean)(implicit arbitraryGoodSummaryDetails: Arbitrary[GoodSummaryDetails]): Arbitrary[GoodsSummary] =
    Arbitrary {
      for {
        loadingPlace       <- if (safetyAndSecurity) { nonEmptyString.map(Some(_)) } else { Gen.const(None) }
        numberOfPackages   <- Gen.option(Gen.choose(1, 100))
        totalMass          <- Gen.choose(1, 100).map(_.toString)
        goodSummaryDetails <- arbitraryGoodSummaryDetails.arbitrary
        sealNumbers        <- listWithMaxLength[SealDomain](10)
      } yield
        GoodsSummary(
          numberOfPackages,
          totalMass,
          loadingPlace,
          goodSummaryDetails,
          sealNumbers
        )
    }

  implicit lazy val arbitraryPreviousReference: Arbitrary[PreviousReferences] =
    Arbitrary {
      for {
        referenceType     <- nonEmptyString
        previousReference <- nonEmptyString
        extraInformation  <- Gen.option(nonEmptyString)
      } yield {
        PreviousReferences(referenceType, previousReference, extraInformation)
      }
    }

}
