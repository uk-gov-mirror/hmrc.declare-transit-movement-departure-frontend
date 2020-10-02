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
import forms.guaranteeDetails.GuaranteeReferenceFormProvider
import org.scalacheck.Gen
import play.api.data.FormError

class GuaranteeReferenceFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "guaranteeReference.error.required"
  val lengthKey = "guaranteeReference.error.length"
  val exactLength = 24
  val invalidKey = "guaranteeReference.error.invalid"
  val representativeNameRegex: String = "^[a-zA-Z0-9]{24}$"

  val form = new GuaranteeReferenceFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(exactLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match regex" in {

      val expectedError = List(FormError(fieldName, invalidKey, Seq(representativeNameRegex)))
      val genInvalidString: Gen[String] = {
        stringsWithLength(exactLength) suchThat (!_.matches(representativeNameRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
