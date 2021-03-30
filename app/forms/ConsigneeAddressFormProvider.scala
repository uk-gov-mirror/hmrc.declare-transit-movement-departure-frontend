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

import forms.mappings.Mappings
import javax.inject.Inject
import models.domain.StringFieldRegex.stringFieldRegex
import models.reference.Country
import models.{ConsigneeAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class ConsigneeAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList): Form[ConsigneeAddress] = Form(
    mapping(
      "AddressLine1" -> text("consigneeAddress.error.AddressLine1.required")
        .verifying(StopOnFirstFail[String](maxLength(35, "consigneeAddress.error.AddressLine1.length"),
                                           regexp(stringFieldRegex, "consigneeAddress.error.AddressLine1.invalid"))),
      "AddressLine2" -> text("consigneeAddress.error.AddressLine2.required")
        .verifying(StopOnFirstFail[String](maxLength(35, "consigneeAddress.error.AddressLine2.length"),
                                           regexp(stringFieldRegex, "consigneeAddress.error.AddressLine2.invalid"))),
      "AddressLine3" -> text("consigneeAddress.error.AddressLine3.required")
        .verifying(StopOnFirstFail[String](maxLength(35, "consigneeAddress.error.AddressLine3.length"),
                                           regexp(stringFieldRegex, "consigneeAddress.error.AddressLine3.invalid"))),
      "country" -> text("consigneeAddress.error.country.required")
        .verifying("eventCountry.error.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsigneeAddress.apply)(ConsigneeAddress.unapply)
  )
}
