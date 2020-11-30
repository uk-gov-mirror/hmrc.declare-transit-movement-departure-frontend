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

package forms.behaviours

import org.scalacheck.Gen
import play.api.data.{Form, FormError}

trait StringFieldBehaviours extends FieldBehaviours {

  def fieldWithMaxLength(form: Form[_], fieldName: String, maxLength: Int, lengthError: FormError, withoutExtendedAscii: Boolean = false): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength, withoutExtendedAscii) -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMinLength(form: Form[_], fieldName: String, minLength: Int, lengthError: FormError, withoutExtendedAscii: Boolean = false): Unit =
    s"must not bind strings longer than $minLength characters" in {

      forAll(stringsLesserThan(minLength, withoutExtendedAscii) -> "shortString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMaxLength(
    form: Form[_],
    fieldName: String,
    maxLength: Int,
    lengthError: FormError,
    gen: Gen[String]
  ): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(gen -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors.headOption.value.key mustEqual lengthError.key
          result.errors.headOption.value.message mustEqual lengthError.message
          result.errors.headOption.value.args.toSeq mustEqual lengthError.args
      }
    }
}
