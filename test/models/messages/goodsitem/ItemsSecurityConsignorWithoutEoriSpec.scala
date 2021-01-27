/*
 * Copyright 2021 HM Revenue & Customs
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

import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

//format off
class ItemsSecurityConsignorWithoutEoriSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "ItemSecurityConsignorWithoutEori" - {
    "must serialize ItemSecurityConsignorWithoutEori to xml" in {

      forAll(arbitrary[ItemsSecurityConsignorWithoutEori]) {
        consignor =>
          val expectedResult =
            <TRACORSECGOO021>
              <NamTRACORSECGOO025>{consignor.name}</NamTRACORSECGOO025>
              <StrNumTRACORSECGOO027>{consignor.streetAndNumber}</StrNumTRACORSECGOO027>
              <PosCodTRACORSECGOO026>{consignor.postCode}</PosCodTRACORSECGOO026>
              <CitTRACORSECGOO022>{consignor.city}</CitTRACORSECGOO022>
              <CouCodTRACORSECGOO023>{consignor.countryCode}</CouCodTRACORSECGOO023>
              <TRACORSECGOO021LNG>EN</TRACORSECGOO021LNG>
            </TRACORSECGOO021>

          consignor.toXml mustEqual expectedResult
//format on
      }
    }
  }
}
