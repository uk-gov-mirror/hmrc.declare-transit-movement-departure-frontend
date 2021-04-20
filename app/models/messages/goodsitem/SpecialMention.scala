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

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, XmlReader}
import play.api.libs.json._
import utils.BinaryToBooleanXMLReader._
import xml.XMLWrites

trait SpecialMention {

  def additionalInformationCoded: String
}

object SpecialMention {

  object Constants {
    val specialMentionCount = 99
  }

  val countrySpecificCodes = Seq("DG0", "DG1")

  implicit val xmlReader: XmlReader[SpecialMention] =
    SpecialMentionGuaranteeLiabilityAmount.xmlReader
      .or(SpecialMentionEc.xmlReader)
      .or(SpecialMentionNonEc.xmlReader)
      .or(SpecialMentionNoCountry.xmlReader)

}

final case class SpecialMentionEc(additionalInformationCoded: String, additionalInformation: String) extends SpecialMention

object SpecialMentionEc {

  implicit val xmlReader: XmlReader[SpecialMentionEc] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionEcParseFailure(message: String) extends ParseError

    (__ \ "ExpFroECMT24")
      .read[Boolean]
      .flatMap {
        case true =>
          XmlReader(
            _ => ParseSuccess(true)
          )
        case false =>
          XmlReader(
            _ => ParseFailure(SpecialMentionEcParseFailure("Failed to parse to SpecialMentionEc: ExpFroECMT24 was false"))
          )
      }
      .flatMap {
        _ =>
          (
            (__ \ "AddInfCodMT23").read[String],
            (__ \ "AddInfMT21").read[String]
          ).tupled.flatMap {
            case (code, addInfo) =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                XmlReader(
                  _ => ParseSuccess(SpecialMentionEc(code, addInfo))
                )
              } else {
                XmlReader(
                  _ => ParseFailure(SpecialMentionEcParseFailure(s"Failed to parse to SpecialMentionEc: $code was not country specific"))
                )
              }
          }
      }
  }

  implicit def writesXml: XMLWrites[SpecialMentionEc] = XMLWrites[SpecialMentionEc] {
    specialMention =>
      <SPEMENMT2>
        <AddInfMT21>{specialMention.additionalInformation}</AddInfMT21>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
        <ExpFroECMT24>1</ExpFroECMT24>
      </SPEMENMT2>
  }
}

final case class SpecialMentionNonEc(
  additionalInformationCoded: String,
  additionalInformation: String,
  exportFromCountry: String
) extends SpecialMention

object SpecialMentionNonEc {

  implicit val xmlReader: XmlReader[SpecialMentionNonEc] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionNonEcParseFailure(message: String) extends ParseError

    (__ \ "AddInfCodMT23")
      .read[String]
      .flatMap {
        code =>
          if (SpecialMention.countrySpecificCodes.contains(code)) {
            XmlReader(
              _ => ParseSuccess(code)
            )
          } else {
            XmlReader(
              _ => ParseFailure(SpecialMentionNonEcParseFailure(s"Failed to parse to SpecialMentionNonEc: $code was not country specific"))
            )
          }
      }
      .flatMap {
        code =>
          (
            (__ \ "ExpFroCouMT25").read[String],
            (__ \ "AddInfMT21").read[String]
          ).tupled.flatMap {
            case (exportFromCountry, addInfo) =>
              XmlReader(
                _ => ParseSuccess(SpecialMentionNonEc(code, addInfo, exportFromCountry))
              )
          }
      }
  }

  implicit def writesXml: XMLWrites[SpecialMentionNonEc] = XMLWrites[SpecialMentionNonEc] {
    specialMention =>
      <SPEMENMT2>
        <AddInfMT21>{specialMention.additionalInformation}</AddInfMT21>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
        <ExpFroCouMT25>{specialMention.exportFromCountry}</ExpFroCouMT25>
      </SPEMENMT2>
  }
}

final case class SpecialMentionNoCountry(additionalInformationCoded: String, additionalInformation: String) extends SpecialMention

object SpecialMentionNoCountry {

  implicit val xmlReader: XmlReader[SpecialMentionNoCountry] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionNoCountryParseFailure(message: String) extends ParseError

    (
      (__ \ "AddInfCodMT23").read[String],
      (__ \ "AddInfMT21").read[String]
    ).tupled.flatMap {
      case (code, _) if SpecialMention.countrySpecificCodes.contains(code) =>
        XmlReader(
          _ => ParseFailure(SpecialMentionNoCountryParseFailure(s"Failed to parse to SpecialMentionNoCountry: $code was country specific"))
        )
      case (code, info) =>
        XmlReader(
          _ => ParseSuccess(SpecialMentionNoCountry(code, info))
        )
    }
  }

  implicit def writesXml: XMLWrites[SpecialMentionNoCountry] = XMLWrites[SpecialMentionNoCountry] {
    specialMention =>
      <SPEMENMT2>
        <AddInfMT21>{specialMention.additionalInformation}</AddInfMT21>
        <AddInfCodMT23>{specialMention.additionalInformationCoded}</AddInfCodMT23>
      </SPEMENMT2>
  }
}

final case class SpecialMentionGuaranteeLiabilityAmount(
  additionalInformationCoded: String,
  additionalInformationOfLiabilityAmount: String
) extends SpecialMention

object SpecialMentionGuaranteeLiabilityAmount {

  implicit val xmlReader: XmlReader[SpecialMentionGuaranteeLiabilityAmount] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionGuaranteeLiabilityAmountParseFailure(message: String) extends ParseError

    (__ \ "AddInfCodMT23").read[String].flatMap {
      case "CAL" => {
        (__ \ "AddInfMT21").read[String].flatMap {
          liabilityAmount =>
            XmlReader(
              _ => ParseSuccess(SpecialMentionGuaranteeLiabilityAmount("CAL", liabilityAmount))
            )
        }
      }
      case _ =>
        XmlReader(
          _ => ParseFailure(SpecialMentionGuaranteeLiabilityAmountParseFailure(s"Failed to parse to SpecialMentionGuaranteeLiabilityAmount does not exist"))
        )
    }
  }

  implicit def writesXml: XMLWrites[SpecialMentionGuaranteeLiabilityAmount] = XMLWrites[SpecialMentionGuaranteeLiabilityAmount] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>CAL</AddInfCodMT23>
        <AddInfMT21>{specialMention.additionalInformationOfLiabilityAmount}</AddInfMT21>
      </SPEMENMT2>
  }

}
