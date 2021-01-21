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

import base.{GeneratorSpec, SpecBase}
import org.scalacheck.Gen

class LocalDateTimeWithAMPMSpec extends SpecBase with GeneratorSpec {

  "LocalDateTimeWithAMPM" - {

    "must format 12 hour time into 24 hour time" in {

      forAll(arb[LocalDateTimeWithAMPM], Gen.chooseNum(1, 12)) {
        (localDateTimeWithAMPM, hour) =>
          val updatedTime         = localDateTimeWithAMPM.localDateTime.withHour(hour)
          val localDateTime12Hour = localDateTimeWithAMPM.copy(localDateTime = updatedTime)

          val convertTo24Hour = localDateTimeWithAMPM.amOrPm match {
            case "PM" if hour == 12 => hour
            case "PM"               => hour + 12
            case "AM" if hour == 12 => 0
            case _                  => hour
          }

          val expectedResult = localDateTimeWithAMPM.copy(localDateTime = localDateTimeWithAMPM.localDateTime.withHour(convertTo24Hour))

          localDateTime12Hour.formatTo24hourTime mustBe expectedResult
      }
    }
  }
}
