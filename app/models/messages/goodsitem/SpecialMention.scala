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

  implicit lazy val reads: Reads[SpecialMention] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    SpecialMentionGuaranteeLiabilityAmount.reads or
      SpecialMentionEc.reads or
      SpecialMentionNonEc.reads or
      SpecialMentionNoCountry.reads

  }

  implicit lazy val writes: OWrites[SpecialMention] = OWrites {
    case sm: SpecialMentionGuaranteeLiabilityAmount => Json.toJsObject(sm)(SpecialMentionGuaranteeLiabilityAmount.writes)
    case sm: SpecialMentionEc => Json.toJsObject(sm)(SpecialMentionEc.writes)
    case sm: SpecialMentionNonEc => Json.toJsObject(sm)(SpecialMentionNonEc.writes)
    case sm: SpecialMentionNoCountry => Json.toJsObject(sm)(SpecialMentionNoCountry.writes)
  }

}

final case class SpecialMentionEc(additionalInformationCoded: String) extends SpecialMention

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
          (__ \ "AddInfCodMT23").read[String].flatMap {
            code =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                XmlReader(
                  _ => ParseSuccess(SpecialMentionEc(code))
                )
              } else {
                XmlReader(
                  _ => ParseFailure(SpecialMentionEcParseFailure(s"Failed to parse to SpecialMentionEc: $code was not country specific"))
                )
              }
          }
      }
  }

  implicit lazy val reads: Reads[SpecialMentionEc] = {

    import play.api.libs.functional.syntax._

    (__ \ "exportFromEc")
      .read[Boolean]
      .flatMap[Boolean] {
        fromEc =>
          if (fromEc) {
            Reads(
              _ => JsSuccess(fromEc)
            )
          } else {
            Reads(
              _ => JsError("exportFromEc must be true")
            )
          }
      }
      .andKeep(
        (__ \ "additionalInformationCoded")
          .read[String]
          .flatMap[String] {
            code =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                Reads(
                  _ => JsSuccess(code)
                )
              } else {
                Reads(
                  _ => JsError(s"additionalInformationCoded must be in ${SpecialMention.countrySpecificCodes}")
                )
              }
          }
      )
      .andKeep(
        (__ \ "additionalInformationCoded")
          .read[String]
          .map(SpecialMentionEc(_))
      )
  }

  implicit lazy val writes: OWrites[SpecialMentionEc] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "exportFromEc").write[Boolean] and
        (__ \ "additionalInformationCoded").write[String]
      ) (
      s => (true, s.additionalInformationCoded)
    )
  }

  implicit def writesXml: XMLWrites[SpecialMentionEc] = XMLWrites[SpecialMentionEc] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>
          {specialMention.additionalInformationCoded}
        </AddInfCodMT23>
        <ExpFroECMT24>1</ExpFroECMT24>
      </SPEMENMT2>
  }
}

final case class SpecialMentionNonEc(
                                      additionalInformationCoded: String,
                                      exportFromCountry: String
                                    ) extends SpecialMention

object SpecialMentionNonEc {

  implicit val xmlReader: XmlReader[SpecialMentionNonEc] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionNonEcParseFailure(message: String) extends ParseError

    (__ \ "ExpFroECMT24")
      .read[Boolean]
      .flatMap {
        case true =>
          XmlReader(
            _ => ParseFailure(SpecialMentionNonEcParseFailure("Failed to parse to SpecialMentionNonEc: ExpFroECMT24 was true"))
          )
        case false =>
          XmlReader(
            _ => ParseSuccess(false)
          )
      }
      .flatMap {
        _ =>
          (__ \ "AddInfCodMT23").read[String].flatMap {
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
      }
      .flatMap {
        code =>
          (__ \ "ExpFroCouMT25").read[String].flatMap {
            exportFromCountry =>
              XmlReader(
                _ => ParseSuccess(SpecialMentionNonEc(code, exportFromCountry))
              )
          }
      }
  }

  implicit lazy val reads: Reads[SpecialMentionNonEc] = {

    import play.api.libs.functional.syntax._

    (__ \ "exportFromEc")
      .read[Boolean]
      .flatMap[Boolean] {
        fromEc =>
          if (fromEc) {
            Reads(
              _ => JsError("exportFromEc must be false")
            )
          } else {
            Reads(
              _ => JsSuccess(fromEc)
            )
          }
      }
      .andKeep(
        (__ \ "additionalInformationCoded")
          .read[String]
          .flatMap[String] {
            code =>
              if (SpecialMention.countrySpecificCodes.contains(code)) {
                Reads(
                  _ => JsSuccess(code)
                )
              } else {
                Reads(
                  _ => JsError(s"additionalInformationCoded must be in ${SpecialMention.countrySpecificCodes}")
                )
              }
          }
      )
      .andKeep(
        (
          (__ \ "additionalInformationCoded").read[String] and
            (__ \ "exportFromCountry").read[String]
          ) (SpecialMentionNonEc(_, _))
      )
  }

  implicit lazy val writes: OWrites[SpecialMentionNonEc] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "exportFromEc").write[Boolean] and
        (__ \ "additionalInformationCoded").write[String] and
        (__ \ "exportFromCountry").write[String]
      ) (
      s => (false, s.additionalInformationCoded, s.exportFromCountry)
    )
  }

  implicit def writesXml: XMLWrites[SpecialMentionNonEc] = XMLWrites[SpecialMentionNonEc] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>
          {specialMention.additionalInformationCoded}
        </AddInfCodMT23>
        <ExpFroECMT24>0</ExpFroECMT24>
        <ExpFroCouMT25>
          {specialMention.exportFromCountry}
        </ExpFroCouMT25>
      </SPEMENMT2>
  }
}

final case class SpecialMentionNoCountry(additionalInformationCoded: String, additionalInformation: String) extends SpecialMention

object SpecialMentionNoCountry {

  implicit val xmlReader: XmlReader[SpecialMentionNoCountry] = {

    import com.lucidchart.open.xtract.__

    case class SpecialMentionNoCountryParseFailure(message: String) extends ParseError

    ((__ \ "AddInfCodMT23").read[String], (__ \ "AddInfMT21").read[String]).tupled.flatMap {
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

  implicit lazy val reads: Reads[SpecialMentionNoCountry] = {

    import play.api.libs.functional.syntax._

    (__ \ "additionalInformationCoded")
      .read[String]
      .flatMap[String] {
        code =>
          if (SpecialMention.countrySpecificCodes.contains(code)) {
            Reads(
              _ => JsError(s"additionalInformationCoded must not be in ${SpecialMention.countrySpecificCodes}")
            )
          } else {
            Reads(
              _ => JsSuccess(code)
            )
          }
      }
      .andKeep(
        (__ \ "additionalInformationCoded")
          .read[String]
          .flatMap( x =>
            (__ \ "additionalInformation")
              .read[String].map(y =>
                SpecialMentionNoCountry(x, y)
            )
          )
      )
  }

  implicit lazy val writes: OWrites[SpecialMentionNoCountry] = Json.writes[SpecialMentionNoCountry]

  implicit def writesXml: XMLWrites[SpecialMentionNoCountry] = XMLWrites[SpecialMentionNoCountry] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>
          {specialMention.additionalInformation}
        </AddInfCodMT23>
        <AddInfCodMT23>
          {specialMention.additionalInformationCoded}
        </AddInfCodMT23>
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

  implicit lazy val reads: Reads[SpecialMentionGuaranteeLiabilityAmount] = {

    import play.api.libs.functional.syntax._

    (__ \ "additionalInformationCoded")
      .read[String]
      .flatMap[String] {
        case "CAL" => {
          (__ \ "additionalInformation").read[String].flatMap[String] {
            liabilityAmount =>
              Reads(
                _ => JsSuccess(liabilityAmount)
              )
          }
        }
        case _ => {
          Reads(
            _ => JsError(s"Failed to parse to SpecialMentionGuaranteeLiabilityAmount does not exist")
          )
        }
      }
      .andKeep(
        (
          (__ \ "additionalInformationCoded").read[String] and
            (__ \ "additionalInformation").read[String]
          ) (SpecialMentionGuaranteeLiabilityAmount(_, _))
      )
  }

  implicit lazy val writes: OWrites[SpecialMentionGuaranteeLiabilityAmount] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "additionalInformation").write[String] and
        (__ \ "additionalInformationCoded").write[String]
      ) (
      s => (s.additionalInformationOfLiabilityAmount, s.additionalInformationCoded)
    )
  }

  implicit def writesXml: XMLWrites[SpecialMentionGuaranteeLiabilityAmount] = XMLWrites[SpecialMentionGuaranteeLiabilityAmount] {
    specialMention =>
      <SPEMENMT2>
        <AddInfCodMT23>CAL</AddInfCodMT23>
        <AddInfMT21>
          {specialMention.additionalInformationOfLiabilityAmount}
        </AddInfMT21>
      </SPEMENMT2>
  }

}
