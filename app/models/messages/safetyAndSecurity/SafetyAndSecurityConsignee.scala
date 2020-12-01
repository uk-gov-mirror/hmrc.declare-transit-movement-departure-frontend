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

final case class SafetyAndSecurityConsignee(
  name: Option[String],
  streetAndNumber: Option[String],
  postCode: Option[String],
  city: Option[String],
  countryCode: Option[String],
  eori: Option[String]
)

object SafetyAndSecurityConsignee {

  implicit val xmlReader: XmlReader[SafetyAndSecurityConsignee] = (
    (__ \ "NameTRACONSEC033").read[String].optional,
    (__ \ "StrNumTRACONSEC035").read[String].optional,
    (__ \ "PosCodTRACONSEC034").read[String].optional,
    (__ \ "CitTRACONSEC030").read[String].optional,
    (__ \ "CouCodTRACONSEC031").read[String].optional,
    (__ \ "TINTRACONSEC036").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityConsignee] = XMLWrites[SafetyAndSecurityConsignee] {
    consignee =>
      <TRACONSEC029>
        {
        consignee.name.fold(NodeSeq.Empty) {
          name =>
            <NameTRACONSEC033>{name}</NameTRACONSEC033>
        } ++
          consignee.streetAndNumber.fold(NodeSeq.Empty) {
            streetAndNumber =>
              <StrNumTRACONSEC035>{streetAndNumber}</StrNumTRACONSEC035>
          } ++
          consignee.postCode.fold(NodeSeq.Empty) {
            postcode =>
              <PosCodTRACONSEC034>{postcode}</PosCodTRACONSEC034>
          } ++
          consignee.city.map {
            city =>
              <CitTRACONSEC030>{city}</CitTRACONSEC030>
          } ++
          consignee.countryCode.fold(NodeSeq.Empty) {
            countryCode =>
              <CouCodTRACONSEC031>{countryCode}</CouCodTRACONSEC031>
          }
        }
        <TRACONSEC029LNG>{LanguageCodeEnglish.code}</TRACONSEC029LNG>
        {
          consignee.eori.fold(NodeSeq.Empty) {
            eori =>
              <TINTRACONSEC036>{eori}</TINTRACONSEC036>
          }
        }
      </TRACONSEC029>
  }
}
