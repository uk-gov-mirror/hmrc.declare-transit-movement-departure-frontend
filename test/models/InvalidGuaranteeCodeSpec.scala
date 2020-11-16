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

package models

import com.lucidchart.open.xtract.XmlReader
import generators.Generators
import models.InvalidGuaranteeCode.DefaultInvalidCode
import org.scalacheck.Gen
import org.scalacheck.Gen.const
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class InvalidGuaranteeCodeSpec extends AnyFreeSpec with Generators with ScalaCheckPropertyChecks with Matchers with OptionValues {
  "InvalidGuaranteeCode" - {
    "must read xml for single items" in {
      forAll(Gen.oneOf(InvalidGuaranteeCode.values)) {
        pointer =>
          val xml = <test>{pointer.code}</test>
          XmlReader.of[InvalidGuaranteeCode].read(xml).toOption.value mustBe pointer
      }
    }

    "must return DefaultInvalidCode" in {

      forAll(nonEmptyString suchThat (x => !InvalidGuaranteeCode.values.contains(x))) {
        string =>
          val xml = <test>{string}</test>
          XmlReader.of[InvalidGuaranteeCode].read(xml).toOption.value mustBe DefaultInvalidCode(string)
      }

    }

  }
}
