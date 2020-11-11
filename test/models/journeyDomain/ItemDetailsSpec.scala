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
import pages.addItems.CommodityCodePage
import pages.{AddTotalNetMassPage, IsCommodityCodeKnownPage, ItemDescriptionPage, ItemTotalGrossMassPage, QuestionPage, TotalNetMassPage}

class ItemDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "ItemDetails" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      ItemDescriptionPage(index),
      ItemTotalGrossMassPage(index),
      AddTotalNetMassPage(index),
      IsCommodityCodeKnownPage(index)
    )

    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[ItemDetails], arbitrary[UserAnswers]) {
          case (itemDetails, userAnswers) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
            val result             = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.value mustEqual itemDetails
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
    }
  }
}

object ItemDetailsSpec {
  //format off

  def setItemDetailsUserAnswers(itemDetails: ItemDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val userAnswers =
      startUserAnswers
        .set(ItemDescriptionPage(index), itemDetails.itemDescription)
        .toOption
        .get
        .set(ItemTotalGrossMassPage(index), itemDetails.totalGrossMass)
        .toOption
        .get
        .set(AddTotalNetMassPage(index), itemDetails.addNetMass)
        .toOption
        .get
        .set(IsCommodityCodeKnownPage(index), itemDetails.isCommodityCodeKnow)
        .toOption
        .get

    val totalNetMass = itemDetails.totalNetMass match {
      case Some(value) => userAnswers.set(TotalNetMassPage(index), value).toOption.get
      case _           => userAnswers
    }

    val commodityCode = itemDetails.commodityCode match {
      case Some(value) => totalNetMass.set(CommodityCodePage(index), value).toOption.get
      case _           => totalNetMass
    }

    commodityCode

    //format off
  }
}
