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

package forms.addItems

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class DeclareMarkFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "declareMark.error.required"
  val lengthKey   = "declareMark.error.length"
  val maxLength   = 42

  def form(totalPackages: Option[Int] = None) = new DeclareMarkFormProvider()(totalPackages)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form(),
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form(),
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form(),
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must fail to bind if total packages are 0 and declare mark is 0" in {

      val result = form(Some(0)).bind(Map(fieldName -> "0")).apply(fieldName)

      result.errors must contain only FormError(fieldName, "declareMark.error.emptyNumberOfPackages")
    }

    "must not bind invalid input" in {

      val expectedRegex: String  = "^[a-zA-Z0-9]*$"
      val invalidCharacters      = "^[$&+,:;=?@#|'<>.^*()%!-]{1,42}$"
      val invalidStringGenerator = RegexpGen.from(invalidCharacters)

      forAll(invalidStringGenerator) {
        invalidString =>
          val result = form().bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe List(FormError(fieldName, "declareMark.error.format", Seq(expectedRegex)))
      }
    }
  }
}
