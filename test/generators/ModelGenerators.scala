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

import models._
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryPrincipalAddress: Arbitrary[PrincipalAddress] =
    Arbitrary {
      for {
        Number and street <- arbitrary[String]
        Town <- arbitrary[String]
      } yield PrincipalAddress(Number and street, Town)
    }

 self: Generators =>

  implicit lazy val arbitraryRepresentativeCapacity: Arbitrary[RepresentativeCapacity] =
    Arbitrary {
      Gen.oneOf(RepresentativeCapacity.values.toSeq)
    }

  implicit lazy val arbitraryProcedureType: Arbitrary[ProcedureType] =
    Arbitrary {
      Gen.oneOf(ProcedureType.values.toSeq)
    }

  implicit lazy val arbitraryDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      Gen.oneOf(DeclarationType.values.toSeq)
    }

  implicit lazy val arbitraryLocalReferenceNumber: Arbitrary[LocalReferenceNumber] =
    Arbitrary {
      for {
        lrn <- alphaNumericWithMaxLength(22)
      } yield new LocalReferenceNumber(lrn)
    }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] = {
    Arbitrary {
      for {
        number <- stringsWithMaxLength(17)
      } yield EoriNumber(number)
    }
  }
}
