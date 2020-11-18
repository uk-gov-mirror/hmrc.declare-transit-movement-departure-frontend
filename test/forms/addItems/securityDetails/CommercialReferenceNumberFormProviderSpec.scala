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

package forms.addItems.securityDetails

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class CommercialReferenceNumberFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey                    = "commercialReferenceNumber.error.required"
  val lengthKey                      = "commercialReferenceNumber.error.length"
  val invalidKey                     = "commercialReferenceNumber.error.invalidCharacters"
  val maxLength                      = 70
  val commercialReferenceNumberRegex = "^[a-zA-Z0-9&'@\\/.\\-%?<>]{1,26}$"

  val form = new CommercialReferenceNumberFormProvider()()

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
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match regex" in {

      val expectedError = List(FormError(fieldName, invalidKey, Seq(commercialReferenceNumberRegex)))
      val genInvalidString: Gen[String] = {
        stringsWithLength(maxLength) suchThat (!_.matches(commercialReferenceNumberRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }

}
