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

package pages.addItems

import generators.UserAnswersGenerator
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class DocumentTypePageSpec extends PageBehaviours with UserAnswersGenerator {

  private val index         = Index(0)
  private val documentIndex = Index(0)

  "DocumentTypePage" - {

    beRetrievable[String](DocumentTypePage(index, documentIndex))

    beSettable[String](DocumentTypePage(index, documentIndex))

    beRemovable[String](DocumentTypePage(index, documentIndex))

    "cleanUp" - {
      "must remove DocumentReferencePage when documentType is anything other then '952 (TIR carnet)'" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .set(DocumentTypePage(index, documentIndex), "xyz")
              .success
              .value

            result.get(DocumentReferencePage(index, documentIndex)) must not be defined
        }
      }

      "must not remove DocumentReferencePage when documentType is anything other then '952 (TIR carnet)'" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .set(DocumentReferencePage(index, documentIndex), "test")
              .success
              .value
              .set(DocumentTypePage(index, documentIndex), "952")
              .success
              .value

            result.get(DocumentReferencePage(index, documentIndex)) mustBe defined

        }
      }
    }
  }
}
