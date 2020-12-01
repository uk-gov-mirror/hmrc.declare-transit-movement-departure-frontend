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

package models.messages.safetyAndSecurity

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.{LanguageCodeEnglish, XMLWrites}

import scala.xml.NodeSeq

final case class SafetyAndSecurityConsignor(
  name: Option[String],
  streetAndNumber: Option[String],
  postCode: Option[String],
  city: Option[String],
  countryCode: Option[String],
  eori: Option[String]
)

object SafetyAndSecurityConsignor {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsignor] = (
    (__ \ "NamTRACORSEC041").read[String].optional,
    (__ \ "StrNumTRACORSEC043").read[String].optional,
    (__ \ "PosCodTRACORSEC042").read[String].optional,
    (__ \ "CitTRACORSEC038").read[String].optional,
    (__ \ "CouCodTRACORSEC039").read[String].optional,
    (__ \ "TINTRACORSEC044").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityConsignor] = XMLWrites[SafetyAndSecurityConsignor] {
    consignor =>
      <TRACORSEC037>
      {
        consignor.name.fold(NodeSeq.Empty) {
          name =>
            <NamTRACORSEC041>{name}</NamTRACORSEC041>
        } ++
        consignor.streetAndNumber.fold(NodeSeq.Empty) {
          streetAndNumber =>
            <StrNumTRACORSEC043>{streetAndNumber}</StrNumTRACORSEC043>
        } ++
        consignor.postCode.fold(NodeSeq.Empty) {
          postcode =>
            <PosCodTRACORSEC042>{postcode}</PosCodTRACORSEC042>
        } ++
        consignor.city.map {
          city =>
            <CitTRACORSEC038>{city}</CitTRACORSEC038>
        } ++
        consignor.countryCode.fold(NodeSeq.Empty) {
          countryCode =>
            <CouCodTRACORSEC039>{countryCode}</CouCodTRACORSEC039>
        }
      }
      <TRACORSEC037LNG>{LanguageCodeEnglish.code}</TRACORSEC037LNG>
      {
        consignor.eori.fold(NodeSeq.Empty) {
          eori =>
            <TINTRACORSEC044>{eori}</TINTRACORSEC044>
        }
      }
    </TRACORSEC037>
  }
}
