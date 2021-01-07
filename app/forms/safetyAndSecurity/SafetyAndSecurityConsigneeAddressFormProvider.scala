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

import forms.Constants.addressRegex
import forms.mappings.Mappings
import javax.inject.Inject
import models.reference.Country
import models.{ConsigneeAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class SafetyAndSecurityConsigneeAddressFormProvider @Inject() extends Mappings {

  val maxLength = 35

  def apply(countryList: CountryList): Form[ConsigneeAddress] = Form(
    mapping(
      "AddressLine1" -> text("safetyAndSecurityConsigneeAddress.error.required", "1")
        .verifying(StopOnFirstFail[String](maxLength(maxLength, "safetyAndSecurityConsigneeAddress.error.length", "1"),
                                           regexp(addressRegex, "safetyAndSecurityConsigneeAddress.error.invalid", "1"))),
      "AddressLine2" -> text("safetyAndSecurityConsigneeAddress.error.required", "2")
        .verifying(StopOnFirstFail[String](maxLength(maxLength, "safetyAndSecurityConsigneeAddress.error.length", "2"),
                                           regexp(addressRegex, "safetyAndSecurityConsigneeAddress.error.invalid", "2"))),
      "AddressLine3" -> text("safetyAndSecurityConsigneeAddress.error.required", "3")
        .verifying(StopOnFirstFail[String](maxLength(maxLength, "safetyAndSecurityConsigneeAddress.error.length", "3"),
                                           regexp(addressRegex, "safetyAndSecurityConsigneeAddress.error.invalid", "3"))),
      "country" -> text("safetyAndSecurityConsignorEori.error.country.required")
        .verifying("safetyAndSecurityConsigneeAddress.AddressLine4.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsigneeAddress.apply)(ConsigneeAddress.unapply)
  )
}
