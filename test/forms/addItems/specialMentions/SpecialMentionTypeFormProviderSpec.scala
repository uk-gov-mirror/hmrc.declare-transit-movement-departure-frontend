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

package forms.addItems.specialMentions

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.{Index, SpecialMentionList}
import models.reference.SpecialMention
import play.api.data.FormError

class SpecialMentionTypeFormProviderSpec extends StringFieldBehaviours with SpecBase {

  val requiredKey = "specialMentionType.error.required"
  val lengthKey   = "specialMentionType.error.length"
  val maxLength   = 100

  private val specialMentionList = SpecialMentionList(
    Seq(
      SpecialMention("10600", "Negotiable Bill of lading 'to order blank endorsed'"),
      SpecialMention("30400", "RET-EXP – Copy 3 to be returned")
    )
  )

  val form = new SpecialMentionTypeFormProvider()(specialMentionList, index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(index.display))
    )

    "not bind if special mention that does not exist in the list" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a special mention which is in the list" in {

      val boundForm = form.bind(Map("value" -> "10600"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
