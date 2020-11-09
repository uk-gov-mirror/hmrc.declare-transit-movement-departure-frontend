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

package models

import models.reference.Country
import play.api.libs.json._

case class ForeignAddress(
  line1: String,
  line2: String,
  line3: Option[String],
  country: Country
)

object ForeignAddress {

  object Constants {
    val lineLength = 35
    val cityLength              = 35
    val postcodeLength          = 9

    object Fields {
      val line1 = "line1"
      val line2 = "line2"
      val line3 = "line2"
      val country = "country"
    }
  }

  implicit val format: OFormat[ForeignAddress] = Json.format[ForeignAddress]
}
