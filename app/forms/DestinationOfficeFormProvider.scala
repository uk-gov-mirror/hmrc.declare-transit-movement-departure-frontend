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

import javax.inject.Inject
import forms.mappings.Mappings
import models.CustomsOfficeList
import models.reference.CustomsOffice
import play.api.data.Form

class DestinationOfficeFormProvider @Inject() extends Mappings {

  def apply(customsOffices: CustomsOfficeList, countryName: String): Form[CustomsOffice] =
    Form(
      "value" -> text("destinationOffice.error.required", Seq(countryName))
        .verifying("destinationOffice.error.required", value => customsOffices.customsOffices.exists(_.id == value))
        .transform[CustomsOffice](value => customsOffices.getCustomsOffice(value).get, _.id)
    )
}
