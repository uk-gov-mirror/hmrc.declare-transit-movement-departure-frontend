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

package forms.addItems.traderSecurityDetails

import base.SpecBase
import forms.Constants.{consigneeNameMaxLength, consignorNameRegex}
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class SecurityConsignorNameFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "securityConsignorName.error.required"
  val lengthKey   = "securityConsignorName.error.length"
  val invalidKey  = "securityConsignorName.error.invalid"

  val form = new SecurityConsignorNameFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(consigneeNameMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = consigneeNameMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consigneeNameMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the principal eori name regex" in {

      val expectedError =
        List(FormError(fieldName, invalidKey, Seq(consignorNameRegex)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(consigneeNameMaxLength) suchThat (!_.matches(consignorNameRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

  }
}
