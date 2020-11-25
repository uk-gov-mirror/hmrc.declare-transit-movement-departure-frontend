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

import forms.mappings.Mappings
import forms.Constants.addressRegex
import javax.inject.Inject
import models.reference.Country
import models.{ConsigneeAddress, CountryList, Index}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class SecurityConsigneeAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, consigneeName: String): Form[ConsigneeAddress] = Form(
    mapping(
      "AddressLine1" -> text("securityConsigneeAddress.error.AddressLine1.required", Seq(consigneeName))
        .verifying(maxLength(100, "securityConsigneeAddress.error.length"))
        .verifying(StopOnFirstFail[String](maxLength(35, "securityConsigneeAddress.error.AddressLine1.length"),
                                           regexp(addressRegex, "securityConsigneeAddress.error.line1.invalid"))),
      "AddressLine2" -> text("securityConsigneeAddress.error.AddressLine2.required")
        .verifying(StopOnFirstFail[String](maxLength(35, "securityConsigneeAddress.error.AddressLine2.length"),
                                           regexp(addressRegex, "securityConsigneeAddress.error.line2.invalid"))),
      "AddressLine3" -> text("securityConsigneeAddress.error.AddressLine3.required")
        .verifying(StopOnFirstFail[String](maxLength(35, "securityConsigneeAddress.error.AddressLine3.length"),
                                           regexp(addressRegex, "securityConsigneeAddress.error.line3.invalid"))),
      "country" -> text("securityConsigneeAddress.error.country.required")
        .verifying("eventCountry.error.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsigneeAddress.apply)(ConsigneeAddress.unapply)
  )
}
