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

package forms.traderDetails

import forms.mappings.Mappings
import javax.inject.Inject
import models.PrincipalAddress
import models.PrincipalAddress.Constants.{numberAndStreetLength, postcodeLength, townLength}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class PrincipalAddressFormProvider @Inject() extends Mappings {

  val validPostcodeCharactersRegex: String = "^[a-zA-Z\\s*0-9]*$"
  val postCodeRegex: String                = "^[a-zA-Z]{1,2}([0-9]{1,2}|[0-9][a-zA-Z])\\s*[0-9][a-zA-Z]{2}$"

  def apply(principalName: String): Form[PrincipalAddress] = Form(
    mapping(
      "numberAndStreet" -> text("principalAddress.error.numberAndStreet.required", Seq(principalName))
        .verifying(maxLength(numberAndStreetLength, "principalAddress.error.numberAndStreet.length")),
      "town" -> text("principalAddress.error.town.required", Seq(principalName))
        .verifying(maxLength(townLength, "principalAddress.error.town.length")),
      "postcode" -> text("principalAddress.error.postcode.required", Seq(principalName))
        .verifying(StopOnFirstFail[String](
          maxLength(postcodeLength, "principalAddress.error.postcode.length"),
          regexp(validPostcodeCharactersRegex, "principalAddress.error.postcode.invalidCharacters", principalName),
          regexp(postCodeRegex, "principalAddress.error.postcode.invalidFormat", principalName),
        ))
    )(PrincipalAddress.apply)(PrincipalAddress.unapply)
  )
}
