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

final case class SafetyAndSecurityCarrier(
  name: Option[String],
  streetAndNumber: Option[String],
  postCode: Option[String],
  city: Option[String],
  countryCode: Option[String],
  eori: Option[String]
)

object SafetyAndSecurityCarrier {

  implicit val xmlReader: XmlReader[SafetyAndSecurityCarrier] = (
    (__ \ "NamCARTRA121").read[String].optional,
    (__ \ "StrAndNumCARTRA254").read[String].optional,
    (__ \ "PosCodCARTRA121").read[String].optional,
    (__ \ "CitCARTRA789").read[String].optional,
    (__ \ "CouCodCARTRA587").read[String].optional,
    (__ \ "TINCARTRA254").read[String].optional
  ).mapN(apply)

  implicit def writes: XMLWrites[SafetyAndSecurityCarrier] = XMLWrites[SafetyAndSecurityCarrier] {
    carrier =>
      <CARTRA100>
        {
        carrier.name.fold(NodeSeq.Empty) {
          name =>
            <NamCARTRA121>{name}</NamCARTRA121>
        } ++
          carrier.streetAndNumber.fold(NodeSeq.Empty) {
            streetAndNumber =>
              <StrAndNumCARTRA254>{streetAndNumber}</StrAndNumCARTRA254>
          } ++
          carrier.postCode.fold(NodeSeq.Empty) {
            postcode =>
              <PosCodCARTRA121>{postcode}</PosCodCARTRA121>
          } ++
          carrier.city.map {
            city =>
              <CitCARTRA789>{city}</CitCARTRA789>
          } ++
          carrier.countryCode.fold(NodeSeq.Empty) {
            countryCode =>
              <CouCodCARTRA587>{countryCode}</CouCodCARTRA587>
          }
        }
        <NADCARTRA121>{LanguageCodeEnglish.code}</NADCARTRA121>
        {
          carrier.eori.fold(NodeSeq.Empty) {
            eori =>
              <TINCARTRA254>{eori}</TINCARTRA254>
          }
        }
      </CARTRA100>
  }
}
