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
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.journeyDomain.SpecialMention.{SpecialMentionWithCAL, SpecialMentionWithType}
import models.journeyDomain.SpecialMentionSpec.setSpecialMentionsUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages._
import pages.addItems.specialMentions.{SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}

class SpecialMentionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "SpecialMention" - {

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[SpecialMention], arbitrary[UserAnswers]) {
          case (specialMention, userAnswers) =>
            val updatedUserAnswers = setSpecialMentionsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
            val result             = UserAnswersReader[SpecialMention](SpecialMention.parseSpecialMention(index, referenceIndex)).run(updatedUserAnswers)

            result.right.value mustEqual specialMention
        }
      }
    }
  }

  "SpecialMentionWithCAL" - {

    val mandatoryPage = SpecialMentionAdditionalInfoPage(index, referenceIndex)

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {

        forAll(arbitrary[SpecialMentionWithCAL], arbitrary[UserAnswers]) {
          case (specialMention, userAnswers) =>
            val updatedUserAnswers = setSpecialMentionsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
            val result             = UserAnswersReader[SpecialMention](SpecialMention.parseSpecialMention(index, referenceIndex)).run(updatedUserAnswers)

            result.right.value mustEqual specialMention
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {

        forAll(arbitrary[SpecialMentionWithCAL], arbitrary[UserAnswers]) {
          case (specialMention, userAnswers) =>
            val updatedUserAnswers = setSpecialMentionsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[SpecialMention](SpecialMention.parseSpecialMention(index, referenceIndex)).run(updatedUserAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }
    }
  }

  "SpecialMentionWithType" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      SpecialMentionTypePage(index, referenceIndex),
      SpecialMentionAdditionalInfoPage(index, referenceIndex)
    )

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {

        forAll(arbitrary[SpecialMentionWithType], arbitrary[UserAnswers]) {
          case (specialMention, userAnswers) =>
            val updatedUserAnswers = setSpecialMentionsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
            val result             = UserAnswersReader[SpecialMention](SpecialMention.parseSpecialMention(index, referenceIndex)).run(updatedUserAnswers)

            result.right.value mustEqual specialMention
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {

        forAll(arbitrary[SpecialMentionWithType], arbitrary[UserAnswers], mandatoryPages) {
          case (specialMention, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setSpecialMentionsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[SpecialMention](SpecialMention.parseSpecialMention(index, referenceIndex)).run(updatedUserAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }
    }
  }

}

object SpecialMentionSpec extends UserAnswersSpecHelper {

  def setSpecialMentionsUserAnswers(specialMention: SpecialMention, index: Index, referenceIndex: Index)(userAnswers: UserAnswers): UserAnswers =
    specialMention match {
      case SpecialMentionWithCAL(additionalInfo) =>
        userAnswers
          .unsafeSetVal(SpecialMentionAdditionalInfoPage(index, referenceIndex))(additionalInfo)
      case SpecialMention.SpecialMentionWithType(specialMention, additionalInfo) =>
        userAnswers
          .unsafeSetVal(SpecialMentionTypePage(index, referenceIndex))(specialMention)
          .unsafeSetVal(SpecialMentionAdditionalInfoPage(index, referenceIndex))(additionalInfo)
    }
}
