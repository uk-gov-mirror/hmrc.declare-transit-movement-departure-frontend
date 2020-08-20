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
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators {
  self: Generators =>

  implicit lazy val arbitraryIsPrincipalEoriKnownUserAnswersEntry: Arbitrary[(IsPrincipalEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsPrincipalEoriKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsPrincipalEoriUserAnswersEntry: Arbitrary[(WhatIsPrincipalEoriPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[WhatIsPrincipalEoriPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryRepresentativeCapacityUserAnswersEntry: Arbitrary[(RepresentativeCapacityPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RepresentativeCapacityPage.type]
        value <- arbitrary[RepresentativeCapacity].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRepresentativeNameUserAnswersEntry: Arbitrary[(RepresentativeNamePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[RepresentativeNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryContainersUsedPageUserAnswersEntry: Arbitrary[(ContainersUsedPage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[ContainersUsedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationForSomeoneElseUserAnswersEntry: Arbitrary[(DeclarationForSomeoneElsePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationForSomeoneElsePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationPlaceUserAnswersEntry: Arbitrary[(DeclarationPlacePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[DeclarationPlacePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProcedureTypeUserAnswersEntry: Arbitrary[(ProcedureTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProcedureTypePage.type]
        value <- arbitrary[ProcedureType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationTypeUserAnswersEntry: Arbitrary[(DeclarationTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationTypePage.type]
        value <- arbitrary[DeclarationType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddSecurityDetailsUserAnswersEntry: Arbitrary[(AddSecurityDetailsPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[AddSecurityDetailsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLocalReferenceNumberUserAnswersEntry: Arbitrary[(LocalReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LocalReferenceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
}
