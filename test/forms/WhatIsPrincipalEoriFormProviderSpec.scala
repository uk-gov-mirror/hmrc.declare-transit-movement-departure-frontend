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
import play.api.data.FormError
import org.scalacheck.Gen

class WhatIsPrincipalEoriFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "whatIsPrincipalEori.error.required"
  val lengthKey = "whatIsPrincipalEori.error.length"
  val invalidCharactersKey = "whatIsPrincipalEori.error.invalidCharacters"
  val invalidFormatKey = "whatIsPrincipalEori.error.invalidFormat"
  val maxLength = 17
  val eoriNumberRegex: String = "^[A-Z]{2}[0-9]{1,15}"
  val validEoriCharactersRegex: String = "^[A-Z0-9]*$"

  val form = new WhatIsPrincipalEoriFormProvider()()

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
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the valid eori characters regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharactersKey, Seq(validEoriCharactersRegex)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(validEoriCharactersRegex))
      }

      forAll(genInvalidString) { invalidString =>
        val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the eori number regex" in {

      val expectedError =
        List(FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex)))

      val genInvalidString: Gen[String] = {
        alphaNumericWithMaxLength(maxLength).map(_.toUpperCase) suchThat (!_.matches(eoriNumberRegex))
      }

      forAll(genInvalidString) { invalidString =>
        val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors mustBe expectedError
      }
    }
  }
}
