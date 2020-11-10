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

import java.time.LocalDateTime

import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.{DeclarationType, GuaranteeType, RepresentativeCapacity, UserAnswers}
import models.journeyDomain.MovementDetails.{
  DeclarationForSelf,
  DeclarationForSomeoneElse,
  DeclarationForSomeoneElseAnswer,
  NormalMovementDetails,
  SimplifiedMovementDetails
}
import models.journeyDomain.{GuaranteeDetails, MovementDetails, RouteDetails}
import models.journeyDomain.RouteDetails.TransitInformation
import models.reference.CountryCode
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
        useDefaultAmount         <- Gen.option(Arbitrary.arbitrary[Boolean])
        accessCode               <- nonEmptyString
      } yield GuaranteeReference(guaranteeType, guaranteeReferenceNumber, liabilityAmount, useDefaultAmount, accessCode)
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
}
