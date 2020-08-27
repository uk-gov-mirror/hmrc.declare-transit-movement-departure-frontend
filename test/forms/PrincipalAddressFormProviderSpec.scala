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

class PrincipalAddressFormProviderSpec extends StringFieldBehaviours {

  val principalName: String = "principal Name"
  val form = new PrincipalAddressFormProvider()(principalName)

  ".numberAndStreet" - {

    val fieldName = "numberAndStreet"
    val requiredKey = "principalAddress.error.numberAndStreet.required"
    val lengthKey = "principalAddress.error.numberAndStreet.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(principalName))
    )
  }

  ".town" - {

    val fieldName = "town"
    val requiredKey = "principalAddress.error.town.required"
    val lengthKey = "principalAddress.error.town.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(principalName))
    )
  }

  ".postcode" - {

    val fieldName = "postcode"
    val requiredKey = "principalAddress.error.postcode.required"
    val lengthKey = "principalAddress.error.postcode.length"
    val invalidKey = "principalAddress.error.postcode.invalid"
    val postCodeRegex: String = "^[a-zA-Z0-9]+([\\s]{1}[a-zA-Z0-9]+)*"
    val maxLength = 9

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(principalName))
    )

    "must not bind strings longer than max length" in {

      val expectedError = List(FormError(fieldName, lengthKey, Seq(maxLength)))

      forAll(stringsLongerThan(maxLength + 1)) { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match regex" in {

      val expectedError =
        List(FormError(fieldName, invalidKey, Seq(postCodeRegex)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(postCodeRegex))
      }

      forAll(genInvalidString) { invalidString =>
        val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors mustBe expectedError
      }
    }
  }
}
