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

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.XMLWrites
import models.XMLWrites._
import models.messages.trader._

import scala.xml.{Elem, Node, NodeSeq}

case class DeclarationRequest(meta: Meta,
                              header: Header,
                              traderPrincipal: TraderPrincipal,
                              traderConsignor: Option[TraderConsignor],
                              traderConsignee: Option[TraderConsignee])

object DeclarationRequest {

  implicit def writes: XMLWrites[DeclarationRequest] = XMLWrites[DeclarationRequest] {
    declarationRequest =>
      val parentNode: Node = <CC015B></CC015B>

      val childNodes: NodeSeq = {
        declarationRequest.meta.toXml ++
          declarationRequest.header.toXml ++
          traderPrinciple(declarationRequest.traderPrincipal) ++
          declarationRequest.traderConsignor.map(_.toXml).getOrElse(NodeSeq.Empty) ++
          declarationRequest.traderConsignee.map(_.toXml).getOrElse(NodeSeq.Empty)
      } //TODO: This needs more xml nodes adding as models become available

      Elem(parentNode.prefix, parentNode.label, parentNode.attributes, parentNode.scope, parentNode.child.isEmpty, parentNode.child ++ childNodes: _*)
  }

  private def traderPrinciple(traderPrincipal: TraderPrincipal): NodeSeq = traderPrincipal match {
    case traderPrincipalWithEori: TraderPrincipalWithEori       => traderPrincipalWithEori.toXml
    case traderPrincipalWithoutEori: TraderPrincipalWithoutEori => traderPrincipalWithoutEori.toXml
    case _                                                      => NodeSeq.Empty
  }

  implicit val reads: XmlReader[DeclarationRequest] =
    (__.read[Meta],
     (__ \ "HEAHEA").read[Header],
     (__ \ "TRAPRIPC1").read[TraderPrincipal],
     (__ \ "TRACONCO1").read[TraderConsignor].optional,
     (__ \ "TRACONCE1").read[TraderConsignee].optional) mapN apply
}
