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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.domain.Address
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.{ConsigneeAddress, ConsignorAddress, EoriNumber, Index, UserAnswers}
import org.scalatest.TryValues
import pages._
import pages.addItems.traderDetails._

class ItemTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {

  "ItemTraderDetail can be parsed from UserAnswers" - {

    "when there is no consignor eoriNumber" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
        case (baseUserAnswers, name, address) =>
          val userAnswers = baseUserAnswers
            .set(AddConsignorPage, false)
            .success
            .value
            .set(ConsignorForAllItemsPage, false)
            .success
            .value
            .set(TraderDetailsConsignorEoriKnownPage(index), false)
            .success
            .value
            .set(TraderDetailsConsignorNamePage(index), name)
            .success
            .value
            .set(TraderDetailsConsignorAddressPage(index), address)
            .success
            .value
            .set(TraderDetailsConsignorEoriKnownPage(index), false)
            .success
            .value

          val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)
          val result                   = UserAnswersParser[Option, ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).value

          result.consignor.value mustEqual RequiredDetails(name, expectedAddress, None)
      }
    }

    "when there is consignor name, address and eori" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress], arb[EoriNumber]) {
        case (baseUserAnswers, name, address, eori @ EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .set(AddConsignorPage, false)
            .success
            .value
            .set(ConsignorForAllItemsPage, false)
            .success
            .value
            .set(TraderDetailsConsignorNamePage(index), name)
            .success
            .value
            .set(TraderDetailsConsignorAddressPage(index), address)
            .success
            .value
            .set(TraderDetailsConsignorEoriKnownPage(index), true)
            .success
            .value
            .set(TraderDetailsConsignorEoriNumberPage(index), eoriNumber1)
            .success
            .value

          val result = UserAnswersParser[Option, ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).value

          val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

          result.consignor.value mustEqual RequiredDetails(name, expectedAddress, Some(eori))
      }
    }

    "when there is no consignee eori only name and address" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
        case (baseUserAnswers, name, address) =>
          val userAnswers = baseUserAnswers
            .set(AddConsigneePage, false)
            .success
            .value
            .set(ConsigneeForAllItemsPage, false)
            .success
            .value
            .set(TraderDetailsConsigneeNamePage(index), name)
            .success
            .value
            .set(TraderDetailsConsigneeAddressPage(index), address)
            .success
            .value
            .set(TraderDetailsConsigneeEoriKnownPage(index), false)
            .success
            .value

          val result                   = UserAnswersParser[Option, ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).value
          val expectedAddress: Address = Address.prismAddressToConsigneeAddress(address)

          result.consignee.value mustEqual RequiredDetails(name, expectedAddress, None)

      }
    }

    "when there is consignee name, address and eori" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress], arb[EoriNumber]) {
        case (baseUserAnswers, name, address, eori @ EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .set(AddConsigneePage, false)
            .success
            .value
            .set(ConsigneeForAllItemsPage, false)
            .success
            .value
            .set(TraderDetailsConsigneeEoriKnownPage(index), true)
            .success
            .value
            .set(TraderDetailsConsigneeEoriNumberPage(index), eoriNumber1)
            .success
            .value
            .set(TraderDetailsConsigneeNamePage(index), name)
            .success
            .value
            .set(TraderDetailsConsigneeAddressPage(index), address)
            .success
            .value

          val result = UserAnswersParser[Option, ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).value

          val expectedAddress: Address = Address.prismAddressToConsigneeAddress(address)

          result.consignee.value mustEqual RequiredDetails(name, expectedAddress, Some(eori))
      }
    }

    "when header level consignor has already been answered" in {
      forAll(arb[UserAnswers], arb[EoriNumber]) {
        case (baseUserAnswers, _ @EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .set(AddConsignorPage, true)
            .success
            .value
            .set(ConsignorForAllItemsPage, true)
            .success
            .value
            .set(TraderDetailsConsignorEoriKnownPage(index), true)
            .success
            .value
            .set(TraderDetailsConsignorEoriNumberPage(index), eoriNumber1)
            .success
            .value

          val result = UserAnswersParser[Option, ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).value

          result.consignor must be(None)

      }
    }

    "when header level consignee has already been answered" in {
      forAll(arb[UserAnswers], arb[EoriNumber]) {
        case (baseUserAnswers, eori1 @ EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .set(AddConsigneePage, true)
            .success
            .value
            .set(ConsigneeForAllItemsPage, true)
            .success
            .value
            .set(TraderDetailsConsigneeEoriKnownPage(index), true)
            .success
            .value
            .set(TraderDetailsConsigneeEoriNumberPage(index), eoriNumber1)
            .success
            .value

          val result = UserAnswersParser[Option, ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).value

          result.consignee must be(None)
      }
    }
  }
}

object ItemTraderDetailsSpec extends UserAnswersSpecHelper {

  def setItemTraderDetails(itemTraderDetails: ItemTraderDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
    // Set Consignor
      .unsafeSetVal(ConsignorForAllItemsPage)(false)
      .unsafeSetVal(AddConsignorPage)(false)
      .unsafeSetPFn(TraderDetailsConsignorEoriKnownPage(index))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(_, _, Some(_))) => true
        case Some(_)                              => false
      })
      .unsafeSetPFn(TraderDetailsConsignorEoriNumberPage(index))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(TraderDetailsConsignorNamePage(index))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(name, _, _)) => name
      })
      .unsafeSetPFn(TraderDetailsConsignorAddressPage(index))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(_, address, _)) => Address.prismAddressToConsignorAddress.getOption(address).get
      })
      // Set Consignee
      .unsafeSetVal(ConsigneeForAllItemsPage)(false)
      .unsafeSetVal(AddConsigneePage)(false)
      .unsafeSetPFn(TraderDetailsConsigneeEoriKnownPage(index))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(_, _, Some(_))) => true
        case Some(_)                              => false
      })
      .unsafeSetPFn(TraderDetailsConsigneeEoriNumberPage(index))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(TraderDetailsConsigneeNamePage(index))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(name, _, _)) => name
      })
      .unsafeSetPFn(TraderDetailsConsigneeAddressPage(index))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(_, address, _)) => Address.prismAddressToConsigneeAddress.getOption(address).get
      })

}
