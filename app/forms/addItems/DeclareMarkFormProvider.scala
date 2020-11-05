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

package forms.addItems

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class DeclareMarkFormProvider @Inject() extends Mappings {

  val regex: String = "^[a-zA-Z0-9]*$"
  val maxLength     = 42

  def apply(totalPackages: Option[Int]): Form[String] =
    Form(
      "value" -> text("declareMark.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLength, "declareMark.error.length"),
            regexp(regex, "declareMark.error.format"),
            emptyNumberOfPackages(totalPackages)
          )
        )
    )

  private def emptyNumberOfPackages(totalPackages: Option[Int]): Constraint[String] =
    Constraint {
      value =>
        totalPackages match {
          case Some(0) if value == "0" => Invalid("declareMark.error.emptyNumberOfPackages")
          case _                       => Valid
        }
    }

}
