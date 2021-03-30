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

import forms.mappings.Mappings
import javax.inject.Inject
import models.domain.StringFieldRegex.stringFieldRegex
import models.reference.Country
import models.{ConsignorAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class SafetyAndSecurityConsignorAddressFormProvider @Inject() extends Mappings {

  val maxLength = 35

  def apply(countryList: CountryList): Form[ConsignorAddress] = Form(
    mapping(
      "AddressLine1" -> text("safetyAndSecurityConsignorAddress.error.required", "1")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLength, "safetyAndSecurityConsignorAddress.error.length", "1"),
            regexp(stringFieldRegex, "safetyAndSecurityConsignorAddress.error.invalid")
          )),
      "AddressLine2" -> text("safetyAndSecurityConsignorAddress.error.required", "2")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLength, "safetyAndSecurityConsignorAddress.error.length", "2"),
            regexp(stringFieldRegex, "safetyAndSecurityConsignorAddress.error.invalid")
          )),
      "AddressLine3" -> text("safetyAndSecurityConsignorAddress.error.required", "3")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLength, "safetyAndSecurityConsignorAddress.error.length", "3"),
            regexp(stringFieldRegex, "safetyAndSecurityConsignorAddress.error.invalid")
          )),
      "country" -> text("safetyAndSecurityConsignorEori.error.country.required")
        .verifying("safetyAndSecurityConsignorEori.error.country.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsignorAddress.apply)(ConsignorAddress.unapply)
  )
}
