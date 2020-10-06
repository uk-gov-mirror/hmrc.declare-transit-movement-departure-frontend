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

package models.messages.trader

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.messages.escapeXml
import models.{LanguageCodeEnglish, XMLWrites}

import scala.xml._

sealed trait TraderConsignee

object TraderConsignee {

  implicit lazy val xmlReader: XmlReader[TraderConsignee] =
    TraderConsigneeWithEori.xmlReader.or(TraderConsigneeWithoutEori.xmlReader)
}

final case class TraderConsigneeWithEori(
  eori: String,
  name: Option[String],
  streetAndNumber: Option[String],
  postCode: Option[String],
  city: Option[String],
  countryCode: Option[String]
) extends TraderConsignee

object TraderConsigneeWithEori {

  implicit val xmlReader: XmlReader[TraderConsigneeWithEori] = (
    (__ \ "TINCE159").read[String],
    (__ \ "NamCE17").read[String].optional,
    (__ \ "StrAndNumCE122").read[String].optional,
    (__ \ "PosCodCE123").read[String].optional,
    (__ \ "CitCE124").read[String].optional,
    (__ \ "CouCE125").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsigneeWithEori] = XMLWrites[TraderConsigneeWithEori] {
    trader =>
      <TRACONCE1>
        {
        trader.name.fold(NodeSeq.Empty) {
          name =>
            <NamCE17>{name}</NamCE17>
        } ++
          trader.streetAndNumber.fold(NodeSeq.Empty) {
            streetAndNumber =>
              <StrAndNumCE122>{streetAndNumber}</StrAndNumCE122>
          } ++
          trader.postCode.fold(NodeSeq.Empty) {
            postCode =>
              <PosCodCE123>{postCode}</PosCodCE123>
          } ++
          trader.city.fold(NodeSeq.Empty) {
            city =>
              <CitCE124>{city}</CitCE124>
          } ++
          trader.countryCode.fold(NodeSeq.Empty) {
            countryCode =>
              <CouCE125>{countryCode}</CouCE125>
          }
        }
        <NADLNGCE>{LanguageCodeEnglish.code}</NADLNGCE>
        <TINCE159>{trader.eori}</TINCE159>
      </TRACONCE1>
  }
}

final case class TraderConsigneeWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends TraderConsignee

object TraderConsigneeWithoutEori {

  implicit val xmlReader: XmlReader[TraderConsigneeWithoutEori] = (
    (__ \ "NamCO17").read[String],
    (__ \ "StrAndNumCE122").read[String],
    (__ \ "PosCodCE123").read[String],
    (__ \ "CitCE124").read[String],
    (__ \ "CouCE125").read[String]
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsigneeWithoutEori] = XMLWrites[TraderConsigneeWithoutEori] {
    trader =>
      <TRACONCE1>
        <NamCE17>{escapeXml(trader.name)}</NamCE17>
        <StrAndNumCE122>{trader.streetAndNumber}</StrAndNumCE122>
        <PosCodCE123>{trader.postCode}</PosCodCE123>
        <CitCE124>{trader.city}</CitCE124>
        <CouCE125>{trader.countryCode}</CouCE125>
        <NADLNGCE>{LanguageCodeEnglish.code}</NADLNGCE>
      </TRACONCE1>
  }
}
