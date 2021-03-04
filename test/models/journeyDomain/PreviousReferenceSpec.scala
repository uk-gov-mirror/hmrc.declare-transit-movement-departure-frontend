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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.journeyDomain.PreviousReferenceSpec.setPreviousReferenceUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Gen
import pages.QuestionPage
import pages.addItems.{AddExtraInformationPage, ExtraInformationPage, PreviousReferencePage, ReferenceTypePage}

class PreviousReferenceSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "previousReference" - {

    "can be parsed from UserAnswers when" - {

      "when all details for the section have been answered" in {
        forAll(arb[PreviousReferences], nonEmptyString, arb[UserAnswers]) {
          (previousReference, extraInformation, userAnswers) =>
            val expectedResult = previousReference.copy(extraInformation = Some(extraInformation))

            val setUserAnswers = setPreviousReferenceUserAnswers(expectedResult, index, referenceIndex)(userAnswers)
              .set(AddExtraInformationPage(index, referenceIndex), true)
              .success
              .value

            val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(setUserAnswers)

            result.value mustBe expectedResult
        }
      }

      "when AddExtraInformation is false" in {
        forAll(arb[PreviousReferences], arb[UserAnswers]) {
          (previousReference, userAnswers) =>
            val expectedResult = previousReference.copy(extraInformation = None)

            val setUserAnswers = setPreviousReferenceUserAnswers(expectedResult, index, referenceIndex)(userAnswers)
              .set(AddExtraInformationPage(index, referenceIndex), false)
              .success
              .value

            val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(setUserAnswers)

            result.value mustBe expectedResult
        }
      }
    }

    "cannot be parsed from UserAnswers when" - {

      "a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          ReferenceTypePage(index, referenceIndex),
          PreviousReferencePage(index, referenceIndex),
          AddExtraInformationPage(index, referenceIndex)
        )

        forAll(arb[PreviousReferences], arb[UserAnswers], mandatoryPages) {
          (previousReference, userAnswers, mandatoryPage) =>

            val setUserAnswers = setPreviousReferenceUserAnswers(previousReference, index, referenceIndex)(userAnswers)
              .remove(mandatoryPage).success.value

            val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(setUserAnswers)

            result mustBe None
        }
      }
    }
  }

}

object PreviousReferenceSpec extends UserAnswersSpecHelper {

  def setPreviousReferenceUserAnswers(previousReference: PreviousReferences, index: Index, referenceIndex: Index)(statUserAnswers: UserAnswers): UserAnswers = {
    val ua = statUserAnswers
      .unsafeSetVal(ReferenceTypePage(index, referenceIndex))(previousReference.referenceType)
      .unsafeSetVal(PreviousReferencePage(index, referenceIndex))(previousReference.previousReference)
      .unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(previousReference.extraInformation.isDefined)

    previousReference.extraInformation.fold(ua) {
      info =>
        ua.unsafeSetVal(ExtraInformationPage(index, referenceIndex))(info)
    }
  }
}
