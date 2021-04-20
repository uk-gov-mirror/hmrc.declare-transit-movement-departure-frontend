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

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._

class SpecialMentionSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "XML" - {

    "SpecialMentionLiabilityAmount" - {

      "must serialise SpecialMentionLiabilityAmount to xml" in {

        forAll(arbitrary[SpecialMentionGuaranteeLiabilityAmount]) {
          specialMentionGuaranteeLiabilityAmount =>
            val expectedResult =
              <SPEMENMT2>
                <AddInfCodMT23>CAL</AddInfCodMT23>
                <AddInfMT21>{specialMentionGuaranteeLiabilityAmount.additionalInformationOfLiabilityAmount}</AddInfMT21>
              </SPEMENMT2>

            specialMentionGuaranteeLiabilityAmount.toXml mustEqual expectedResult
        }
      }

      "must deserialise when 'liability amount' exists and code is 'CAL'" in {

        forAll(arbitrary[SpecialMentionGuaranteeLiabilityAmount]) {
          specialMentionGuaranteeLiabilityAmount =>
            val xml =
              <SPEMENMT2>
                <AddInfCodMT23>CAL</AddInfCodMT23>
                <AddInfMT21>{specialMentionGuaranteeLiabilityAmount.additionalInformationOfLiabilityAmount}</AddInfMT21>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMention].read(xml).toOption.value

            result mustBe specialMentionGuaranteeLiabilityAmount

        }

      }

      "must fail to deserialise when code is not 'CAL'" in {

        forAll(arbitrary[SpecialMentionGuaranteeLiabilityAmount]) {
          specialMentionGuaranteeLiabilityAmount =>
            val xml =
              <SPEMENMT2>
                <AddInfCodMT23>XYZ</AddInfCodMT23>
                <AddInfMT21>{specialMentionGuaranteeLiabilityAmount.additionalInformationOfLiabilityAmount}</AddInfMT21>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMentionGuaranteeLiabilityAmount].read(xml).toOption

            result mustBe None

        }

      }
    }

    "SpecialMentionEc" - {

      "must serialise SpecialMentionEc to xml" in {

        forAll(arbitrary[SpecialMentionEc]) {
          specialMentionEc =>
            val expectedResult =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionEc.additionalInformationCoded}</AddInfCodMT23>
                <ExpFroECMT24>1</ExpFroECMT24>
              </SPEMENMT2>

            specialMentionEc.toXml mustEqual expectedResult
        }
      }

      "must deserialise when `export from EC` is true and code is country specific" in {

        forAll(arbitrary[SpecialMentionEc]) {
          specialMentionEc =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionEc.additionalInformationCoded}</AddInfCodMT23>
                <ExpFroECMT24>1</ExpFroECMT24>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMention].read(xml).toOption.value

            result mustBe specialMentionEc
        }
      }

      "must fail to deserialise when 'export from EC` is false" in {
        forAll(arbitrary[SpecialMentionEc]) {
          specialMentionEc =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionEc.additionalInformationCoded}</AddInfCodMT23>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMentionEc].read(xml).toOption

            result mustBe None
        }
      }

      "must fail to deserialise when code is not country specific" in {

        val xml =
          <SPEMENMT2>
            <AddInfMT21>Some Info</AddInfMT21>
            <AddInfCodMT23>Invalid code</AddInfCodMT23>
            <ExpFroECMT24>1</ExpFroECMT24>
          </SPEMENMT2>

        val result = XmlReader.of[SpecialMentionEc].read(xml).toOption

        result mustBe None
      }
    }

    "SpecialMentionOutsideEc" - {

      "must serialise SpecialMentionNonEc to xml" in {

        forAll(arbitrary[SpecialMentionNonEc]) {
          specialMentionNonEc =>
            val expectedResult =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionNonEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionNonEc.additionalInformationCoded}</AddInfCodMT23>
                <ExpFroCouMT25>{specialMentionNonEc.exportFromCountry}</ExpFroCouMT25>
              </SPEMENMT2>

            specialMentionNonEc.toXml mustEqual expectedResult
        }
      }

      "must deserialise when code is country specific" in {

        forAll(arbitrary[SpecialMentionNonEc]) {
          specialMentionNonEc =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionNonEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionNonEc.additionalInformationCoded}</AddInfCodMT23>
                <ExpFroCouMT25>{specialMentionNonEc.exportFromCountry}</ExpFroCouMT25>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMention].read(xml).toOption.value

            result mustBe specialMentionNonEc
        }
      }

      "must fail to deserialise when 'exportFromCountry` is not defined" in {
        forAll(arbitrary[SpecialMentionNoCountry]) {
          specialMentionNonEc =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionNonEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionNonEc.additionalInformationCoded}</AddInfCodMT23>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMentionNonEc].read(xml).toOption

            result mustBe None
        }
      }

      "must fail to deserialise when code is not country specific" in {

        val xml =
          <SPEMENMT2>
            <AddInfMT21>Something</AddInfMT21>
            <AddInfCodMT23>Invalid code</AddInfCodMT23>
            <ExpFroCouMT25>GB</ExpFroCouMT25>
          </SPEMENMT2>

        val result = XmlReader.of[SpecialMentionNonEc].read(xml).toOption

        result mustBe None
      }

    }

    "SpecialMentionNoCountry" - {

      "must serialise SpecialMentionNonEc to xml" in {

        forAll(arbitrary[SpecialMentionNoCountry]) {
          specialMentionNoCountry =>
            val expectedResult =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionNoCountry.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionNoCountry.additionalInformationCoded}</AddInfCodMT23>
              </SPEMENMT2>

            specialMentionNoCountry.toXml mustEqual expectedResult
        }
      }

      "must deserialise when the code is not country specific" in {
        forAll(arbitrary[SpecialMentionNoCountry]) {
          specialMentionNoCountry =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionNoCountry.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionNoCountry.additionalInformationCoded}</AddInfCodMT23>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMention].read(xml).toOption.value

            result mustBe specialMentionNoCountry
        }
      }

      "must fail to deserialise when the code is country specific" in {

        forAll(countrySpecificCodeGen) {
          additionalInformation =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>Additional Info</AddInfMT21>
                <AddInfCodMT23>{additionalInformation}</AddInfCodMT23>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMentionNoCountry].read(xml).toOption

            result mustBe None
        }
      }
    }
  }
}
