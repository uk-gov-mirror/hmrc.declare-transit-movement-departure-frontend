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

import generators.MessagesModelGenerators
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary._
import com.lucidchart.open.xtract.XmlReader

import scala.xml.{Node, NodeSeq}
import models.XMLWrites._
import models.messages.trader.{TraderPrincipal, TraderPrincipalWithEori, TraderPrincipalWithoutEori}

import scala.xml.Utility.trim

class DeclarationRequestSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  import DeclarationRequestSpec._

  "DeclarationRequest" - {

    "must serialise DeclarationRequest to xml" in {
      //TODO: This needs more xml nodes adding as models become available
      forAll(arbitrary[DeclarationRequest]) {
        declarationRequest =>
          val expectedResult: Node =
            <CC015B>
              {declarationRequest.meta.toXml}
              {declarationRequest.header.toXml}
              {traderPrinciple(declarationRequest.traderPrincipal)}
              {declarationRequest.traderConsignor.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.traderConsignee.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.traderAuthorisedConsignee.toXml}
              {declarationRequest.customsOfficeDeparture.toXml}
              {declarationRequest.customsOfficeTransit.flatMap(_.toXml)}
              {declarationRequest.customsOfficeDestination.toXml}
              {declarationRequest.controlResult.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.representative.map(_.toXml).getOrElse(NodeSeq.Empty)}
              {declarationRequest.seals.map(_.toXml).getOrElse(NodeSeq.Empty)}
            </CC015B>

          declarationRequest.toXml.map(trim) mustBe expectedResult.map(trim)
      }

    }

    "must de-serialise xml to DeclarationRequest" in {

      forAll(arbitrary[DeclarationRequest]) {
        declarationRequest =>
          val result = XmlReader.of[DeclarationRequest].read(declarationRequest.toXml)
          result.toOption.value mustBe declarationRequest
      }

    }

  }

}

object DeclarationRequestSpec {

  def traderPrinciple(traderPrincipal: TraderPrincipal): NodeSeq = traderPrincipal match {
    case traderPrincipalWithEori: TraderPrincipalWithEori       => traderPrincipalWithEori.toXml
    case traderPrincipalWithoutEori: TraderPrincipalWithoutEori => traderPrincipalWithoutEori.toXml
    case _                                                      => NodeSeq.Empty
  }
}
