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
import play.api.data.{Field, FormError}

class LiabilityAmountFormProviderSpec extends StringFieldBehaviours {

  val requiredKey                    = "liabilityAmount.error.required"
  val lengthKey                      = "liabilityAmount.error.length"
  val invalidCharactersKey           = "liabilityAmount.error.characters"
  val invalidFormatKey               = "liabilityAmount.error.invalidFormat"
  val greaterThanZeroErrorKey        = "liabilityAmount.error.greaterThanZero"
  val maxLength                      = 100
  val liabilityAmountCharactersRegex = "^$|^[0-9.]*$"
  val liabilityAmountFormatRegex     = "^$|([0-9]*(?:\\.[0-9]{1,2})?)$"
  val greaterThanZeroRegex           = "^$|([1-9]{1}[0-9.]*)$"

  val form = new LiabilityAmountFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    "must not bind strings that do not match invalid characters regex" in {

      val expectedError = List(FormError(fieldName, invalidCharactersKey, Seq(liabilityAmountCharactersRegex)))
      val genInvalidString: Gen[String] = {
        stringsWithLength(maxLength) suchThat (!_.matches(liabilityAmountCharactersRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match invalid format regex" in {

      val expectedError = List(FormError(fieldName, invalidFormatKey, Seq(liabilityAmountFormatRegex)))
      val genInvalidString: Gen[String] = {
        decimals
          .suchThat(_.matches(liabilityAmountCharactersRegex))
          .suchThat(!_.matches(liabilityAmountFormatRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match greater than zero regex" in {

      val expectedError = List(FormError(fieldName, greaterThanZeroErrorKey, Seq(greaterThanZeroRegex)))
      val invalidString = "0.5"
      val result        = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors mustBe expectedError
    }

  }
}
