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
import play.api.data.Form
import play.api.data.Forms._
import models.{CountryList, PrincipalAddress}
import models.PrincipalAddress.Constants.{numberAndStreetLength, postcodeLength, townLength}
import models.reference.Country

class PrincipalAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[PrincipalAddress] = Form(
    mapping(
      "numberAndStreet" -> text("principalAddress.error.numberAndStreet.required")
        .verifying(maxLength(numberAndStreetLength, "principalAddress.error.numberAndStreet.length")),
      "town" -> text("principalAddress.town.required")
        .verifying(maxLength(townLength, "principalAddress.error.town.length")),
      "postcode" -> text("principalAddress.postcode.required")
        .verifying(maxLength(postcodeLength, "principalAddress.error.postcode.length")),
      "country" -> text("eventCountry.error.required"))
       (PrincipalAddress.apply)(PrincipalAddress.unapply)
  )
}
