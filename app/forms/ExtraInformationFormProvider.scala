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

import javax.inject.Inject
import forms.mappings.Mappings
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class ExtraInformationFormProvider @Inject() extends Mappings {

  val maxLength = 26

  def apply(): Form[String] =
    Form(
      "value" -> text("extraInformation.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLength, "extraInformation.error.length"),
            regexp(stringFieldRegex, "extraInformation.error.invalid")
          ))
    )
}
