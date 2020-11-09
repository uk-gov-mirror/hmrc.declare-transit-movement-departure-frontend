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

import models.{DeclarationType, RepresentativeCapacity}
import models.journeyDomain.MovementDetails.{DeclarationForSelf, DeclarationForSomeoneElse, DeclarationForSomeoneElseAnswer, SimplifiedMovementDetails}
import models.journeyDomain.RouteDetails
import models.journeyDomain.RouteDetails.TransitInformation
import models.reference.CountryCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait JourneyModelGenerators {
  self: Generators =>

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
