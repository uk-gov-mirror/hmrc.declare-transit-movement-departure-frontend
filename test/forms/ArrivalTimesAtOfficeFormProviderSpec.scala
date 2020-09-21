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

import forms.behaviours.DateTimeWithAMPMBehaviours
import forms.mappings.LocalDateTimeWithAMPM
import play.api.data.FormError
import utils.Format

class ArrivalTimesAtOfficeFormProviderSpec extends DateTimeWithAMPMBehaviours {

  val officeOfTransit = "office"
  val form = new ArrivalTimesAtOfficeFormProvider()(officeOfTransit)
  val localDateTime = dateTime.withHour(1)
  val pastDateTime = localDateTime.minusDays(1)
  val futureDateTime = localDateTime.plusWeeks(2)
  val formattedPastDateTime: String = s"${Format.dateFormattedWithMonthName(pastDateTime)}"
  val formattedFutureDateTime: String = s"${Format.dateFormattedWithMonthName(futureDateTime)}"
  val formPastError = FormError("value", "arrivalTimesAtOffice.error.past.date", Seq(officeOfTransit, formattedPastDateTime))
  val formFutureError = FormError("value", "arrivalTimesAtOffice.error.future.date", Seq(officeOfTransit, formattedFutureDateTime))

  ".value" - {

    val localDateTimeWithAMPM = LocalDateTimeWithAMPM(localDateTime, "am")

    behave like dateTimeField(form, "value", localDateTimeWithAMPM)

    behave like mandatoryDateTimeField(form, "value", "arrivalTimesAtOffice.error.required.all", Seq(officeOfTransit))

    behave like dateTimeFieldWithMin(form, "value", localDateTime ,formPastError)

    behave like dateFieldWithMax(form, "value", futureDateTime ,formFutureError)

    behave like invalidDateField(form, "value", "arrivalTimesAtOffice.error.invalid.date" , Seq(officeOfTransit))

    behave like invalidHourField(form, "value", "arrivalTimesAtOffice.error.invalid.hour" , Seq(officeOfTransit))

    behave like invalidTimeField(form, "value", "arrivalTimesAtOffice.error.invalid.time" , Seq(officeOfTransit))

    behave like mandatoryDateField(form, "value", "arrivalTimesAtOffice.error.required.date" , Seq(officeOfTransit))

    behave like mandatoryTimeField(form, "value", "arrivalTimesAtOffice.error.required.time" , Seq(officeOfTransit))

    behave like mandatoryAMPMField(form, "value", "arrivalTimesAtOffice.amOrPm.error.required" , Seq(officeOfTransit))

  }
}
