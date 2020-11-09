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

import base.{GeneratorSpec, SpecBase}
import generators.JourneyModelGenerators
import models.{ProcedureType, UserAnswers}
import models.journeyDomain.MovementDetails.{DeclarationForSelf, DeclarationForSomeoneElse, NormalMovementDetails, SimplifiedMovementDetails}
import org.scalacheck.{Arbitrary, Gen}
import pages.{movementDetails, _}
import pages.movementDetails.PreLodgeDeclarationPage
import queries.Settable

class MovementDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "MovmentDetails" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(movementUserAnswers) {
          case (_, userAnswers) =>
            val result = UserAnswersParser[Option, MovementDetails].run(userAnswers)

            result must be(defined)
        }
      }
    }
  }

  "NormalMovmentDetails" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      ProcedureTypePage,
      DeclarationTypePage,
      PreLodgeDeclarationPage,
      ContainersUsedPage,
      DeclarationPlacePage,
      DeclarationForSomeoneElsePage
    )

    "can be parsed" - {
      "when all details for section have been answered" in {
        forAll(normalMovementUserAnswers) {
          case (expected, userAnswers) =>
            val result = UserAnswersParser[Option, NormalMovementDetails].run(userAnswers).value

            result mustEqual expected
        }
      }

    }

    "cannot be parsed from UserAnswers" - {
      "when an answer is missing" in {
        forAll(normalMovementUserAnswers, mandatoryPages) {
          case ((_, ua), mandatoryPage) =>
            val userAnswers = ua.remove(mandatoryPage).success.value

            val result = UserAnswersParser[Option, NormalMovementDetails].run(userAnswers)

            result mustEqual None
        }
      }

      "when the movement is a simplified" in {
        forAll(simpleMovementUserAnswers) {
          case (_, userAnswers) =>
            val result = UserAnswersParser[Option, NormalMovementDetails].run(userAnswers)

            result mustEqual None
        }
      }
    }
  }

  "SimplifiedMovementDetails" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      ProcedureTypePage,
      DeclarationTypePage,
      ContainersUsedPage,
      DeclarationPlacePage,
      DeclarationForSomeoneElsePage
    )

    "can be parsed from UserAnswers" - {
      "when all the answers have been answered" in {
        forAll(simpleMovementUserAnswers) {
          case (expected, userAnswers) =>
            val result = UserAnswersParser[Option, SimplifiedMovementDetails].run(userAnswers).value

            result mustEqual expected
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(simpleMovementUserAnswers, mandatoryPages) {
          case ((_, ua), mandatoryPage) =>
            val userAnswers = ua.remove(mandatoryPage).success.value

            val result = UserAnswersParser[Option, SimplifiedMovementDetails].run(userAnswers)

            result mustEqual None
        }
      }

      "when the movement is a simplified movement" in {
        forAll(normalMovementUserAnswers) {
          case (_, userAnswers) =>
            val result = UserAnswersParser[Option, SimplifiedMovementDetails].run(userAnswers)

            result mustEqual None
        }
      }
    }
  }

  val simpleMovementUserAnswers: Gen[(SimplifiedMovementDetails, UserAnswers)] =
    for {
      movementDetails <- Arbitrary.arbitrary[SimplifiedMovementDetails]
      baseUserAnswers <- Arbitrary.arbitrary[UserAnswers]
    } yield {
      val interstitialUserAnswers =
        baseUserAnswers
          .set(ProcedureTypePage, ProcedureType.Simplified)
          .toOption
          .value
          .set(DeclarationTypePage, movementDetails.declarationType)
          .toOption
          .value
          .set(ContainersUsedPage, movementDetails.containersUsed)
          .toOption
          .value
          .set(DeclarationPlacePage, movementDetails.declarationPlacePage)
          .toOption
          .value
          .set(DeclarationForSomeoneElsePage, movementDetails.declarationForSomeoneElse != DeclarationForSelf)
          .toOption
          .value

      val userAnswers = movementDetails.declarationForSomeoneElse match {
        case DeclarationForSelf =>
          interstitialUserAnswers
        case DeclarationForSomeoneElse(companyName, capacity) =>
          interstitialUserAnswers
            .set(RepresentativeNamePage, companyName)
            .toOption
            .value
            .set(RepresentativeCapacityPage, capacity)
            .toOption
            .value

      }

      (movementDetails, userAnswers)
    }

  private val normalMovementUserAnswers: Gen[(NormalMovementDetails, UserAnswers)] =
    for {
      movementDetails <- Arbitrary.arbitrary[NormalMovementDetails]
      baseUserAnswers <- Arbitrary.arbitrary[UserAnswers]
    } yield {
      val interstitialUserAnswers = baseUserAnswers
        .set(ProcedureTypePage, ProcedureType.Normal)
        .success
        .value
        .set(DeclarationTypePage, movementDetails.declarationType)
        .toOption
        .value
        .set(PreLodgeDeclarationPage, movementDetails.prelodge)
        .toOption
        .value
        .set(ContainersUsedPage, movementDetails.containersUsed)
        .toOption
        .value
        .set(DeclarationPlacePage, movementDetails.declarationPlacePage)
        .toOption
        .value
        .set(DeclarationForSomeoneElsePage, movementDetails.declarationForSomeoneElse != DeclarationForSelf)
        .toOption
        .value

      val userAnswers = movementDetails.declarationForSomeoneElse match {
        case DeclarationForSelf =>
          interstitialUserAnswers
        case DeclarationForSomeoneElse(companyName, capacity) =>
          interstitialUserAnswers
            .set(RepresentativeNamePage, companyName)
            .toOption
            .value
            .set(RepresentativeCapacityPage, capacity)
            .toOption
            .value
      }

      (movementDetails, userAnswers)
    }

  private val movementUserAnswers: Gen[(MovementDetails, UserAnswers)] =
    Gen.oneOf(simpleMovementUserAnswers, normalMovementUserAnswers)

}
