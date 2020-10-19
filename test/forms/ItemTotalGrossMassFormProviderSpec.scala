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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class ItemTotalGrossMassFormProviderSpec extends StringFieldBehaviours with SpecBase {

  val requiredKey                                  = "itemTotalGrossMass.error.required"
  val lengthKey                                    = "itemTotalGrossMass.error.length"
  val invalidCharactersKey                         = "itemTotalGrossMass.error.invalidCharacters"
  val invalidFormatKey                             = "itemTotalGrossMass.error.invalidFormat"
  val maxLength                                    = 15
  val totalGrossMassInvalidCharactersRegex: String = "^[0-9.]*$"
  val totalGrossMassInvalidFormatRegex: String     = "^[0-9]{1,11}(?:\\.[0-9]{1,3})?$"

  val form = new ItemTotalGrossMassFormProvider()(index)

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
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(index.display))
    )

    "must not bind strings that do not match the total gross mass invalid characters regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharactersKey, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(totalGrossMassInvalidCharactersRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the total gross mass invalid format regex" in {

      val expectedError =
        List(FormError(fieldName, invalidFormatKey, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        decimals suchThat (_.matches(totalGrossMassInvalidCharactersRegex))
        decimals suchThat (!_.matches(totalGrossMassInvalidFormatRegex))
        decimals suchThat (_.toDouble > 0.000)
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
