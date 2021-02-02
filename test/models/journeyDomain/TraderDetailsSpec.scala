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
import models.journeyDomain.TraderDetails.{PersonalInformation, RequiredDetails, TraderEori, TraderInformation}
import models.{ConsigneeAddress, ConsignorAddress, EoriNumber, PrincipalAddress, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages.{ConsignorEoriPage, _}

class TraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {
  import TraderDetailsSpec._

  "TraderDetail can be parser from UserAnswers" - {
    "when there is a principal trader eori details only" in {
      forAll(arb[UserAnswers], arb[EoriNumber]) {
        (baseUserAnswers, eori) =>
          val userAnswers = setTraderDetailsPrincipalEoriOnly(eori)(baseUserAnswers)

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result mustEqual TraderDetails(TraderEori(eori), None, None)

      }
    }

    "when there is a principal trader name and address only" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
        case (baseUserAnswers, name, principalAddress) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, false)
            .success
            .value
            .set(PrincipalNamePage, name)
            .success
            .value
            .set(PrincipalAddressPage, principalAddress)
            .success
            .value
            .set(AddConsigneePage, false)
            .success
            .value
            .set(AddConsignorPage, false)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          val expectedAddress = Address.prismAddressToPrincipalAddress(principalAddress)

          result mustEqual TraderDetails(PersonalInformation(name, expectedAddress), None, None)

      }
    }

    "when there is consignor name, address and eori" in {
      forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
        case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, eoriNumber)
            .success
            .value
            .set(AddConsigneePage, false)
            .success
            .value
            .set(AddConsignorPage, true)
            .success
            .value
            .set(IsConsignorEoriKnownPage, true)
            .success
            .value
            .set(ConsignorEoriPage, eoriNumber)
            .success
            .value
            .set(ConsignorNamePage, name)
            .success
            .value
            .set(ConsignorAddressPage, address)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

          result.consignor.value mustEqual TraderInformation(name, expectedAddress, Some(EoriNumber(eoriNumber)))

      }
    }

    "when there is consignee name, address and eori" in {
      forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
        case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, eoriNumber)
            .success
            .value
            .set(AddConsignorPage, false)
            .success
            .value
            .set(AddConsigneePage, true)
            .success
            .value
            .set(IsConsigneeEoriKnownPage, true)
            .success
            .value
            .set(WhatIsConsigneeEoriPage, eoriNumber)
            .success
            .value
            .set(ConsigneeNamePage, name)
            .success
            .value
            .set(ConsigneeAddressPage, address)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          val expectedAddress: Address = Address.prismAddressToConsigneeAddress(address)

          result.consignee.value mustEqual TraderInformation(name, expectedAddress, Some(EoriNumber(eoriNumber)))

      }
    }

    "when there is a TraderDetails" in {
      forAll(arb[UserAnswers], arb[TraderDetails]) {
        (baseUserAnswers, traderDetails) =>
          val userAnswers = setTraderDetails(traderDetails)(baseUserAnswers)

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result mustEqual traderDetails
      }
    }

  }
}

object TraderDetailsSpec extends UserAnswersSpecHelper {

  def setTraderDetails(traderDetails: TraderDetails)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
      .unsafeSetVal(IsPrincipalEoriKnownPage)(traderDetails.principalTraderDetails.isInstanceOf[TraderEori])
      .unsafeSetPFn(WhatIsPrincipalEoriPage)(traderDetails.principalTraderDetails)({
        case TraderEori(eori) => eori.value
      })
      .unsafeSetPFn(PrincipalNamePage)(traderDetails.principalTraderDetails)({
        case PersonalInformation(name, _) => name
      })
      .unsafeSetPFn(PrincipalAddressPage)(traderDetails.principalTraderDetails)({
        case PersonalInformation(_, address) => Address.prismAddressToPrincipalAddress.getOption(address).get
      })
      // Set Consignor
      .unsafeSetVal(AddConsignorPage)(traderDetails.consignor.isDefined)
      .unsafeSetPFn(IsConsignorEoriKnownPage)(traderDetails.consignor)({
        case Some(TraderInformation(_, _, Some(_))) => true
        case Some(_)                                => false
      })
      .unsafeSetPFn(ConsignorEoriPage)(traderDetails.consignor)({
        case Some(TraderInformation(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(ConsignorNamePage)(traderDetails.consignor)({
        case Some(TraderInformation(name, _, _)) => name
      })
      .unsafeSetPFn(ConsignorAddressPage)(traderDetails.consignor)({
        case Some(TraderInformation(_, address, _)) => Address.prismAddressToConsignorAddress.getOption(address).get
      })
      // Set Consignee
      .unsafeSetVal(AddConsigneePage)(traderDetails.consignee.isDefined)
      .unsafeSetPFn(IsConsigneeEoriKnownPage)(traderDetails.consignee)({
        case Some(TraderInformation(_, _, Some(_))) => true
        case Some(_)                                => false
      })
      .unsafeSetPFn(WhatIsConsigneeEoriPage)(traderDetails.consignee)({
        case Some(TraderInformation(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(ConsigneeNamePage)(traderDetails.consignee)({
        case Some(TraderInformation(name, _, _)) => name
      })
      .unsafeSetPFn(ConsigneeAddressPage)(traderDetails.consignee)({
        case Some(TraderInformation(_, address, _)) => Address.prismAddressToConsigneeAddress.getOption(address).get
      })

  def setTraderDetailsPrincipalEoriOnly(eoriNumber: EoriNumber)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
      .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
      .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber.value)
      .unsafeSetVal(AddConsigneePage)(false)
      .unsafeSetVal(AddConsignorPage)(false)
}
