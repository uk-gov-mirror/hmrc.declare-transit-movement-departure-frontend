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
import models.CustomsOfficeList
import models.reference.{CountryCode, CustomsOffice}
import play.api.data.FormError

class DestinationOfficeFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey                       = "destinationOffice.error.required"
  private val lengthKey                         = "destinationOffice.error.length"
  private val maxLength                         = 20
  private val countryName                       = "United Kingdom"
  private val customsOffice1: CustomsOffice     = CustomsOffice("officeId", "someName", CountryCode("GB"), Seq.empty, None)
  private val customsOffice2: CustomsOffice     = CustomsOffice("id", "name", CountryCode("GB"), Seq.empty, None)
  private val customsOffices: CustomsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))
  private val form                              = new DestinationOfficeFormProvider()(customsOffices, countryName)

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
      requiredError = FormError(fieldName, requiredKey, Seq(countryName))
    )

    "not bind if customs office id does not exist in the customs office list" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a customs office id which is in the list" in {

      val boundForm = form.bind(Map("value" -> "officeId"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
