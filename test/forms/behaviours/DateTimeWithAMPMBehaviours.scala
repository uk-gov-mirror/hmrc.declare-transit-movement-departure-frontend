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

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

import models.LocalDateTimeWithAMPM
import org.scalacheck.Gen
import play.api.data.{Form, FormError}

class DateTimeWithAMPMBehaviours extends FieldBehaviours {

  val dateTime =  LocalDateTime.now(ZoneOffset.UTC)

  def dateTimeField(form: Form[_], key: String, validData: Gen[LocalDateTimeWithAMPM]): Unit = {

    "must bind valid data" in {

      forAll(validData -> "valid date") {
        date =>

          val data = Map(
            s"$key.day"   -> date.dateTime.getDayOfMonth.toString,
            s"$key.month" -> date.dateTime.getMonthValue.toString,
            s"$key.year"  -> date.dateTime.getYear.toString,
            s"$key.hour"  -> date.dateTime.getHour.toString,
            s"$key.minute"  -> date.dateTime.getMinute.toString,
            s"$key.amOrPm"  -> date.amOrPm

          )

          val result = form.bind(data)

          result.value.value mustEqual date.copy(dateTime = date.dateTime.withSecond(0).withNano(0))
      }
    }
  }

  def dateFieldWithMax(form: Form[_], key: String, max: LocalDateTime, formError: FormError): Unit = {

    s"must fail to bind a date greater than ${max.format(DateTimeFormatter.ISO_LOCAL_DATE)}" in {

      val generator: Gen[LocalDateTime] = dateTimesBetween(max.plusDays(1), max.plusYears(10))

      forAll(generator -> "invalid dates") {
        dateTime =>
          val dateTimeWithAmPm = LocalDateTimeWithAMPM(dateTime, "am")

          val data = Map(
            s"$key.day"   -> dateTimeWithAmPm.dateTime.getDayOfMonth.toString,
            s"$key.month" -> dateTimeWithAmPm.dateTime.getMonthValue.toString,
            s"$key.year"  -> dateTimeWithAmPm.dateTime.getYear.toString,
            s"$key.hour"  -> dateTimeWithAmPm.dateTime.getHour.toString,
            s"$key.minute"  -> dateTimeWithAmPm.dateTime.getMinute.toString,
            s"$key.amOrPm"  -> dateTimeWithAmPm.amOrPm
          )

          val result = form.bind(data)

          result.errors must contain only formError
      }
    }
  }

  def dateTimeFieldWithMin(form: Form[_], key: String, min: LocalDateTime, formError: FormError): Unit = {

    s"must fail to bind a date earlier than ${min.format(DateTimeFormatter.ISO_LOCAL_DATE)}" in {

      val generator = dateTimesBetween(min.minusYears(10), min.minusDays(1))

      forAll(generator -> "invalid dates") {
        dateTime =>
          val dateTimeWithAmPm = LocalDateTimeWithAMPM(dateTime, "am")
          val data = Map(
            s"$key.day"   -> dateTimeWithAmPm.dateTime.getDayOfMonth.toString,
            s"$key.month" -> dateTimeWithAmPm.dateTime.getMonthValue.toString,
            s"$key.year"  -> dateTimeWithAmPm.dateTime.getYear.toString,
            s"$key.hour"  -> dateTimeWithAmPm.dateTime.getHour.toString,
            s"$key.minute"  -> dateTimeWithAmPm.dateTime.getMinute.toString,
            s"$key.amOrPm"  -> dateTimeWithAmPm.amOrPm
          )

          val result = form.bind(data)

          result.errors must contain only formError
      }
    }
  }

  def invalidDateField(form: Form[_], key: String, invalidDateKey: String, errorArgs: Seq[String] = Seq.empty): Unit = {

    "must fail to bind invalid date data" in {

          val data = Map(
            s"$key.day"   -> dateTime.getDayOfMonth.toString,
            s"$key.month" -> s"${dateTime.getMonthValue.toString}111",
            s"$key.year"  -> dateTime.getYear.toString,
            s"$key.hour"  -> dateTime.getHour.toString,
            s"$key.minute"  -> dateTime.getMinute.toString,
            s"$key.amOrPm"  -> "am"

          )

          val result = form.bind(data)

          result.errors must contain only FormError(key, invalidDateKey, errorArgs)

    }
  }

  def invalidHourField(form: Form[_], key: String, invalidHourKey: String, errorArgs: Seq[String] = Seq.empty): Unit = {

      val invalidHour: Int = dateTime.withHour(14).getHour

    "must fail to bind invalid hour" in {

          val data = Map(
            s"$key.day"   -> dateTime.getDayOfMonth.toString,
            s"$key.month" -> dateTime.getMonthValue.toString,
            s"$key.year"  -> dateTime.getYear.toString,
            s"$key.hour"  -> invalidHour.toString,
            s"$key.minute"  -> dateTime.getMinute.toString,
            s"$key.amOrPm"  -> "am"

          )

          val result = form.bind(data)

          result.errors must contain only FormError(key, invalidHourKey, errorArgs)

    }
  }

  def invalidTimeField(form: Form[_], key: String, invalidTimeKey: String, errorArgs: Seq[String] = Seq.empty): Unit = {

      val invalidHour: Int = dateTime.withHour(0).getHour

    "must fail to bind invalid time data" in {

          val data = Map(
            s"$key.day"   -> dateTime.getDayOfMonth.toString,
            s"$key.month" -> dateTime.getMonthValue.toString,
            s"$key.year"  -> dateTime.getYear.toString,
            s"$key.hour"  -> invalidHour.toString,
            s"$key.minute"  -> dateTime.getMinute.toString,
            s"$key.amOrPm"  -> "am"
          )

          val result = form.bind(data)

          result.errors must contain only FormError(key, invalidTimeKey, errorArgs)

    }
  }

  def mandatoryDateField(form: Form[_], key: String, requiredDateKey: String, errorArgs: Seq[String] = Seq.empty): Unit = {

    "must fail to bind missing date data" in {


          val data = Map(
            s"$key.day"   -> "",
            s"$key.month" -> "",
            s"$key.year"  -> "",
            s"$key.hour"  -> "11",
            s"$key.minute"  -> "40",
            s"$key.amOrPm"  -> "pm"
          )

          val result = form.bind(data)


      result.errors must contain only FormError(key, requiredDateKey, List("day", "month", "year") ++ errorArgs)
    }
  }

  def mandatoryTimeField(form: Form[_], key: String, requiredTimeKey: String, errorArgs: Seq[String] = Seq.empty): Unit = {

    "must fail to bind missing time data" in {

          val data = Map(
            s"$key.day"   -> dateTime.getDayOfMonth.toString,
            s"$key.month" -> dateTime.getMonthValue.toString,
            s"$key.year"  -> dateTime.getYear.toString,
            s"$key.hour"  -> "",
            s"$key.minute"  -> "",
            s"$key.amOrPm"  -> "am"

          )

          val result = form.bind(data)

      result.errors must contain only FormError(key, requiredTimeKey, List("hour", "minute") ++ errorArgs)
    }
  }

  def mandatoryAMPMField(form: Form[_], key: String, requiredTimeKey: String, errorArgs: Seq[String] = Seq.empty): Unit = {

    "must fail to bind missing am/pm data" in {

          val data = Map(
            s"$key.day"   -> dateTime.getDayOfMonth.toString,
            s"$key.month" -> dateTime.getMonthValue.toString,
            s"$key.year"  -> dateTime.getYear.toString,
            s"$key.hour"  -> dateTime.getHour.toString,
            s"$key.minute"  -> dateTime.getMinute.toString,
            s"$key.amOrPm"  -> ""
          )

          val result = form.bind(data)

      result.errors must contain only FormError(key, requiredTimeKey, errorArgs)
    }
  }
}
