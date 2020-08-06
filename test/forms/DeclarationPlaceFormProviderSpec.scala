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
import models.LocalReferenceNumber
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError

class DeclarationPlaceFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "declarationPlace.error.required"
  val lengthKey = "declarationPlace.error.length"
  val invalidKey = "declarationPlace.error.invalid"
  val maxLength = 9
  val postCodeRegex: String = "^[a-zA-Z]{1,2}([0-9]{1,2}|[0-9][a-zA-Z])\\s*[0-9][a-zA-Z]{2}$"


  val form = new DeclarationPlaceFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings longer than max length" in {

      val expectedError = FormError(fieldName, lengthKey, Seq(maxLength))

      forAll(stringsLongerThan(maxLength + 1)) {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings that do not match regex" in {

      val expectedError = FormError(fieldName, invalidKey, Seq(postCodeRegex))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(postCodeRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
