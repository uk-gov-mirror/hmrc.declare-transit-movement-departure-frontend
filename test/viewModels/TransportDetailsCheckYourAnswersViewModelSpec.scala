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

package viewModels

import base.SpecBase
import models.{CountryList, TransportModeList}
import models.reference.{Country, CountryCode, TransportMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{InlandModePage, ModeCrossingBorderPage}
import uk.gov.hmrc.viewmodels.Text.Literal

class TransportDetailsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  val countryList = new CountryList(Seq(Country(CountryCode("FR"), "France")))

  val transportMode1: TransportMode = TransportMode("1", "crossing border")
  val transportMode2: TransportMode = TransportMode("2", "inland mode")
  val transportModes:TransportModeList = TransportModeList(Seq(transportMode1, transportMode2))

  "TransportDetailsCheckYourAnswersViewModel" - {

    //TODO: Need to add tests to cover all potential rows

    "display modeCrossingBorder" in {

      val updatedAnswers  = emptyUserAnswers.set(ModeCrossingBorderPage, "1").success.value
        .set(InlandModePage, "2").success.value

      val data = TransportDetailsCheckYourAnswersViewModel(updatedAnswers, countryList, transportModes)

      data.sections.length mustBe 1

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 2
      data.sections.head.rows(0).value.content mustEqual Literal("inland mode")
      data.sections.head.rows(1).value.content mustEqual Literal("crossing border")
    }

  }

}
