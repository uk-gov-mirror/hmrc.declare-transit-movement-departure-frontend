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

package forms.addItems.traderSecurityDetails

import forms.Constants.consigneeNameMaxLength
import forms.mappings.Mappings
import models.Index
import models.domain.StringFieldRegex.consignorNameRegex
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

import javax.inject.Inject

class SecurityConsigneeNameFormProvider @Inject() extends Mappings {

  def apply(index: Index): Form[String] =
    Form(
      "value" -> text("securityConsigneeName.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(consigneeNameMaxLength, "securityConsigneeName.error.length"),
            regexp(consignorNameRegex, "securityConsigneeName.error.invalid")
          ))
    )
}
