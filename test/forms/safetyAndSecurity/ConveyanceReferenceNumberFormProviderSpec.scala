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

package forms.safetyAndSecurity

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class ConveyanceReferenceNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey          = "conveyanceReferenceNumber.error.required"
  val maxLengthKey         = "conveyanceReferenceNumber.error.maxLength"
  val minLengthKey         = "conveyanceReferenceNumber.error.minLength"
  val maxLength            = 8
  val minLength            = 7
  val RefRegex             = "^[a-zA-Z0-9]{7,8}$"
  val invalidCharactersKey = "conveyanceReferenceNumber.error.invalid"

  val form = new ConveyanceReferenceNumberFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, maxLengthKey, Seq(maxLength))
    )

    behave like fieldWithMinLength(
      form,
      fieldName,
      minLength   = minLength,
      lengthError = FormError(fieldName, minLengthKey, Seq(minLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the invalid characters regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharactersKey, Seq(RefRegex)))
      val genInvalidString: Gen[String] = {
        stringsWithLength(minLength) suchThat (!_.matches(RefRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
