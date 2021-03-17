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

import base.SpecBase
import cats.data.NonEmptyList
import generators.Generators
import models.reference.OfficeOfTransit
import org.scalacheck.Arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class OfficeOfTransitListSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "getAll" - {
    "return the full list of offices of transit" in {
      forAll(nonEmptyListOf[OfficeOfTransit](10)) {
        officesOfTransit =>
          val officeOfTransitList = OfficeOfTransitList(officesOfTransit.toList)

          officeOfTransitList.getAll must contain theSameElementsAs officesOfTransit.toList
      }
    }
  }

  "getById" - {
    "return an office of transit if it exists" in {
      forAll(nonEmptyListOf[OfficeOfTransit](10)) {
        officesOfTransit =>
          val officeOfTransitList = OfficeOfTransitList(officesOfTransit.toList)

          val officeId = officesOfTransit.head.id

          officeOfTransitList.getById(officeId).value mustEqual officesOfTransit.head
      }
    }

    "return a None if it does not exists" in {

      val officesOfTransit = Seq(
        OfficeOfTransit("1", "one"),
        OfficeOfTransit("2", "two"),
        OfficeOfTransit("3", "three")
      )

      val officeOfTransitList = OfficeOfTransitList(officesOfTransit)

      val officeId: String = "4"

      officeOfTransitList.getById(officeId) mustEqual None

    }
  }

  "filter" - {
    "return a list of transit offices without the office with matching id" in {
      val officesOfTransit = Seq(
        OfficeOfTransit("1", "one"),
        OfficeOfTransit("2", "two"),
        OfficeOfTransit("3", "three"),
        OfficeOfTransit("4", "four"),
        OfficeOfTransit("5", "five")
      )

      val officeOfTransitList = OfficeOfTransitList(officesOfTransit)

      val officeIds = Seq("3")

      val expectedOffices = Seq(
        OfficeOfTransit("1", "one"),
        OfficeOfTransit("2", "two"),
        OfficeOfTransit("4", "four"),
        OfficeOfTransit("5", "five")
      )

      officeOfTransitList.filter(officeIds) must contain theSameElementsAs expectedOffices

    }

    "return a list of transit offices without the offices with matching ids" in {
      val officesOfTransit = Seq(
        OfficeOfTransit("1", "one"),
        OfficeOfTransit("2", "two"),
        OfficeOfTransit("3", "three"),
        OfficeOfTransit("4", "four"),
        OfficeOfTransit("5", "five")
      )

      val officeOfTransitList = OfficeOfTransitList(officesOfTransit)

      val officeIds = Seq("5", "3")

      val expectedOffices = Seq(
        OfficeOfTransit("1", "one"),
        OfficeOfTransit("2", "two"),
        OfficeOfTransit("4", "four")
      )

      officeOfTransitList.filter(officeIds) must contain theSameElementsAs expectedOffices

    }

    "return the full list of transit offices when there are no offices with matching id" in {
      val officesOfTransit = Seq(
        OfficeOfTransit("1", "one"),
        OfficeOfTransit("2", "two"),
        OfficeOfTransit("3", "three"),
        OfficeOfTransit("4", "four"),
        OfficeOfTransit("5", "five")
      )

      val officeOfTransitList = OfficeOfTransitList(officesOfTransit)

      val officeIds = Seq("13")

      officeOfTransitList.filter(officeIds) must contain theSameElementsAs officesOfTransit

    }

  }

}
