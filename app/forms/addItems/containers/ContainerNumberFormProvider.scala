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

package forms.addItems.containers

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class ContainerNumberFormProvider @Inject() extends Mappings {

  //todo: move these to models
  private lazy val containerNumberMaxLength = 17
  private lazy val containerNumberRegex     = s"^[a-zA-Z0-9&'@\\/.\\-%?<>]{1,$containerNumberMaxLength}$$"

  def apply(): Form[String] =
    Form(
      "value" -> text("containerNumber.error.required")
        .verifying(
          StopOnFirstFail(
            maxLength(containerNumberMaxLength, "containerNumber.error.length", args = Seq(containerNumberMaxLength)),
            regexp(containerNumberRegex, "containerNumber.error.invalid")
          )
        )
    )
}
