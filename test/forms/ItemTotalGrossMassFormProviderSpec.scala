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
import models.domain.GrossMass.Constants._

class ItemTotalGrossMassFormProviderSpec extends StringFieldBehaviours with SpecBase {

  val form = new ItemTotalGrossMassFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthGrossMass)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLengthGrossMass,
      lengthError = FormError(fieldName, lengthKeyGrossMass, Seq(maxLengthGrossMass))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKeyGrossMass, Seq(index.display))
    )

    "must not bind strings that do not match the total gross mass invalid characters regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharactersKeyGrossMass, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLengthGrossMass) suchThat (!_.matches(totalGrossMassInvalidCharactersRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the total gross mass invalid format regex" in {

      val expectedError =
        List(FormError(fieldName, invalidFormatKeyGrossMass, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        decimals
          .suchThat(_.length < 16)
          .suchThat(_.matches(totalGrossMassInvalidCharactersRegex))
          .suchThat(!_.matches(totalGrossMassInvalidFormatRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind string of '0'" in {

      val invalidString = "0"
      val expectedError = List(FormError(fieldName, invalidAmountKeyGrossMass, Seq(index.display)))
      val result        = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors mustBe expectedError
    }

  }
}
