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
import models.domain.NetMass.Constants._
import org.scalacheck.Gen
import play.api.data.FormError

class TotalNetMassFormProviderSpec extends StringFieldBehaviours with SpecBase {

  val form = new TotalNetMassFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthNetMass)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLengthNetMass,
      lengthError = FormError(fieldName, lengthKeyNetMass, Seq(index.display))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKeyNetMass, Seq(index.display))
    )

    "must not bind strings that do not match the total net mass invalid characters regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharactersKeyNetMass, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLengthNetMass) suchThat (!_.matches(totalNetMassInvalidCharactersRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the total net mass invalid format regex" in {

      val expectedError =
        List(FormError(fieldName, invalidFormatKeyNetMass, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        decimals
          .suchThat(_.length < 16)
          .suchThat(_.matches(totalNetMassInvalidCharactersRegex))
          .suchThat(!_.matches(totalNetMassInvalidFormatRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind string of '0'" in {

      val invalidString = "0"
      val expectedError = List(FormError(fieldName, invalidAmountKeyNetMass, Seq(index.display)))
      val result        = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors mustBe expectedError
    }
  }
}
