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
import models.XMLWrites._
import models.messages.trader.{TraderConsignorWithEori, TraderConsignorWithoutEori, TraderPrincipalWithEori, TraderPrincipalWithoutEori}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class TraderConsignorSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "TraderConsignorSpec" - {

    "must serialize TraderPrincipalWithEori to xml" in {

      forAll(arbitrary[TraderConsignorWithEori]) {
        trader =>
          val nameNode        = trader.name.map(value => <NamCO17>{escapeXml(value)}</NamCO17>)
          val streetNameNode  = trader.streetAndNumber.map(value => <StrAndNumCO122>{value}</StrAndNumCO122>)
          val postCodeNode    = trader.postCode.map(value => <PosCodCO123>{value}</PosCodCO123>)
          val cityNode        = trader.city.map(value => <CitCO124>{value}</CitCO124>)
          val countryCodeNode = trader.countryCode.map(value => <CouCO125>{value}</CouCO125>)

          val expectedResult =
            <TRACONCO1>
              {nameNode.getOrElse(NodeSeq.Empty) ++
              streetNameNode.getOrElse(NodeSeq.Empty) ++
              postCodeNode.getOrElse(NodeSeq.Empty) ++
              cityNode.getOrElse(NodeSeq.Empty) ++
              countryCodeNode.getOrElse(NodeSeq.Empty)}
              <NADLNGCO>EN</NADLNGCO>
              <TINCO159>{trader.eori}</TINCO159>
            </TRACONCO1>

          trader.toXml mustEqual expectedResult
      }

    }

    "must serialize TraderPrincipalWithoutEori to xml" in {
      forAll(arbitrary[TraderConsignorWithoutEori]) {
        trader =>
          val expectedResult =
            <TRACONCO1>
              <NamCO17>{escapeXml(trader.name)}</NamCO17>
              <StrAndNumCO122>{trader.streetAndNumber}</StrAndNumCO122>
              <PosCodCO123>{trader.postCode}</PosCodCO123>
              <CitCO124>{trader.city}</CitCO124>
              <CouCO125>{trader.countryCode}</CouCO125>
              <NADLNGCO>EN</NADLNGCO>
            </TRACONCO1>

          trader.toXml mustEqual expectedResult
      }

    }

  }

}
