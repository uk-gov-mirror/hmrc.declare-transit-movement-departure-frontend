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

package forms.safetyAndSecurity

import forms.behaviours.StringFieldBehaviours
import models.CountryList
import models.reference.{Country, CountryCode}
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class SafetyAndSecurityConsigneeAddressFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey = "safetyAndSecurityConsigneeAddress.error.required"
  private val lengthKey   = "safetyAndSecurityConsigneeAddress.error.length"
  private val invalidKey  = "safetyAndSecurityConsigneeAddress.error.invalid"
  private val maxLength   = 35
  private val countries   = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))

  val form = new SafetyAndSecurityConsigneeAddressFormProvider()(countries)

  ".AddressLine1" - {

    val fieldName = "AddressLine1"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq("1"))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, "1")
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength)
  }
  ".AddressLine2" - {

    val fieldName = "AddressLine2"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq("2"))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, "2")
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength)
  }

  ".AddressLine3" - {

    val fieldName = "AddressLine3"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq("3"))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, "3")
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength)
  }
}
