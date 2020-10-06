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
import models.messages.trader.{TraderConsigneeWithEori, TraderConsigneeWithoutEori, TraderConsignorWithEori, TraderConsignorWithoutEori}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class TraderConsigneeSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "TraderConsigneeSpec" - {

    "must serialize TraderConsigneeWithEori to xml" in {

      forAll(arbitrary[TraderConsigneeWithEori]) {
        trader =>
          val nameNode        = trader.name.map(value => <NamCE17>{escapeXml(value)}</NamCE17>)
          val streetNameNode  = trader.streetAndNumber.map(value => <StrAndNumCE122>{value}</StrAndNumCE122>)
          val postCodeNode    = trader.postCode.map(value => <PosCodCE123>{value}</PosCodCE123>)
          val cityNode        = trader.city.map(value => <CitCE124>{value}</CitCE124>)
          val countryCodeNode = trader.countryCode.map(value => <CouCE125>{value}</CouCE125>)

          val expectedResult =
            <TRACONCE1>
              {nameNode.getOrElse(NodeSeq.Empty) ++
              streetNameNode.getOrElse(NodeSeq.Empty) ++
              postCodeNode.getOrElse(NodeSeq.Empty) ++
              cityNode.getOrElse(NodeSeq.Empty) ++
              countryCodeNode.getOrElse(NodeSeq.Empty)}
              <NADLNGCE>EN</NADLNGCE>
              <TINCE159>{trader.eori}</TINCE159>
            </TRACONCE1>

          trader.toXml mustEqual expectedResult
      }

    }

    "must serialize TraderConsigneeWithoutEori to xml" in {
      forAll(arbitrary[TraderConsigneeWithoutEori]) {
        trader =>
          val expectedResult =
            <TRACONCE1>
              <NamCE17>{escapeXml(trader.name)}</NamCE17>
              <StrAndNumCE122>{trader.streetAndNumber}</StrAndNumCE122>
              <PosCodCE123>{trader.postCode}</PosCodCE123>
              <CitCE124>{trader.city}</CitCE124>
              <CouCE125>{trader.countryCode}</CouCE125>
              <NADLNGCE>EN</NADLNGCE>
            </TRACONCE1>

          trader.toXml mustEqual expectedResult
      }

    }

  }

}
