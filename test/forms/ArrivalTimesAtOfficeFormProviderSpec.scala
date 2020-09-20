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

package forms

import java.time.{LocalDateTime, ZoneOffset}

import forms.behaviours.DateTimeWithAMPMBehaviours
import forms.mappings.LocalDateTimeWithAMPM

class ArrivalTimesAtOfficeFormProviderSpec extends DateTimeWithAMPMBehaviours {

  val officeOfTransit = "office"
  val form = new ArrivalTimesAtOfficeFormProvider()(officeOfTransit)

  ".value" - {

    val validData = dateTimesBetween(
      min = LocalDateTime.of(2000, 1, 1, 12, 0),
      max = LocalDateTime.now(ZoneOffset.UTC)
    ).sample.value

    val localDateTimeWithAMPM = LocalDateTimeWithAMPM(validData, "am")

    behave like dateTimeField(form, "value", localDateTimeWithAMPM)

    behave like mandatoryDateField(form, "value", "arrivalTimesAtOffice.error.required.all", Seq(officeOfTransit))


  }
}
