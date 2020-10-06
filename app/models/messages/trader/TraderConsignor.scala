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

sealed trait TraderConsignor

object TraderConsignor {

  implicit lazy val xmlReader: XmlReader[TraderConsignor] =
    TraderConsignorWithEori.xmlReader.or(TraderConsignorWithoutEori.xmlReader)
}

final case class TraderConsignorWithEori(
  eori: String,
  name: Option[String],
  streetAndNumber: Option[String],
  postCode: Option[String],
  city: Option[String],
  countryCode: Option[String]
) extends TraderConsignor

object TraderConsignorWithEori {

  implicit val xmlReader: XmlReader[TraderConsignorWithEori] = (
    (__ \ "TINCO159").read[String],
    (__ \ "NamCO17").read[String].optional,
    (__ \ "StrAndNumCO122").read[String].optional,
    (__ \ "PosCodCO123").read[String].optional,
    (__ \ "CitCO124").read[String].optional,
    (__ \ "CouCO125").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsignorWithEori] = XMLWrites[TraderConsignorWithEori] {
    trader =>
      <TRACONCO1>
        {
        trader.name.fold(NodeSeq.Empty) {
          name =>
            <NamCO17>{name}</NamCO17>
        } ++
          trader.streetAndNumber.fold(NodeSeq.Empty) {
            streetAndNumber =>
              <StrAndNumCO122>{streetAndNumber}</StrAndNumCO122>
          } ++
          trader.postCode.fold(NodeSeq.Empty) {
            postCode =>
              <PosCodCO123>{postCode}</PosCodCO123>
          } ++
          trader.city.fold(NodeSeq.Empty) {
            city =>
              <CitCO124>{city}</CitCO124>
          } ++
          trader.countryCode.fold(NodeSeq.Empty) {
            countryCode =>
              <CouCO125>{countryCode}</CouCO125>
          }
        }
        <NADLNGCO>{LanguageCodeEnglish.code}</NADLNGCO>
        <TINCO159>{trader.eori}</TINCO159>
      </TRACONCO1>
  }
}

final case class TraderConsignorWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends TraderConsignor

object TraderConsignorWithoutEori {

  implicit val xmlReader: XmlReader[TraderConsignorWithoutEori] = (
    (__ \ "NamCO17").read[String],
    (__ \ "StrAndNumCO122").read[String],
    (__ \ "PosCodCO123").read[String],
    (__ \ "CitCO124").read[String],
    (__ \ "CouCO125").read[String]
  ).mapN(apply)

  implicit def writes: XMLWrites[TraderConsignorWithoutEori] = XMLWrites[TraderConsignorWithoutEori] {
    trader =>
      <TRACONCO1>
        <NamCO17>{escapeXml(trader.name)}</NamCO17>
        <StrAndNumCO122>{trader.streetAndNumber}</StrAndNumCO122>
        <PosCodCO123>{trader.postCode}</PosCodCO123>
        <CitCO124>{trader.city}</CitCO124>
        <CouCO125>{trader.countryCode}</CouCO125>
        <NADLNGCO>{LanguageCodeEnglish.code}</NADLNGCO>
      </TRACONCO1>
  }
}
