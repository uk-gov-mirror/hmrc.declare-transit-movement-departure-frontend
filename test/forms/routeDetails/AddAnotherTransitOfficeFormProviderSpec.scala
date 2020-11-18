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

package forms.routeDetails

import forms.behaviours.StringFieldBehaviours
import models.OfficeOfTransitList
import models.reference.OfficeOfTransit
import play.api.data.FormError

class AddAnotherTransitOfficeFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "addAnotherTransitOffice.error.required"
  val maxLength   = 8

  val officeOfTransitList = OfficeOfTransitList(Seq(OfficeOfTransit("id", "name"), OfficeOfTransit("officeId", "someName")))
  val form                = new AddAnotherTransitOfficeFormProvider()(officeOfTransitList)

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
