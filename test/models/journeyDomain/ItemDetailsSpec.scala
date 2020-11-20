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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import generators.JourneyModelGenerators
import models.journeyDomain.ItemDetailsSpec.setItemDetailsUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.addItems.{AddTotalNetMassPage, CommodityCodePage, IsCommodityCodeKnownPage, ItemDescriptionPage, ItemTotalGrossMassPage}
import pages.{addItems, QuestionPage, TotalNetMassPage}

class ItemDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "ItemDetails" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      ItemDescriptionPage(index),
      ItemTotalGrossMassPage(index)
    )

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[ItemDetails], arbitrary[UserAnswers]) {
          case (itemDetails, userAnswers) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
            val result             = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.value mustEqual itemDetails
        }
      }

      "when addTotalNetMass is false and totalNetMass has been answered, totNetMass should be none" in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(totalNetMass = Some("totalNetMassValue")))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .set(AddTotalNetMassPage(index), false)
              .toOption
              .get
              .set(TotalNetMassPage(index), itemDetails.totalNetMass.value)
              .toOption
              .get

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.value.totalNetMass mustEqual None
        }
      }

      "when IsCommodityCodeKnownPage is false and CommodityCodePage has been answered, CommodityCodePage should be none" in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(commodityCode = Some("totalNetMassValue")))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .set(IsCommodityCodeKnownPage(index), false)
              .toOption
              .get
              .set(CommodityCodePage(index), itemDetails.commodityCode.value)
              .toOption
              .get

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.value.commodityCode mustEqual None
        }
      }

    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(arbitrary[UserAnswers], mandatoryPages) {
          case (userAnswers, mandatoryPage) =>
            val updatedUserAnswers = userAnswers.remove(mandatoryPage).success.value
            val result             = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result mustEqual None
        }
      }

      "when addTotalNetMass is true but totalNetMass is missing " in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(totalNetMass = None))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .set(addItems.AddTotalNetMassPage(index), true)
              .toOption
              .get

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result mustEqual None
        }
      }

      "when IsCommodityCodeKnownPage is true but CommodityCodePage is missing " in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(commodityCode = None))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .set(addItems.IsCommodityCodeKnownPage(index), true)
              .toOption
              .get

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result mustEqual None
        }
      }

    }
  }
}

object ItemDetailsSpec {
  //format off

  def setItemDetailsUserAnswers(itemDetails: ItemDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val userAnswers =
      startUserAnswers
        .set(addItems.ItemTotalGrossMassPage(index), itemDetails.totalGrossMass)
        .toOption
        .get
        .set(addItems.ItemDescriptionPage(index), itemDetails.itemDescription)
        .toOption
        .get

    val totalNetMass = itemDetails.totalNetMass match {
      case Some(value) =>
        userAnswers
          .set(addItems.AddTotalNetMassPage(index), true)
          .toOption
          .get
          .set(TotalNetMassPage(index), value)
          .toOption
          .get

      case _ =>
        userAnswers
          .set(addItems.AddTotalNetMassPage(index), false)
          .toOption
          .get
    }

    val commodityCode = itemDetails.commodityCode match {
      case Some(value) =>
        totalNetMass
          .set(addItems.IsCommodityCodeKnownPage(index), true)
          .toOption
          .get
          .set(CommodityCodePage(index), value)
          .toOption
          .get
      case _ =>
        totalNetMass
          .set(addItems.IsCommodityCodeKnownPage(index), false)
          .toOption
          .get
    }

    commodityCode

    //format off
  }
}
