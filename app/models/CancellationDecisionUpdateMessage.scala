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

package models

import java.time.LocalDate
import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import play.api.libs.json.{Json, OWrites}
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import models.XMLReads._

case class CancellationDecisionUpdateMessage(mrn: String,
                                             cancellationRequestDate: Option[LocalDate],
                                             cancellationInitiatedBy: Int,
                                             cancellationDecision: Option[Int],
                                             cancellationDecisionDate: LocalDate,
                                             cancellationJustification: Option[String])

object CancellationDecisionUpdateMessage {

  implicit val writes: OWrites[CancellationDecisionUpdateMessage] = Json.writes[CancellationDecisionUpdateMessage]

  implicit val xmlReader: XmlReader[CancellationDecisionUpdateMessage] = (
    (__ \ "HEAHEA" \ "DocNumHEA5").read[String],
    (__ \ "HEAHEA" \ "DatOfCanReqHEA147").read[LocalDate].optional,
    (__ \ "HEAHEA" \ "CanIniByCusHEA94").read[Int],
    (__ \ "HEAHEA" \ "CanDecHEA93").read[Int].optional,
    (__ \ "HEAHEA" \ "DatOfCanDecHEA146").read[LocalDate],
    (__ \ "HEAHEA" \ "CanJusHEA248").read[String].optional
  ).mapN(apply)
}
