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
import play.api.libs.json.{JsError, JsSuccess, Json}
import xml.XMLWrites._

class SpecialMentionSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "SpecialMentionLiabilityAmount" - {

    "must deserialise when 'liability Amount' exists and code is 'CAL'" in {

      forAll(liabilityAmountGen) {
        liabilityAmount =>
          val json = Json.obj(
            "additionalInformationCoded" -> "CAL",
            "additionalInformation"      -> liabilityAmount
          )

          val expectedSpecialMention = SpecialMentionGuaranteeLiabilityAmount("CAL", liabilityAmount)

          json.validate[SpecialMentionGuaranteeLiabilityAmount] mustEqual JsSuccess(expectedSpecialMention)
      }
    }

    "must fail to deserialise when code is not 'CAL'" in {

      forAll(liabilityAmountGen) {
        liabilityAmount =>
          val json = Json.obj(
            "additionalInformationCoded" -> "XYZ",
            "additionalInformation"      -> liabilityAmount
          )

          json.validate[SpecialMentionGuaranteeLiabilityAmount] mustEqual JsError("Failed to parse to SpecialMentionGuaranteeLiabilityAmount does not exist")
      }
    }

    "must serialise" in {
      forAll(liabilityAmountGen) {
        liabilityAmount =>
          val json = Json.obj(
            "additionalInformationCoded" -> "CAL",
            "additionalInformation"      -> liabilityAmount
          )

          Json.toJson(SpecialMentionGuaranteeLiabilityAmount("CAL", liabilityAmount))(SpecialMentionGuaranteeLiabilityAmount.writes) mustEqual json

      }
    }
  }

  "SpecialMentionEc" - {

    "must deserialise when `export from EC` is true and code is country specific" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> true
          )

          val expectedMention = SpecialMentionEc(additionalInformationCoded, additionalInformation)

          json.validate[SpecialMentionEc] mustEqual JsSuccess(expectedMention)
      }
    }

    "must fail to deserialise when `export from EC` is false" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> false
          )

          json.validate[SpecialMentionEc] mustEqual JsError("exportFromEc must be true")
      }
    }

    "must fail to deserialise when the code is not country-specific" in {

      forAll(nonCountrySpecificCodeGen) {
        additionalInformation =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformation,
            "exportFromEc"               -> true
          )

          json.validate[SpecialMentionEc] mustBe a[JsError]
      }
    }

    "must serialise" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> true
          )

          Json.toJson(SpecialMentionEc(additionalInformationCoded, additionalInformation))(SpecialMentionEc.writes) mustEqual json
      }
    }
  }

  "SpecialMentionOutsideEc" - {

    "must deserialise when `export from EC` is false and code is country specific" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(2), stringsWithMaxLength(70)) {
        case (additionalInformationCoded, country, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> false,
            "exportFromCountry"          -> country
          )

          val expectedMention = SpecialMentionNonEc(additionalInformationCoded, additionalInformation, country)

          json.validate[SpecialMentionNonEc] mustEqual JsSuccess(expectedMention)
      }
    }

    "must fail to deserialise when 'export from EC` is true" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(2), stringsWithMaxLength(70)) {
        case (additionalInformationCoded, country, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> true,
            "exportFromCountry"          -> country
          )

          json.validate[SpecialMentionNonEc] mustEqual JsError("exportFromEc must be false")
      }
    }

    "must fail to deserialise when code is not country specific" in {

      forAll(nonCountrySpecificCodeGen, stringsWithMaxLength(2)) {
        case (additionalInformation, country) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformation,
            "exportFromEc"               -> false,
            "exportFromCountry"          -> country
          )

          json.validate[SpecialMentionEc] mustBe a[JsError]
      }
    }

    "must serialise" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(2), stringsWithMaxLength(70)) {
        case (additionalInformationCoded, country, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> false,
            "exportFromCountry"          -> country
          )

          Json.toJson(SpecialMentionNonEc(additionalInformationCoded, additionalInformation, country))(SpecialMentionNonEc.writes) mustEqual json
      }
    }
  }

  "Special Mention No Country" - {

    "must deserialise when the code is not country specific" in {

      forAll(nonCountrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation
          )

          val expectedMention = SpecialMentionNoCountry(additionalInformationCoded, additionalInformation)

          json.validate[SpecialMentionNoCountry] mustEqual JsSuccess(expectedMention)
      }
    }

    "must fail to deserialise when the code is country specific" in {

      forAll(countrySpecificCodeGen) {
        additionalInformation =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformation
          )

          json.validate[SpecialMentionNoCountry] mustBe a[JsError]
      }
    }

    "must serialise" in {

      forAll(nonCountrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation
          )

          Json.toJson(SpecialMentionNoCountry(additionalInformationCoded, additionalInformation))(SpecialMentionNoCountry.writes) mustEqual json
      }
    }
  }

  "Special Mention" - {

    "must deserialise to a Special Mention EC" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> true
          )

          val expectedMention = SpecialMentionEc(additionalInformationCoded, additionalInformation)

          json.validate[SpecialMention] mustEqual JsSuccess(expectedMention)
      }
    }

    "must deserialise to a Special Mention Non-EC" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(2), stringsWithMaxLength(70)) {
        case (additionalInformationCoded, country, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> false,
            "exportFromCountry"          -> country
          )

          val expectedMention = SpecialMentionNonEc(additionalInformationCoded, additionalInformation, country)

          json.validate[SpecialMention] mustEqual JsSuccess(expectedMention)
      }
    }

    "must deserialise to a Special Mention No Country" in {

      forAll(nonCountrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation
          )

          val expectedMention = SpecialMentionNoCountry(additionalInformationCoded, additionalInformation)

          json.validate[SpecialMention] mustEqual JsSuccess(expectedMention)
      }
    }

    "must serialise from a Special Mention EC" in {

      forAll(nonCountrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> true
          )

          Json.toJson(SpecialMentionEc(additionalInformationCoded, additionalInformation): SpecialMention) mustEqual json
      }
    }

    "must serialise from a Special Mention Non-EC" in {

      forAll(countrySpecificCodeGen, stringsWithMaxLength(2), stringsWithMaxLength(70)) {
        (additionalInformationCoded, country, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation,
            "exportFromEc"               -> false,
            "exportFromCountry"          -> country
          )

          Json.toJson(SpecialMentionNonEc(additionalInformationCoded, additionalInformation, country): SpecialMention) mustEqual json
      }
    }

    "must serialise from a Special Mention No Country" in {

      forAll(nonCountrySpecificCodeGen, stringsWithMaxLength(70)) {
        (additionalInformationCoded, additionalInformation) =>
          val json = Json.obj(
            "additionalInformationCoded" -> additionalInformationCoded,
            "additionalInformation"      -> additionalInformation
          )

          Json.toJson(SpecialMentionNoCountry(additionalInformationCoded, additionalInformation): SpecialMention) mustEqual json
      }
    }
  }

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
                <ExpFroECMT24>0</ExpFroECMT24>
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
                <ExpFroECMT24>0</ExpFroECMT24>
                <ExpFroCouMT25>{specialMentionNonEc.exportFromCountry}</ExpFroCouMT25>
              </SPEMENMT2>

            specialMentionNonEc.toXml mustEqual expectedResult
        }
      }

      "must deserialise when `export from EC` is false and code is country specific" in {

        forAll(arbitrary[SpecialMentionNonEc]) {
          specialMentionNonEc =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionNonEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionNonEc.additionalInformationCoded}</AddInfCodMT23>
                <ExpFroECMT24>0</ExpFroECMT24>
                <ExpFroCouMT25>{specialMentionNonEc.exportFromCountry}</ExpFroCouMT25>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMention].read(xml).toOption.value

            result mustBe specialMentionNonEc
        }
      }

      "must fail to deserialise when 'export from EC` is true" in {
        forAll(arbitrary[SpecialMentionNonEc]) {
          specialMentionNonEc =>
            val xml =
              <SPEMENMT2>
                <AddInfMT21>{specialMentionNonEc.additionalInformation}</AddInfMT21>
                <AddInfCodMT23>{specialMentionNonEc.additionalInformationCoded}</AddInfCodMT23>
                <ExpFroECMT24>1</ExpFroECMT24>
                <ExpFroCouMT25>{specialMentionNonEc.exportFromCountry}</ExpFroCouMT25>
              </SPEMENMT2>

            val result = XmlReader.of[SpecialMentionNonEc].read(xml).toOption

            result mustBe None
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
            <ExpFroECMT24>0</ExpFroECMT24>
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
