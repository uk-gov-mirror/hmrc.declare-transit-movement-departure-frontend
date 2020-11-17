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

package forms.goodsSummary

import forms.mappings.Mappings
import javax.inject.Inject
import models.Index
import models.domain.SealDomain
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class SealIdDetailsFormProvider @Inject() extends Mappings {

  val maxSealsNumberLength = 20
  val sealNumberRegex      = "^[a-zA-Z0-9]*$"

  def apply(index: Index, seals: Seq[SealDomain] = Seq.empty[SealDomain]): Form[String] =
    Form(
      "value" -> text("sealIdDetails.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxSealsNumberLength, "sealIdDetails.error.length"),
            regexp(sealNumberRegex, "sealIdDetails.error.invalidCharacters"),
            doesNotExistIn(seals, index, "sealIdentity.error.duplicate")
          ))
    )
}
