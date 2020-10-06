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

import java.time.LocalDate

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.{LanguageCodeEnglish, LocalReferenceNumber, XMLWrites}
import models.XMLReads._
import utils.Format

import scala.xml.NodeSeq

case class Header(
  refNumHEA4: String,
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
  typOfMeaOfTraCroHEA88: Option[Int],
  conIndHEA96: Int, //TODO: If user specifies they're using a container, this is set to 1 (Containerised indicator)
  totNumOfIteHEA305: Int,
  totNumOfPacHEA306: Option[Int],
  totGroMasHEA307: String,
  decDatHEA383: LocalDate,
  decPlaHEA394: String
)

//TODO: NumOfLoaLisHEA304 not found in spec or appendix R. Optional field so doesn't need to be sent. What is it??

object Header {

  val typeOfDeclarationLength             = 9
  val countryLength                       = 2
  val agreedLocationOfGoodsCodeLength     = 17
  val authorisedLocationOfGoodsCodeLength = 17
  val agreedLocationOfGoodsLength         = 35
  val declarationPlace                    = 35
  val identityMeansOfTransport            = 27
  val placeOfLoadingGoodsCodeLength       = 17
  val customsSubPlaceLength               = 17

  // scalastyle:off
  implicit def writes: XMLWrites[Header] = XMLWrites[Header] {
    header =>
      <HEAHEA>
        <RefNumHEA4>{escapeXml(header.refNumHEA4)}</RefNumHEA4>
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
            <InlTraModHEA75>{value.toString}</InlTraModHEA75>
          ) ++
          header.traModAtBorHEA76.fold(NodeSeq.Empty) ( value =>
            <TraModAtBorHEA76>{value.toString}</TraModAtBorHEA76>
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
            <TypOfMeaOfTraCroHEA88>{value.toString}</TypOfMeaOfTraCroHEA88>
          )
        }
        <ConIndHEA96>{header.conIndHEA96.toString}</ConIndHEA96>
        <DiaLanIndAtDepHEA254>{LanguageCodeEnglish.code}</DiaLanIndAtDepHEA254>
        <NCTSAccDocHEA601LNG>{LanguageCodeEnglish.code}</NCTSAccDocHEA601LNG>
        <TotNumOfIteHEA305>{header.totNumOfIteHEA305.toString}</TotNumOfIteHEA305>
        {
          header.totNumOfPacHEA306.fold(NodeSeq.Empty) (value =>
            <TotNumOfPacHEA306>{value.toString}</TotNumOfPacHEA306>
          )
        }
        <TotGroMasHEA307>{header.totGroMasHEA307.toString}</TotGroMasHEA307>
        <DecDatHEA383>{Format.dateFormatted(header.decDatHEA383)}</DecDatHEA383>
        <DecPlaHEA394>{escapeXml(header.decPlaHEA394)}</DecPlaHEA394>
        <DecPlaHEA394LNG>{LanguageCodeEnglish.code}</DecPlaHEA394LNG>
      </HEAHEA>
  }
  // scalastyle:on

  implicit val reads: XmlReader[Header] = (
    (__ \ "RefNumHEA4").read[String],
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
    (__ \ "TypOfMeaOfTraCroHEA88").read[Int].optional,
    (__ \ "ConIndHEA96").read[Int],
    (__ \ "TotNumOfIteHEA305").read[Int],
    (__ \ "TotNumOfPacHEA306").read[Int].optional,
    (__ \ "TotGroMasHEA307").read[String],
    (__ \ "DecDatHEA383").read[LocalDate],
    (__ \ "DecPlaHEA394").read[String]
  ).mapN(apply)
}
