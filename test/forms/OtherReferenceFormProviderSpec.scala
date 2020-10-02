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

package forms

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class OtherReferenceFormProviderSpec extends StringFieldBehaviours {

  val requiredKey                       = "otherReference.error.required"
  val lengthKey                         = "otherReference.error.length"
  val exactLength                       = 35
  val invalidKey                        = "otherReference.error.invalid"
  val otherReferenceNumberRegex: String = "^[a-zA-Z0-9]{35}$"

  val form = new OtherReferenceFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(exactLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match regex" in {

      val expectedError = List(FormError(fieldName, invalidKey, Seq(otherReferenceNumberRegex)))
      val genInvalidString: Gen[String] = {
        stringsWithLength(exactLength) suchThat (!_.matches(otherReferenceNumberRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
