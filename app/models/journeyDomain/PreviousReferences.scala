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

import cats.implicits._
import models.Index
import pages.addItems.{AddExtraInformationPage, ExtraInformationPage, PreviousReferencePage, ReferenceTypePage}

final case class PreviousReferences(
  referenceType: String,
  previousReference: String,
  extraInformation: Option[String]
)

object PreviousReferences {

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

}
