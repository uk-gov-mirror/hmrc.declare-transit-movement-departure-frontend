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

package models.journeyDomain

import cats.data.{NonEmptyList, ReaderT}
import cats.implicits._
import derivable.DeriveNumberOfPreviousAdministrativeReferences
import models.DeclarationType.Option2
import models.reference.CountryCode
import models.{Index, UserAnswers}
import pages.addItems._
import pages.{CountryOfDispatchPage, DeclarationTypePage}

final case class PreviousReferences(
  referenceType: String,
  previousReference: String,
  extraInformation: Option[String]
)

object PreviousReferences {

  val nonEUCountries =
    Seq(CountryCode("AD"), CountryCode("IS"), CountryCode("LI"), CountryCode("NO"), CountryCode("SM"), CountryCode("SJ"), CountryCode("CH"))

  def previousReferenceReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[PreviousReferences] = {

    val extraInformation: UserAnswersReader[Option[String]] =
      AddExtraInformationPage(itemIndex, referenceIndex).reader.flatMap {
        case true  => ExtraInformationPage(itemIndex, referenceIndex).reader.map(Some(_))
        case false => none[String].pure[UserAnswersReader]
      }

    (
      ReferenceTypePage(itemIndex, referenceIndex).reader,
      PreviousReferencePage(itemIndex, referenceIndex).reader,
      extraInformation
    ).tupled.map((PreviousReferences.apply _).tupled)
  }

  def derivePreviousReferences(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[PreviousReferences]]] =
    (
      DeclarationTypePage.reader,
      CountryOfDispatchPage.reader
    ).tupled.flatMap {
      case (Option2, code) if nonEUCountries.contains(code) =>
        allPreviousReferencesReader(itemIndex) // Mandatory reader if 'T2' or 'T2F' and non EU country
      case _ =>
        AddAdministrativeReferencePage(itemIndex).reader.flatMap { // Optional reader if any other condition
          case true  => allPreviousReferencesReader(itemIndex)
          case false => none[NonEmptyList[PreviousReferences]].pure[UserAnswersReader]
        }
    }

  private def allPreviousReferencesReader(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[PreviousReferences]]] =
    DeriveNumberOfPreviousAdministrativeReferences(itemIndex).reader
      .filter(_.nonEmpty)
      .flatMap(
        _.zipWithIndex.traverse[UserAnswersReader, PreviousReferences]({
          case (_, index) =>
            PreviousReferences.previousReferenceReader(itemIndex, Index(index))
        })
      )
      .map(NonEmptyList.fromList)

}
