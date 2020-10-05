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

package forms.guaranteeDetails

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class GuaranteeReferenceFormProvider @Inject() extends Mappings {


  def apply(grnExactLength:Int): Form[String] = {

   val guaranteeReferenceRegex: String = s"^[a-zA-Z0-9]{$grnExactLength}$$"

    Form(
    "value" -> text("guaranteeReference.error.required")
      .verifying(
    StopOnFirstFail[String](
    exactLength(grnExactLength, "guaranteeReference.error.length2"),
            regexp(guaranteeReferenceRegex, "guaranteeReference.error.invalid")
          )))
  }
}