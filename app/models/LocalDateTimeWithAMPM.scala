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

package models

import java.time.{LocalDateTime, LocalTime}

import play.api.libs.json.{Json, _}
import utils.Format.timeFormatterFromAMPM

case class LocalDateTimeWithAMPM(dateTime: LocalDateTime, amOrPm: String)

object LocalDateTimeWithAMPM {

  implicit lazy val writes: OWrites[LocalDateTimeWithAMPM] = {
    x =>
      val formatTimeWithAmPm = x.dateTime.toLocalTime + x.amOrPm.toUpperCase
      val parseToTime        = LocalTime.parse(formatTimeWithAmPm, timeFormatterFromAMPM)
      val parseToDateTime    = x.dateTime.toLocalDate.atTime(parseToTime)

      Json.obj(
        "dateTime" -> parseToDateTime,
        "amOrPm"   -> x.amOrPm
      )
  }

  implicit val reads: Reads[LocalDateTimeWithAMPM] = {

    import play.api.libs.functional.syntax._

    import java.time.format.DateTimeFormatter

    ((__ \ "dateTime").read[LocalDateTime] and
      (__ \ "amOrPm").read[String])(LocalDateTimeWithAMPM.apply _)
  }
}
