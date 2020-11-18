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

package forms.goodsSummary

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class SealIdDetailsFormProviderSpec extends StringFieldBehaviours with SpecBase {

  val requiredKey             = "sealIdDetails.error.required"
  val lengthKey               = "sealIdDetails.error.length"
  val maxLength               = 20
  val invalidCharacters       = "sealIdDetails.error.invalidCharacters"
  val sealNumberRegex: String = "^[a-zA-Z0-9]*$"
  val form                    = new SealIdDetailsFormProvider()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form(sealIndex),
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form(sealIndex),
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form(sealIndex),
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the seal number regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharacters, Seq(sealNumberRegex)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(sealNumberRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form(sealIndex).bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

  }
}
