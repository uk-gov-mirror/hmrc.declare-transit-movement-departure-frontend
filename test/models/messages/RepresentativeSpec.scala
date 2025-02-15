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

package models.messages

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import xml.XMLWrites._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class RepresentativeSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with MessagesModelGenerators
    with StreamlinedXmlEquality
    with OptionValues {

  "RepresentativeSpec" - {

    "must serialize Representative to xml" in {
      forAll(arbitrary[Representative]) {
        representative =>
          val representativeCapacity = representative.repCapREP18.map(
            value => <RepCapREP18>{value}</RepCapREP18>
          )

          val expectedResult =
            <REPREP>
              <NamREP5>{escapeXml(representative.namREP5)}</NamREP5>
              {representativeCapacity.getOrElse(NodeSeq.Empty)}
              <RepCapREP18LNG>EN</RepCapREP18LNG>
            </REPREP>

          representative.toXml mustEqual expectedResult
      }

    }

    "must deserialize Representative from xml" in {
      forAll(arbitrary[Representative]) {
        data =>
          val xml    = data.toXml
          val result = XmlReader.of[Representative].read(xml).toOption.value
          result mustBe data
      }
    }
  }
}
