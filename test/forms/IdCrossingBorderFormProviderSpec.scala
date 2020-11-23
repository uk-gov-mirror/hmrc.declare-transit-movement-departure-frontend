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
import forms.Constants.{vehicleIdMaxLength, vehicleIdRegex}
import org.scalacheck.Gen
import play.api.data.FormError

class IdCrossingBorderFormProviderSpec extends StringFieldBehaviours {

  val requiredKey       = "idCrossingBorder.error.required"
  val lengthKey         = "idCrossingBorder.error.length"
  val invalidCharacters = "idCrossingBorder.error.invalidCharacters"
  val form              = new IdCrossingBorderFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(vehicleIdMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = vehicleIdMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(vehicleIdMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the id regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharacters, Seq(vehicleIdRegex)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(vehicleIdMaxLength) suchThat (!_.matches(vehicleIdRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
