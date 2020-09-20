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

package forms.mappings

import java.time.LocalDateTime
import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.{Failure, Success, Try}

private[mappings] class LocalDateTimeFormatter(
                                            invalidKey: String,
                                            allRequiredKey: String,
                                            timeRequiredKey: String,
                                            dateRequiredKey: String,
                                            amOrPmRequired : String,
                                            args: Seq[String] = Seq.empty
                                          ) extends Formatter[LocalDateTimeWithAMPM] with Formatters {

  private val fieldTimeKeys = List("hour", "minute", "amOrPm")
  private val fieldDateKeys = List("day", "month", "year")

  private def toDateTime(key: String, day: Int, month: Int, year: Int, hour: Int, minute: Int, amOrPm: String): Either[Seq[FormError], LocalDateTimeWithAMPM] =
    Try(LocalDateTime.of(year, month, day, hour, minute)) match {
      case Success(dateTime) =>
        Right(LocalDateTimeWithAMPM(dateTime, amOrPm))
      case Failure(_) =>
        Left(Seq(FormError(key, invalidKey, args)))
    }

  private def formatDateTime(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDateTimeWithAMPM] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )
    val string = stringFormatter (
      errorKey = amOrPmRequired,
      args
    )

    for {
      day   <- int.bind(s"$key.day", data).right
      month <- int.bind(s"$key.month", data).right
      year  <- int.bind(s"$key.year", data).right
      hour  <- int.bind(s"$key.hour", data).right
      minute  <- int.bind(s"$key.minute", data).right
      amOrPm  <- string.bind(s"$key.amOrPm", data).right
      dateTime  <- toDateTime(key, day, month, year, hour, minute, amOrPm).right
    } yield dateTime
  }

  //noinspection ScalaStyle
  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDateTimeWithAMPM] = {

    val fields1: Map[String, Option[String]] = fieldTimeKeys.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty)
    }.toMap

    lazy val missingTimeFields = fields1
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList

    val fields2: Map[String, Option[String]] = fieldDateKeys.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty)
    }.toMap

    lazy val missingDateFields: Seq[String] = fields2
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList

    (missingDateFields.nonEmpty, missingTimeFields.nonEmpty) match {
      case (false, false) =>
        formatDateTime(key, data).left.map {
          _.map(_.copy(key = key, args = args))
        }
      case (true, false)  =>
        Left(List(FormError(key, dateRequiredKey, missingDateFields ++ args)))
      case (false, true) =>
        if(missingTimeFields.contains(s"$key.amOrPm")) {
          Left(List(FormError(key, amOrPmRequired, missingTimeFields ++ args)))
        } else {
          Left(List(FormError(key, timeRequiredKey, missingTimeFields ++ args)))
         }
      case _ =>
        Left(List(FormError(key, allRequiredKey, args)))
    }
  }

  override def unbind(key: String, value: LocalDateTimeWithAMPM): Map[String, String] =
    Map(
      s"$key.day" -> value.dateTime.getDayOfMonth.toString,
      s"$key.month" -> value.dateTime.getMonthValue.toString,
      s"$key.year" -> value.dateTime.getMonthValue.toString,
      s"$key.hour" -> value.dateTime.getHour.toString,
      s"$key.minute" -> value.dateTime.getMinute.toString,
      s"$key.amOrPm" -> value.amOrPm
    )
}
