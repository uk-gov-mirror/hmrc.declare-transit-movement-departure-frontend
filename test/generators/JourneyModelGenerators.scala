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

package generators

import java.time.{LocalDate, LocalDateTime}

import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetails, GoodSummarySimplifiedDetails}
import models.journeyDomain.GuaranteeDetails._
import models.journeyDomain.MovementDetails._
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain._
import models.journeyDomain.RouteDetails.TransitInformation
import models.reference._
import models.journeyDomain.{GoodsSummary, GuaranteeDetails, ItemDetails, MovementDetails, RouteDetails}
import models.{DeclarationType, GuaranteeType, RepresentativeCapacity}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait JourneyModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryGuaranteeDetails: Arbitrary[GuaranteeDetails] =
    Arbitrary(Gen.oneOf(arbitrary[GuaranteeReference], arbitrary[GuaranteeOther]))

  implicit lazy val arbitraryGuaranteeOther: Arbitrary[GuaranteeOther] =
    Arbitrary {
      for {
        guaranteeType   <- Arbitrary.arbitrary[GuaranteeType]
        otherReference  <- nonEmptyString
        liabilityAmount <- nonEmptyString
      } yield GuaranteeOther(guaranteeType, otherReference, liabilityAmount)
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
        markOrNumber        <- Gen.option(arbitrary[String])
      } yield UnpackedPackages(packageType, howManyPackagesPage, totalPieces, markOrNumber)
    }

  implicit lazy val arbitraryBulkPackage: Arbitrary[BulkPackages] =
    Arbitrary {
      for {
        bulkPackage         <- arbitraryBulkPackageType.arbitrary
        howManyPackagesPage <- Gen.option(Gen.choose(1, 10))
        markOrNumber        <- Gen.option(arbitrary[String])
      } yield BulkPackages(bulkPackage, howManyPackagesPage, markOrNumber)
    }

  implicit lazy val arbitraryOtherPackage: Arbitrary[OtherPackages] =
    Arbitrary {
      for {
        code                <- nonEmptyString
        description         <- nonEmptyString
        howManyPackagesPage <- Gen.choose(1, 10)
        markOrNumber        <- arbitrary[String]
      } yield OtherPackages(PackageType(code, description), howManyPackagesPage, markOrNumber)
    }

  implicit lazy val arbitraryItemDetails: Arbitrary[ItemDetails] =
    Arbitrary {
      for {
        itemDescription <- nonEmptyString
        totalGrossMass  <- nonEmptyString
        totalNetMass    <- Gen.option(arbitrary[String])
        commodityCode   <- Gen.option(arbitrary[String])
      } yield ItemDetails(itemDescription, totalGrossMass, totalNetMass, commodityCode)
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

  implicit lazy val arbitraryMovementDetails: Arbitrary[MovementDetails] =
    Arbitrary(Gen.oneOf(arbitrary[NormalMovementDetails], arbitrary[SimplifiedMovementDetails]))

  implicit lazy val arbitraryTransitInformation: Arbitrary[TransitInformation] =
    Arbitrary {
      for {
        transitOffice <- stringsWithMaxLength(stringMaxLength)
        arrivalTime   <- arbitrary[LocalDateTime]
      } yield
        TransitInformation(
          transitOffice,
          arrivalTime
        )
    }

  implicit lazy val arbitraryRouteDetails: Arbitrary[RouteDetails] =
    Arbitrary {
      for {
        countryOfDispatch  <- arbitrary[CountryCode]
        officeOfDeparture  <- stringsWithMaxLength(stringMaxLength)
        destinationCountry <- arbitrary[CountryCode]
        destinationOffice  <- stringsWithMaxLength(stringMaxLength)
        transitInformation <- nonEmptyListOf[TransitInformation](10)
      } yield
        RouteDetails(
          countryOfDispatch,
          officeOfDeparture,
          destinationCountry,
          destinationOffice,
          transitInformation
        )
    }

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

  implicit def arbitraryGoodsSummary(implicit arbitraryGoodSummaryDetails: Arbitrary[GoodSummaryDetails]): Arbitrary[GoodsSummary] =
    Arbitrary {
      for {
        numberOfPackages   <- Gen.option(Gen.choose(1, 100))
        totalMass          <- Gen.choose(1, 100).map(_.toString)
        loadingPlace       <- Gen.option(stringsWithMaxLength(stringMaxLength)) // TODO: awaiting implementation
        goodSummaryDetails <- arbitraryGoodSummaryDetails.arbitrary
        sealNumbers        <- listWithMaxLength[SealDomain](10)
      } yield
        GoodsSummary(
          numberOfPackages,
          totalMass,
          None, // loadingPlace,
          goodSummaryDetails,
          sealNumbers
        )
    }
}
