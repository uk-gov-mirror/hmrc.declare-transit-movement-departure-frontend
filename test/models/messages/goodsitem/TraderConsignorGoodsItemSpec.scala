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

package models.messages.goodsitem

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import models.XMLWrites._
import models.messages.escapeXml
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class TraderConsignorGoodsItemSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "TraderConsignorGoodsItemSpec" - {

    "must serialize TraderConsignorGoodsItem to xml" in {
      forAll(arbitrary[TraderConsignorGoodsItem]) {
        trader =>
          val eori = trader.eori.map(value => <TINCO259>
            {value}
          </TINCO259>)

          val expectedResult =
            <TRACONCO2>
              <NamCO27>
                {escapeXml(trader.name)}
              </NamCO27>
              <StrAndNumCO222>
                {trader.streetAndNumber}
              </StrAndNumCO222>
              <PosCodCO223>
                {trader.postCode}
              </PosCodCO223>
              <CitCO224>
                {trader.city}
              </CitCO224>
              <CouCO225>
                {trader.countryCode}
              </CouCO225>
              <NADLNGGTCO>EN</NADLNGGTCO>{eori.getOrElse(NodeSeq.Empty)}
            </TRACONCO2>

          trader.toXml mustEqual expectedResult
      }

    }

    "must deserialize TraderConsignorGoodsItem from xml" in {
      forAll(arbitrary[TraderConsignorGoodsItem]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[TraderConsignorGoodsItem].read(xml).toOption.value
          result mustBe data
      }
    }

  }

}
