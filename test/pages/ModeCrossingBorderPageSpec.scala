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

package pages

import models.reference.CountryCode
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.{OptionValues, TryValues}
import pages.behaviours.PageBehaviours

class ModeCrossingBorderPageSpec extends PageBehaviours with TryValues with OptionValues {

  "ModeCrossingBorderPage" - {

    beRetrievable[String](ModeCrossingBorderPage)

    beSettable[String](ModeCrossingBorderPage)

    beRemovable[String](ModeCrossingBorderPage)

    "must clear IdCrossingBorder and Nationality Crossing Border Page if 2, 20, 5, 50, 7 or 70 is selected" in {
      val crossingMode = Gen.oneOf("2", "20", "5", "50", "7", "70").sample.value
      val userAnswers  = new UserAnswers(LocalReferenceNumber("AB123").get, EoriNumber("3242343"))

      val updatedAnswers = (for {
        a <- userAnswers.set(NationalityCrossingBorderPage, CountryCode("JJ"))
        b <- a.set(IdCrossingBorderPage, "23")
      } yield b).success.value

      updatedAnswers.get(NationalityCrossingBorderPage).value mustBe CountryCode("JJ")
      updatedAnswers.get(IdCrossingBorderPage).value mustBe "23"
      updatedAnswers.get(ModeCrossingBorderPage) mustBe None

      val newUpdateAnswers = updatedAnswers.set(ModeCrossingBorderPage, crossingMode).success.value

      newUpdateAnswers.get(NationalityCrossingBorderPage) mustBe None
      newUpdateAnswers.get(IdCrossingBorderPage) mustBe None
      newUpdateAnswers.get(ModeCrossingBorderPage).value mustBe crossingMode
    }

    "must not clear IdCrossingBorder and Nationality Crossing Border Page if 2, 20, 5, 50, 7 or 70 is selected" in {
      val invalidModes = Set("2", "20", "5", "50", "7", "70")
      val crossingMode = Gen.numStr.suchThat(x => !invalidModes(x)).sample.value
      val userAnswers  = new UserAnswers(LocalReferenceNumber("AB123").get, EoriNumber("3242343"))

      val updatedAnswers = (for {
        a <- userAnswers.set(NationalityCrossingBorderPage, CountryCode("JJ"))
        b <- a.set(IdCrossingBorderPage, "23")
      } yield b).success.value

      updatedAnswers.get(NationalityCrossingBorderPage).value mustBe CountryCode("JJ")
      updatedAnswers.get(IdCrossingBorderPage).value mustBe "23"
      updatedAnswers.get(ModeCrossingBorderPage) mustBe None

      val newUpdateAnswers = updatedAnswers.set(ModeCrossingBorderPage, crossingMode).success.value

      newUpdateAnswers.get(NationalityCrossingBorderPage).value mustBe CountryCode("JJ")
      newUpdateAnswers.get(IdCrossingBorderPage).value mustBe "23"
      newUpdateAnswers.get(ModeCrossingBorderPage).value mustBe crossingMode
    }
  }
}
