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

package forms.addItems.traderSecurityDetails

import base.SpecBase
import forms.Constants._
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class SecurityConsigneeEoriFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey         = "securityConsigneeEori.error.required"
  val lengthKey           = "securityConsigneeEori.error.length"
  val invalidCharacterKey = "securityConsigneeEori.error.invalid"
  val invalidFormatKey    = "securityConsigneeEori.error.invalidFormat"
  val form                = new SecurityConsigneeEoriFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthEoriNumber)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLengthEoriNumber,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthEoriNumber))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the EORI number regex" in {

      val expectedError =
        List(FormError(fieldName, invalidCharacterKey, Seq(validEoriCharactersRegex)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLengthEoriNumber) suchThat (!_.matches(validEoriCharactersRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the EORI number format regex" in {

      val expectedError =
        List(FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex)))

      val invalidString          = "^[0-9]{2}[0-9a-zA-Z]{1,15}"
      val invalidStringGenerator = RegexpGen.from(invalidString)

      forAll(invalidStringGenerator) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
