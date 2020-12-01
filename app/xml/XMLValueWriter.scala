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

package xml

import java.time.LocalDate

import play.twirl.api.utils.StringEscapeUtils
import utils.Format.dateFormatter

trait XMLValueWriter[A] {
  def asXmlText(a: A): String
}

object XMLValueWriter {

  def apply[A](implicit ev: XMLValueWriter[A]): XMLValueWriter[A] = ev

  implicit class XMLValueWriterOps[A](val a: A) extends AnyVal {

    /*
        This uses the XMLValueWriter of the object to be serialized and that string representation
        as per the XML spec
     */
    def asXmlText(implicit ev: XMLValueWriter[A]): String =
      XMLValueWriter[String].asXmlText(ev.asXmlText(a))
  }

  implicit val stringXmlValueWriter: XMLValueWriter[String] = string => StringEscapeUtils.escapeXml11(string)

  implicit val intXmlValueWriter: XMLValueWriter[Int] = int => int.toString

  implicit val dateFormattedXmlValueWriter: XMLValueWriter[LocalDate] = date => date.format(dateFormatter)

}
