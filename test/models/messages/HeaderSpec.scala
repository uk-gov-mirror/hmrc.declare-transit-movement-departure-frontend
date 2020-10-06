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

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import models.XMLWrites._
import utils.Format

import scala.xml.NodeSeq
import scala.xml.Utility.trim

class HeaderSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with MessagesModelGenerators with StreamlinedXmlEquality with OptionValues {

  "HeaderSpec" - {

    "must serialize Header to xml" in {
      forAll(arbitrary[Header]) {
        header =>
          val couOfDesCodHEA30 = header.couOfDesCodHEA30.map(
            value => <CouOfDesCodHEA30>{escapeXml(value)}</CouOfDesCodHEA30>
          )

          val agrLocOfGooCodHEA38 = header.agrLocOfGooCodHEA38.map(
            value => <AgrLocOfGooCodHEA38>{escapeXml(value)}</AgrLocOfGooCodHEA38>
          )

          val agrLocOfGooHEA39 = header.agrLocOfGooHEA39.map(
            value => <AgrLocOfGooHEA39>{escapeXml(value)}</AgrLocOfGooHEA39>
              <AgrLocOfGooHEA39LNG>EN</AgrLocOfGooHEA39LNG>
          )

          val autLocOfGooCodHEA41 = header.autLocOfGooCodHEA41.map(
            value => <AutLocOfGooCodHEA41>{escapeXml(value)}</AutLocOfGooCodHEA41>
          )

          val plaOfLoaCodHEA46 = header.plaOfLoaCodHEA46.map(
            value => <PlaOfLoaCodHEA46>{escapeXml(value)}</PlaOfLoaCodHEA46>
          )
          val couOfDisCodHEA55 = header.couOfDisCodHEA55.map(
            value => <CouOfDisCodHEA55>{escapeXml(value)}</CouOfDisCodHEA55>
          )
          val cusSubPlaHEA66 = header.cusSubPlaHEA66.map(
            value => <CusSubPlaHEA66>{escapeXml(value)}</CusSubPlaHEA66>
          )

          val inlTraModHEA75 = header.inlTraModHEA75.map(
            value => <InlTraModHEA75>{value.toString}</InlTraModHEA75>
          )

          val traModAtBorHEA76 = header.traModAtBorHEA76.map(
            value => <TraModAtBorHEA76>{value.toString}</TraModAtBorHEA76>
          )

          val ideOfMeaOfTraAtDHEA78 = header.ideOfMeaOfTraAtDHEA78.map(
            value => <IdeOfMeaOfTraAtDHEA78>{escapeXml(value)}</IdeOfMeaOfTraAtDHEA78>
              <IdeOfMeaOfTraAtDHEA78LNG>EN</IdeOfMeaOfTraAtDHEA78LNG>
          )

          val natOfMeaOfTraAtDHEA80 = header.natOfMeaOfTraAtDHEA80.map(
            value => <NatOfMeaOfTraAtDHEA80>{escapeXml(value)}</NatOfMeaOfTraAtDHEA80>
          )

          val ideOfMeaOfTraCroHEA85 = header.ideOfMeaOfTraCroHEA85.map(
            value => <IdeOfMeaOfTraCroHEA85>{escapeXml(value)}</IdeOfMeaOfTraCroHEA85>
              <IdeOfMeaOfTraCroHEA85LNG>EN</IdeOfMeaOfTraCroHEA85LNG>
          )

          val natOfMeaOfTraCroHEA87 = header.natOfMeaOfTraCroHEA87.map(
            value => <NatOfMeaOfTraCroHEA87>{escapeXml(value)}</NatOfMeaOfTraCroHEA87>
          )

          val typOfMeaOfTraCroHEA88 = header.typOfMeaOfTraCroHEA88.map(
            value => <TypOfMeaOfTraCroHEA88>{value.toString}</TypOfMeaOfTraCroHEA88>
          )

          val totNumOfPacHEA306 = header.totNumOfPacHEA306.map(
            value => <TotNumOfPacHEA306>{value.toString}</TotNumOfPacHEA306>
          )

          val expectedResult: NodeSeq =
            <HEAHEA>
              <RefNumHEA4>{escapeXml(header.refNumHEA4)}</RefNumHEA4>
              <TypOfDecHEA24>{header.typOfDecHEA24}</TypOfDecHEA24>
              {couOfDesCodHEA30.getOrElse(NodeSeq.Empty)}
              {agrLocOfGooCodHEA38.getOrElse(NodeSeq.Empty)}
              {agrLocOfGooHEA39.getOrElse(NodeSeq.Empty)}
              {autLocOfGooCodHEA41.getOrElse(NodeSeq.Empty)}
              {plaOfLoaCodHEA46.getOrElse(NodeSeq.Empty)}
              {couOfDisCodHEA55.getOrElse(NodeSeq.Empty)}
              {cusSubPlaHEA66.getOrElse(NodeSeq.Empty)}
              {inlTraModHEA75.getOrElse(NodeSeq.Empty)}
              {traModAtBorHEA76.getOrElse(NodeSeq.Empty)}
              {ideOfMeaOfTraAtDHEA78.getOrElse(NodeSeq.Empty)}
              {natOfMeaOfTraAtDHEA80.getOrElse(NodeSeq.Empty)}
              {ideOfMeaOfTraCroHEA85.getOrElse(NodeSeq.Empty)}
              {natOfMeaOfTraCroHEA87.getOrElse(NodeSeq.Empty)}
              {typOfMeaOfTraCroHEA88.getOrElse(NodeSeq.Empty)}
              <ConIndHEA96>{header.conIndHEA96.toString}</ConIndHEA96>
              <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
              <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
              <TotNumOfIteHEA305>{header.totNumOfIteHEA305.toString}</TotNumOfIteHEA305>
              {totNumOfPacHEA306.getOrElse(NodeSeq.Empty)}
              <TotGroMasHEA307>{header.totGroMasHEA307.toString}</TotGroMasHEA307>
              <DecDatHEA383>{Format.dateFormatted(header.decDatHEA383)}</DecDatHEA383>
              <DecPlaHEA394>{escapeXml(header.decPlaHEA394)}</DecPlaHEA394>
              <DecPlaHEA394LNG>EN</DecPlaHEA394LNG>
            </HEAHEA>

          header.toXml.map(trim) mustEqual expectedResult.map(trim)
      }
    }

    "must deserialize Xml to Header" in {
      forAll(arbitrary[Header]) {
        header =>
          val result = XmlReader.of[Header].read(header.toXml)

          result.toOption.value mustBe header
      }

    }

  }

}
