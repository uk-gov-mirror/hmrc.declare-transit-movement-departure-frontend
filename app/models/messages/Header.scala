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

package models.messages

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.{LanguageCodeEnglish, LocalReferenceNumber, XMLWrites}

import scala.xml.NodeSeq

//<xs:complexType>
//<xs:sequence>

//<xs:element name="ConIndHEA96" type="xs:string"/>
//<xs:element name="DiaLanIndAtDepHEA254" type="xs:string" minOccurs="0"/>
//<xs:element name="NCTSAccDocHEA601LNG" type="xs:string"/>
//<xs:element name="NumOfLoaLisHEA304" type="xs:string" minOccurs="0"/>
//<xs:element name="TotNumOfIteHEA305" type="xs:string"/>
//<xs:element name="TotNumOfPacHEA306" type="xs:string" minOccurs="0"/>
//<xs:element name="TotGroMasHEA307" type="xs:string"/>
//<xs:element name="DecDatHEA383" type="xs:string"/>
//<xs:element name="DecPlaHEA394" type="xs:string"/>
//<xs:element name="DecPlaHEA394LNG" type="xs:string" minOccurs="0"/>
//</xs:sequence>
//</xs:complexType>
case class Header(
  refNumHEA4: LocalReferenceNumber,
  typOfDecHEA24: String,
  couOfDesCodHEA30: Option[String],
  agrLocOfGooCodHEA38: Option[String],
  agrLocOfGooHEA39: Option[String],
  autLocOfGooCodHEA41: Option[String],
  plaOfLoaCodHEA46: Option[String],
  couOfDisCodHEA55: Option[String],
  cusSubPlaHEA66: Option[String],
  inlTraModHEA75: Option[Int],
  traModAtBorHEA76: Option[Int],
  ideOfMeaOfTraAtDHEA78: Option[String],
  natOfMeaOfTraAtDHEA80: Option[String],
  ideOfMeaOfTraCroHEA85: Option[String],
  natOfMeaOfTraCroHEA87: Option[String],
  typOfMeaOfTraCroHEA88: Option[Int]
)

object Header {

  val typeOfDeclarationLength             = 9
  val countryLength                       = 2
  val agreedLocationOfGoodsCodeLength     = 17
  val authorisedLocationOfGoodsCodeLength = 17
  val agreedLocationOfGoodsLength         = 35

  val identityMeansOfTransport = 27

  val placeOfLoadingGoodsCodeLength = 17
  val customsSubPlaceLength         = 17

  // scalastyle:off
  implicit def writes: XMLWrites[Header] = XMLWrites[Header] {
    header =>
      <HEAHEA>
        <RefNumHEA4>{escapeXml(header.refNumHEA4.toString)}</RefNumHEA4>
        <TypOfDecHEA24>{header.typOfDecHEA24}</TypOfDecHEA24>
        {
          header.couOfDesCodHEA30.fold(NodeSeq.Empty) ( value =>
            <CouOfDesCodHEA30>{escapeXml(value)}</CouOfDesCodHEA30>
          ) ++
          header.agrLocOfGooCodHEA38.fold(NodeSeq.Empty) ( value =>
            <AgrLocOfGooCodHEA38>{escapeXml(value)}</AgrLocOfGooCodHEA38>
          ) ++
          header.agrLocOfGooHEA39.fold(NodeSeq.Empty) ( value =>
            <AgrLocOfGooHEA39>{escapeXml(value)}</AgrLocOfGooHEA39>
            <AgrLocOfGooHEA39LNG>{LanguageCodeEnglish.code}</AgrLocOfGooHEA39LNG>
          ) ++
          header.autLocOfGooCodHEA41.fold(NodeSeq.Empty) ( value =>
            <AutLocOfGooCodHEA41>{escapeXml(value)}</AutLocOfGooCodHEA41>
          ) ++
          header.plaOfLoaCodHEA46.fold(NodeSeq.Empty) ( value =>
            <PlaOfLoaCodHEA46>{escapeXml(value)}</PlaOfLoaCodHEA46>
          ) ++
          header.couOfDisCodHEA55.fold(NodeSeq.Empty) ( value =>
            <CouOfDisCodHEA55>{escapeXml(value)}</CouOfDisCodHEA55>
          ) ++
          header.cusSubPlaHEA66.fold(NodeSeq.Empty) ( value =>
            <CusSubPlaHEA66>{escapeXml(value)}</CusSubPlaHEA66>
          ) ++
          header.inlTraModHEA75.fold(NodeSeq.Empty) ( value =>
            <InlTraModHEA75>{value}</InlTraModHEA75>
          ) ++
          header.traModAtBorHEA76.fold(NodeSeq.Empty) ( value =>
            <TraModAtBorHEA76>{value}</TraModAtBorHEA76>
          ) ++
          header.ideOfMeaOfTraAtDHEA78.fold(NodeSeq.Empty) (value =>
            <IdeOfMeaOfTraAtDHEA78>{escapeXml(value)}</IdeOfMeaOfTraAtDHEA78>
            <IdeOfMeaOfTraAtDHEA78LNG>{LanguageCodeEnglish.code}</IdeOfMeaOfTraAtDHEA78LNG>
          ) ++
          header.natOfMeaOfTraAtDHEA80.fold(NodeSeq.Empty) (value =>
            <NatOfMeaOfTraAtDHEA80>{escapeXml(value)}</NatOfMeaOfTraAtDHEA80>
          ) ++
          header.ideOfMeaOfTraCroHEA85.fold(NodeSeq.Empty) (value =>
            <IdeOfMeaOfTraCroHEA85>{escapeXml(value)}</IdeOfMeaOfTraCroHEA85>
            <IdeOfMeaOfTraCroHEA85LNG>{LanguageCodeEnglish.code}</IdeOfMeaOfTraCroHEA85LNG>
          ) ++
          header.natOfMeaOfTraCroHEA87.fold(NodeSeq.Empty) (value =>
            <NatOfMeaOfTraCroHEA87>{escapeXml(value)}</NatOfMeaOfTraCroHEA87>
          ) ++
          header.typOfMeaOfTraCroHEA88.fold(NodeSeq.Empty) (value =>
            <TypOfMeaOfTraCroHEA88>{value}</TypOfMeaOfTraCroHEA88>
          )
        }
      </HEAHEA>
  }
  // scalastyle:on

  implicit val reads: XmlReader[Header] = (
    __.read[LocalReferenceNumber],
    (__ \ "TypOfDecHEA24").read[String],
    (__ \ "CouOfDesCodHEA30").read[String].optional,
    (__ \ "AgrLocOfGooCodHEA38").read[String].optional,
    (__ \ "AgrLocOfGooHEA39").read[String].optional,
    (__ \ "AutLocOfGooCodHEA41").read[String].optional,
    (__ \ "PlaOfLoaCodHEA46").read[String].optional,
    (__ \ "CouOfDisCodHEA55").read[String].optional,
    (__ \ "CusSubPlaHEA66").read[String].optional,
    (__ \ "InlTraModHEA75").read[Int].optional,
    (__ \ "TraModAtBorHEA76").read[Int].optional,
    (__ \ "IdeOfMeaOfTraAtDHEA78").read[String].optional,
    (__ \ "NatOfMeaOfTraAtDHEA80").read[String].optional,
    (__ \ "IdeOfMeaOfTraCroHEA85").read[String].optional,
    (__ \ "NatOfMeaOfTraCroHEA87").read[String].optional,
    (__ \ "TypOfMeaOfTraCroHEA88").read[Int].optional
  ).mapN(apply)
}
