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
import models.LanguageCodeEnglish
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

//format off
class ItemsSecurityConsigneeWithoutEoriSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "ItemSecurityConsigneeWithoutEori" - {
    "must serialize ItemSecurityConsigneeWithoutEori to xml" in {

      forAll(arbitrary[ItemsSecurityConsigneeWithoutEori]) {
        consignee =>
          val expectedResult =
            <TRACONSECGOO013>
              <NamTRACONSECGOO017>{consignee.name}</NamTRACONSECGOO017>
              <StrNumTRACONSECGOO019>{consignee.streetAndNumber}</StrNumTRACONSECGOO019>
              <PosCodTRACONSECGOO018>{consignee.postCode}</PosCodTRACONSECGOO018>
              <CityTRACONSECGOO014>{consignee.city}</CityTRACONSECGOO014>
              <CouCodTRACONSECGOO015>{consignee.countryCode}</CouCodTRACONSECGOO015>
              <TRACONSECGOO013LNG>{LanguageCodeEnglish.code}</TRACONSECGOO013LNG>
            </TRACONSECGOO013>

          consignee.toXml mustEqual expectedResult
//format on
      }
    }
  }
}
