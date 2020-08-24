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

class PrincipalNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "principalName.error.required"
  val lengthKey = "principalName.error.length"
  val maxLengthPrincipalName = 35
  val invalidCharacters = "principalName.error.invalidCharacters"
  val invalidFormatKey = "principalName.error.invalidFormat"
  val principalNameRegex: String = "^([a-zA-Z0-9@'><\\/?%&.-_]{1,35})$"
  val validPrincipalNameCharactersRegex: String = "^[a-zA-Z0-9@'><\\/?%&.-_]*$"


  val form = new PrincipalNameFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthPrincipalName)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthPrincipalName,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthPrincipalName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the valid principal name characters regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharacters, Seq(validPrincipalNameCharactersRegex)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLengthPrincipalName) suchThat (!_.matches(validPrincipalNameCharactersRegex))
      }

      forAll(genInvalidString) { invalidString =>
        val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the principal name regex" in {

      val expectedError =
        List(FormError(fieldName, invalidFormatKey, Seq(principalNameRegex)))

      val genInvalidString: Gen[String] = {
        alphaNumericWithMaxLength(maxLengthPrincipalName).map(_.toUpperCase) suchThat (!_.matches(principalNameRegex))
      }

      forAll(genInvalidString) { invalidString =>
        val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors mustBe expectedError
      }
    }

  }
}
