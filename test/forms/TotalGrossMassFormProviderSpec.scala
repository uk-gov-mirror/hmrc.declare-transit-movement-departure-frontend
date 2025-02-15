/*
 * Copyright 2021 HM Revenue & Customs
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
import models.domain.GrossMass.Constants._
import wolfendale.scalacheck.regexp.RegexpGen

class TotalGrossMassFormProviderSpec extends StringFieldBehaviours {

  private val form = new TotalGrossMassFormProvider()()

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
      lengthError = FormError(fieldName, lengthKeyTotalGrossMass, Seq(maxLengthGrossMass))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKeyTotalGrossMass)
    )

    "must not bind strings with invalid characters" in {
      val invalidKey             = "totalGrossMass.error.invalidCharacters"
      val expectedError          = FormError(fieldName, invalidKey, Seq(totalGrossMassInvalidFormatRegex))
      val generator: Gen[String] = RegexpGen.from(s"[a-zA-Z!£^*(){}_+=:;|`~,±üçñèé@]{15}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

  }
}
