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

import java.time.{LocalDate, LocalDateTime, LocalTime}

import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseResult, ParseSuccess, PartialParseSuccess, XmlReader}
import com.lucidchart.open.xtract.XmlReader.{intReader, strictReadSeq}
import play.api.Logger
import utils.Format.{dateFormatter, dateTimeFormatterIE015, timeFormatter}

import scala.util.{Failure, Success, Try}
import scala.xml.NodeSeq

object XMLReads {

  case class LocalDateParseFailure(message: String) extends ParseError
  case class LocalTimeParseFailure(message: String) extends ParseError
  implicit val xmlDateReads: XmlReader[LocalDate] = {
    new XmlReader[LocalDate] {
      override def read(xml: NodeSeq): ParseResult[LocalDate] =
        Try(LocalDate.parse(xml.text, dateFormatter)) match {
          case Success(value) => ParseSuccess(value)
          case Failure(e)     => ParseFailure(LocalDateParseFailure(e.getMessage))
        }
    }
  }

  implicit val xmlTimeReads: XmlReader[LocalTime] = {
    new XmlReader[LocalTime] {
      override def read(xml: NodeSeq): ParseResult[LocalTime] =
        Try(LocalTime.parse(xml.text, timeFormatter)) match {
          case Success(value) => ParseSuccess(value)
          case Failure(e)     => ParseFailure(LocalTimeParseFailure(e.getMessage))
        }
    }
  }

  implicit val xmlDateTimeReads: XmlReader[LocalDateTime] = {
    new XmlReader[LocalDateTime] {
      override def read(xml: NodeSeq): ParseResult[LocalDateTime] =
        Try(LocalDateTime.parse(xml.text, dateTimeFormatterIE015)) match {
          case Success(value) => ParseSuccess(value)
          case Failure(e)     => ParseFailure(LocalTimeParseFailure(e.getMessage))
        }
    }
  }

  implicit val booleanFromIntReader: XmlReader[Boolean] = intReader.map(intValue => if (intValue == 1) true else false)

  implicit def strictReadOptionSeq[A](implicit reader: XmlReader[A]): XmlReader[Option[Seq[A]]] =
    XmlReader {
      xml =>
        strictReadSeq[A].read(xml) match {
          case ParseSuccess(x) if x.nonEmpty => ParseSuccess(x)
          case _                             => ParseFailure()
        }
    }.optional

  def readAs[T](message: NodeSeq)(implicit r: XmlReader[T]): Option[T] =
    XmlReader.of[T].read(message) match {
      case ParseSuccess(model) => Some(model)
      case PartialParseSuccess(_, errors) =>
        Logger.error(s"PartialParseSuccess: Failed with errors: $errors")
        None
      case ParseFailure(errors) =>
        Logger.error(s"ParseFailure: Failed with errors: $errors")
        None
    }
}
