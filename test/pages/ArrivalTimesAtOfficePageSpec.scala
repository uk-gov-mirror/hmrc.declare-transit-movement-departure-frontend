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

package pages

import java.time.{LocalDate, LocalDateTime}

import forms.mappings.LocalDateTimeWithAMPM
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ArrivalTimesAtOfficePageSpec extends PageBehaviours {

  "ArrivalTimesAtOfficePage" - {

    implicit lazy val arbitraryLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary {
        LocalDateTime.of(1900, 1, 1, 12, 0, 0)
    }

    implicit lazy val arbitraryDateTimeWithAMPM: Arbitrary[LocalDateTimeWithAMPM] = {
      Arbitrary {
        for {
          localDateTime <- arbitrary[LocalDateTime]
          name <- arbitrary[String]
        } yield LocalDateTimeWithAMPM(localDateTime, name)
      }
    }


    beRetrievable[LocalDateTimeWithAMPM](ArrivalTimesAtOfficePage)

    beSettable[LocalDateTimeWithAMPM](ArrivalTimesAtOfficePage)

    beRemovable[LocalDateTimeWithAMPM](ArrivalTimesAtOfficePage)
  }
}
