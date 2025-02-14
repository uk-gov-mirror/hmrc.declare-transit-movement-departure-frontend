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

package matchers

import org.scalatest.matchers.MatchResult
import org.scalatest.matchers.Matcher
import play.api.libs.json._

trait JsonMatchers {

  class JsonContains(json: JsObject) extends Matcher[JsObject] {

    def apply(left: JsObject): MatchResult = {

      val mismatches = json.keys.filter(key => (left \ key) != (json \ key))

      MatchResult(
        mismatches.isEmpty,
        s"""$left did not match for key(s) ${mismatches.mkString(", ")}""",
        s"""$left matched for key(s) ${json.keys.mkString(", ")}"""
      )
    }
  }

  def containJson(expectedJson: JsObject) = new JsonContains(expectedJson)
}

object JsonMatchers extends JsonMatchers
