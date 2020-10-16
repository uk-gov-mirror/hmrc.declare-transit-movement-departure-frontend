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

package models.journeyDomain

import cats.data._
import cats.implicits._
import models.{DeclarationType, RepresentativeCapacity, UserAnswers}
import pages._

sealed trait DeclarationForSomeoneElseAnswer

object DeclarationForSelf extends DeclarationForSomeoneElseAnswer {

  implicit val readDeclarationForSelf: UserAnswersReader[DeclarationForSelf.type] =
    DeclarationForSelf.pure[UserAnswersReader]

}

final case class DeclarationForSomeoneElse(
  companyName: String,
  capacity: RepresentativeCapacity
) extends DeclarationForSomeoneElseAnswer

object DeclarationForSomeoneElse {

  implicit val readDeclarationForSomeoneElse: UserAnswersReader[DeclarationForSomeoneElse] =
    (
      RepresentativeNamePage.read,
      RepresentativeCapacityPage.read
    ).tupled.map(DeclarationForSomeoneElse.apply _ tupled)
}

sealed trait MovementDetails

final case class NormalMovementDetails(
  declarationType: DeclarationType,
  prelodge: Boolean,
  containersUsed: Boolean,
  declarationPlacePage: String,
  declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
) extends MovementDetails

final case class SimplifiedMovementDetails(
  declarationType: DeclarationType,
  containersUsed: Boolean,
  declarationPlacePage: String,
  declarationForSomeoneElse: DeclarationForSomeoneElseAnswer
) extends MovementDetails

object SimplifiedMovementDetails {

  implicit val makeSimplifiedMovementDetails: ParseUserAnswers[Option, SimplifiedMovementDetails] = {

    val declarationForSomeoneElseAnswer: UserAnswersReader[DeclarationForSomeoneElseAnswer] =
      DeclarationForSomeoneElsePage.read.flatMap(
        bool =>
          if (bool) {
            UserAnswersReader[DeclarationForSomeoneElse].widen[DeclarationForSomeoneElseAnswer]
          } else {
            UserAnswersReader[DeclarationForSelf.type].widen[DeclarationForSomeoneElseAnswer]
        }
      )

    ParseUserAnswers(
      (
        DeclarationTypePage.read,
        ContainersUsedPage.read,
        DeclarationPlacePage.read,
        declarationForSomeoneElseAnswer
      ).tupled
    )(SimplifiedMovementDetails.apply _ tupled)
  }

}
