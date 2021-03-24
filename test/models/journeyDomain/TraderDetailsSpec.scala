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
import models.ProcedureType.Simplified
import models.domain.Address
import models.journeyDomain.TraderDetails._
import models.{ConsigneeAddress, ConsignorAddress, EoriNumber, PrincipalAddress, ProcedureType, UserAnswers}
import org.scalatest.TryValues
import pages.{ConsignorEoriPage, _}

class TraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {
  import TraderDetailsSpec._

  "TraderDetail can be parser from UserAnswers" - {
    "Principal trader details" - {

      "when procedure type is Normal" - {

        "when Eori is known" - {

          "when Eori is answered" in {
            forAll(arb[UserAnswers], arb[EoriNumber]) {
              (baseUserAnswers, eori) =>
                val userAnswers = baseUserAnswers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
                  .unsafeSetVal(AddConsigneePage)(false)
                  .unsafeSetVal(AddConsignorPage)(false)

                val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

                result mustEqual TraderDetails(PrincipalTraderEoriInfo(eori), None, None)

            }
          }

          "when Eori is missing" ignore {}
        }

        "when Eori is not known" - {

          "when principal trader name and address are answered" in {
            forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
              case (baseUserAnswers, name, principalAddress) =>
                val userAnswers = baseUserAnswers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                  .unsafeSetVal(PrincipalNamePage)(name)
                  .unsafeSetVal(PrincipalAddressPage)(principalAddress)
                  .unsafeSetVal(AddConsigneePage)(false)
                  .unsafeSetVal(AddConsignorPage)(false)

                val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

                val expectedAddress = Address.prismAddressToPrincipalAddress(principalAddress)

                result mustEqual TraderDetails(PrincipalTraderDetails(name, expectedAddress), None, None)

            }
          }

          "when address is missing" ignore {}

          "when name is missing" ignore {}
        }

        "when Principal Eori known page is missing" ignore {}
      }

      "when procedure type is Simplified" - {

        "when Eori is answered" in {
          forAll(arb[UserAnswers], arb[EoriNumber]) {
            (baseUserAnswers, eori) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(AddConsignorPage)(false)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              result mustEqual TraderDetails(PrincipalTraderEoriInfo(eori), None, None)

          }
        }

        "when Eori is missing" ignore {}
      }

    }

    "Consignor trader details" - {

      "when the eori is known" - {
        "when name, address and eori are answered" in {
          forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
            case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
                .unsafeSetVal(ConsignorNamePage)(name)
                .unsafeSetVal(ConsignorAddressPage)(address)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

              result.consignor.value mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

          }
        }

        // TODO: Fix test
        "when name and eori are answered but address is missing" ignore {
          forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
            case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
                .unsafeSetVal(ConsignorNamePage)(name)
                .unsafeSetVal(ConsignorAddressPage)(address)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

              result.consignor.value mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

          }
        }

        // TODO: Fix test
        "when address and eori are answered but name is missing" ignore {
          forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
            case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
                .unsafeSetVal(ConsignorNamePage)(name)
                .unsafeSetVal(ConsignorAddressPage)(address)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

              result.consignor.value mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

          }
        }

        // TODO: Fix test
        "when name and address are answered but eori is missing" ignore {
          forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
            case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
                .unsafeSetVal(ConsignorNamePage)(name)
                .unsafeSetVal(ConsignorAddressPage)(address)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

              result.consignor.value mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

          }
        }
      }

      "when the eori is not known" - {
        // TODO: Fix test
        "when name is answered but address is missing" ignore {
          forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
            case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
                .unsafeSetVal(ConsignorNamePage)(name)
                .unsafeSetVal(ConsignorAddressPage)(address)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

              result.consignor.value mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

          }
        }

        // TODO: Fix test
        "when address is answered but name is missing" ignore {
          forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
            case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
                .unsafeSetVal(ConsignorNamePage)(name)
                .unsafeSetVal(ConsignorAddressPage)(address)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

              result.consignor.value mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

          }
        }
      }

    }

    "Consignee trader details" - {
      "when the eori is known" - {
        "when there is consignee name, address and eori" in {
          forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
            case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
                .unsafeSetVal(AddConsignorPage)(false)
                .unsafeSetVal(AddConsigneePage)(true)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
                .unsafeSetVal(WhatIsConsigneeEoriPage)(eoriNumber)
                .unsafeSetVal(ConsigneeNamePage)(name)
                .unsafeSetVal(ConsigneeAddressPage)(address)

              val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

              val expectedAddress: Address = Address.prismAddressToConsigneeAddress(address)

              result.consignee.value mustEqual ConsigneeDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

          }
        }

        "when name and eori are answered but address " ignore {}

        "when address and eori are answered but name is missing" ignore {}

        "when name and address are answered but eori is missing" ignore {}
      }

      "when the eori is not known" - {
        // TODO: Fix test
        "when name is answered but address is missing" ignore {}

        // TODO: Fix test
        "when address is answered but name is missing" ignore {}
      }
    }

    "full model reads" - {
      "for Normal" ignore {
        forAll(arb[UserAnswers], genTraderDetailsNormal) {
          (baseUserAnswers, traderDetails) =>
            val userAnswers = setTraderDetails(traderDetails)(baseUserAnswers)

            val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

            result mustEqual traderDetails
        }
      }

      "for Simplified" ignore {
        forAll(arb[UserAnswers], genTraderDetailsSimplified) {
          (baseUserAnswers, traderDetails) =>
            val userAnswers = setTraderDetails(traderDetails)(baseUserAnswers)

            val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

            result mustEqual traderDetails
        }
      }
    }
  }
}

object TraderDetailsSpec extends UserAnswersSpecHelper {

  def setTraderDetails(traderDetails: TraderDetails)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
    // Set Principal Trader details
      .unsafeSetVal(IsPrincipalEoriKnownPage)(traderDetails.principalTraderDetails.isInstanceOf[PrincipalTraderEoriInfo])
      .unsafeSetPFn(WhatIsPrincipalEoriPage)(traderDetails.principalTraderDetails)({
        case PrincipalTraderEoriInfo(eori) => eori.value
      })
      .unsafeSetPFn(PrincipalNamePage)(traderDetails.principalTraderDetails)({
        case PrincipalTraderPersonalInfo(name, _) => name
      })
      .unsafeSetPFn(PrincipalAddressPage)(traderDetails.principalTraderDetails)({
        case PrincipalTraderPersonalInfo(_, address) => Address.prismAddressToPrincipalAddress.getOption(address).get
      })
      .assert("Eori must be provided for Simplified procedure") {
        ua =>
          (ua.get(ProcedureTypePage), ua.get(WhatIsPrincipalEoriPage)) match {
            case (Some(Simplified), None) => false
            case _                        => true
          }
      }
      // Set Consignor details
      .unsafeSetVal(AddConsignorPage)(traderDetails.consignor.isDefined)
      .unsafeSetVal(IsConsignorEoriKnownPage)(traderDetails.consignor.fold(false)(_.eori.isDefined))
      .unsafeSetPFn(ConsignorEoriPage)(traderDetails.consignor)({
        case Some(ConsignorDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(ConsignorNamePage)(traderDetails.consignor)({
        case Some(ConsignorDetails(name, _, _)) => name
      })
      .unsafeSetPFn(ConsignorAddressPage)(traderDetails.consignor)({
        case Some(ConsignorDetails(_, address, _)) => Address.prismAddressToConsignorAddress.getOption(address).get
      })
      // Set Consignee details
      .unsafeSetVal(AddConsigneePage)(traderDetails.consignee.isDefined)
      .unsafeSetVal(IsConsigneeEoriKnownPage)(traderDetails.consignee.fold(false)(_.eori.isDefined))
      .unsafeSetPFn(WhatIsConsigneeEoriPage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(ConsigneeNamePage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(name, _, _)) => name
      })
      .unsafeSetPFn(ConsigneeAddressPage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(_, address, _)) => Address.prismAddressToConsigneeAddress.getOption(address).get
      })

}
