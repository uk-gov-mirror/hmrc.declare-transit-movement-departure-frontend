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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import generators.JourneyModelGenerators
import models.{Index, UserAnswers}
import org.scalatest.TryValues
import pages.addItems.traderSecurityDetails.{
  AddSecurityConsigneesEoriPage,
  AddSecurityConsignorsEoriPage,
  SecurityConsignorAddressPage,
  SecurityConsignorNamePage
}

class ItemsSecurityTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {
  "ItemsSecurityTraderDetails can be parsed within user answers" - {
    "when the minimal user answers has been answered" in {

      forAll(arb[UserAnswers], arb[ItemsSecurityTraderDetails]) {
        (baseUserAnswers, itemsSecurityTraderDetails) =>
          val userAnswers = ItemsSecurityTraderDetails.setItemsSecurityTraderDetails(index, baseUserAnswers)
          val result: ItemsSecurityTraderDetails =
            UserAnswersParser[Option, ItemsSecurityTraderDetails](ItemsSecurityTraderDetails.parser(index)).run(baseUserAnswers).value

          result mustBe itemsSecurityTraderDetails
      }

    }

  }

  object ItemsSecurityTraderDetailsSpec {

    def setItemsSecurityTraderDetails(itemsSecurityDetails: ItemsSecurityTraderDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers = {
      val eori: UserAnswers = itemsSecurityDetails.consignor match {
        case Some(value) =>
          startUserAnswers
            .set(AddSecurityConsignorsEoriPage(index), false)
            .toOption
            .get
            .set(SecurityConsignorNamePage(index), "test")
            .toOption
            .get
            .set(SecurityConsignorAddressPage(index), arbitraryConsignorAddress)
            .toOption
            .get
        case None =>
          startUserAnswers
            .set(AddSecurityConsignorsEoriPage(index), true)
            .toOption
            .get
            .set(AddSecurityConsignorsEoriPage(index), "GB123456")
            .toOption
            .get
      }
      eori
    }
  }
}
