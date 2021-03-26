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

  "Parsing PrincipalTrader from UserAnswers" - {

    "when procedure type is Normal" - {

      "when Eori is known has been answered" - {

        "when Eori is answered" in {
          forAll(arb[UserAnswers], arb[EoriNumber]) {
            (baseUserAnswers, eori) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
              //                  .unsafeSetVal(AddConsigneePage)(false)
              //                  .unsafeSetVal(AddConsignorPage)(false)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).value

              result mustEqual PrincipalTraderEoriInfo(eori)

          }
        }

        "when Eori is missing" in {
          forAll(arb[UserAnswers]) {
            baseUserAnswers =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                //                  .unsafeSetVal(AddConsigneePage)(false)
                //                  .unsafeSetVal(AddConsignorPage)(false)
                .unsafeRemove(WhatIsPrincipalEoriPage)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers)

              result mustEqual None

          }

        }
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
              //                  .unsafeSetVal(AddConsigneePage)(false)
              //                  .unsafeSetVal(AddConsignorPage)(false)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).value

              val expectedAddress = Address.prismAddressToPrincipalAddress(principalAddress)

              result mustEqual PrincipalTraderDetails(name, expectedAddress)

          }
        }

        "when address is missing" in {
          forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
            case (baseUserAnswers, name, principalAddress) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeSetVal(PrincipalNamePage)(name)
                //                  .unsafeSetVal(AddConsigneePage)(false)
                //                  .unsafeSetVal(AddConsignorPage)(false)
                .unsafeRemove(PrincipalAddressPage)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers)

              result mustEqual None

          }

        }

        "when name is missing" in {
          forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
            case (baseUserAnswers, name, principalAddress) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeSetVal(PrincipalAddressPage)(principalAddress)
                //                  .unsafeSetVal(AddConsigneePage)(false)
                //                  .unsafeSetVal(AddConsignorPage)(false)
                .unsafeRemove(PrincipalNamePage)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers)

              result mustEqual None

          }

        }
      }

      "when Principal Eori known page is missing" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[EoriNumber], arb[PrincipalAddress]) {
          case (baseUserAnswers, name, eori, principalAddress) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
              .unsafeSetVal(PrincipalNamePage)(name)
              .unsafeSetVal(PrincipalAddressPage)(principalAddress)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
              //                .unsafeSetVal(AddConsigneePage)(false)
              //                .unsafeSetVal(AddConsignorPage)(false)
              .unsafeRemove(IsPrincipalEoriKnownPage)

            val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers)

            result mustEqual None

        }

      }
    }

    "when procedure type is Simplified" - {

      "when Eori is answered" in {
        forAll(arb[UserAnswers], arb[EoriNumber]) {
          (baseUserAnswers, eori) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
            //                .unsafeSetVal(AddConsigneePage)(false)
            //                .unsafeSetVal(AddConsignorPage)(false)

            val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).value

            result mustEqual PrincipalTraderEoriInfo(eori)

        }
      }

      "when Eori is missing" in {
        forAll(arb[UserAnswers]) {
          baseUserAnswers =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
              .unsafeRemove(WhatIsPrincipalEoriPage)
//                .unsafeSetVal(AddConsigneePage)(false)
//                .unsafeSetVal(AddConsignorPage)(false)

            val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers)

            result mustEqual None

        }

      }
    }

    "when procedure type is missing" in {
      forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
        (baseUserAnswers, eori, name, principalAddress) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
            //              .unsafeSetVal(AddConsigneePage)(false)
            //              .unsafeSetVal(AddConsignorPage)(false)
            .unsafeSetVal(PrincipalNamePage)(name)
            .unsafeSetVal(PrincipalAddressPage)(principalAddress)
            .unsafeRemove(ProcedureTypePage)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers)

          result mustEqual None

      }

    }

  }

  "Parsing ConsignorDetails from UserAnswers" - {

    "when the eori is known" - {
      "when name, address and eori are answered" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
            val userAnswers = baseUserAnswers
            //              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            //              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            //              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
            //              .unsafeSetVal(AddConsigneePage)(false)
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeSetVal(ConsignorAddressPage)(address)

            val result = UserAnswersReader[Option[ConsignorDetails]].run(userAnswers).value

            val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

            result.value mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

        }
      }

      "when name and eori are answered but address is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
              .unsafeSetVal(AddConsigneePage)(false)
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeRemove(ConsignorAddressPage)

            val result = UserAnswersReader[Option[ConsignorDetails]].run(userAnswers)

            result mustEqual None

        }
      }

      "when address and eori are answered but name is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], arb[ConsignorAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
              .unsafeSetVal(AddConsigneePage)(false)
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
              .unsafeSetVal(ConsignorAddressPage)(address)
              .unsafeRemove(ConsignorNamePage)

            val result = UserAnswersReader[Option[ConsignorDetails]].run(userAnswers)

            result mustEqual None
        }
      }

      "when name and address are answered but eori is missing" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
          case (baseUserAnswers, name, address) =>
            val userAnswers = baseUserAnswers
//              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//              .unsafeSetVal(AddConsigneePage)(false)
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeSetVal(ConsignorAddressPage)(address)
              .unsafeRemove(ConsignorEoriPage)

            val result = UserAnswersReader[Option[ConsignorDetails]].run(userAnswers)

            result mustEqual None

        }
      }
    }

    "when the eori is not known" - {
      "when name, address and eori are answered" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
            val userAnswers = baseUserAnswers
//                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
//                .unsafeSetVal(AddConsigneePage)(false)
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(false)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeSetVal(ConsignorAddressPage)(address)

            val result = UserAnswersReader[Option[ConsignorDetails]].run(userAnswers).value

            val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

            result.value mustEqual ConsignorDetails(name, expectedAddress, None)

        }
      }

      "when name is answered but address is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name) =>
            val userAnswers = baseUserAnswers
//                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
//                .unsafeSetVal(AddConsigneePage)(false)
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(false)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeRemove(ConsignorAddressPage)

            val result = UserAnswersReader[Option[ConsignorDetails]].run(userAnswers)

            result mustEqual None

        }
      }

      "when address is answered but name is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], arb[ConsignorAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), address) =>
            val userAnswers = baseUserAnswers
//                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//                .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
//                .unsafeSetVal(AddConsigneePage)(false)
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
              .unsafeSetVal(ConsignorAddressPage)(address)
              .unsafeRemove(ConsignorNamePage)
              .unsafeRemove(ConsignorEoriPage)

            val result = UserAnswersReader[Option[ConsignorDetails]].run(userAnswers)

            result mustEqual None

        }
      }
    }

  }

  "Parsing ConsigneeDetails from UserAnswers" - {
    "when the eori is known" - {
      "when there is consignee name, address and eori" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
            val userAnswers = baseUserAnswers
//              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
//              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(WhatIsConsigneeEoriPage)(eoriNumber)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeSetVal(ConsigneeAddressPage)(address)

            val result = UserAnswersReader[Option[ConsigneeDetails]].run(userAnswers).value

            val expectedAddress: Address = Address.prismAddressToConsigneeAddress(address)

            result.value mustEqual ConsigneeDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

        }
      }

      "when name and eori are answered but address is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name) =>
            val userAnswers = baseUserAnswers
//              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
//              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(WhatIsConsigneeEoriPage)(eoriNumber)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeRemove(ConsigneeAddressPage)

            val result = UserAnswersReader[Option[ConsigneeDetails]].run(userAnswers)

            result mustEqual None
        }
      }

      "when address and eori are answered but name is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], arb[ConsigneeAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), address) =>
            val userAnswers = baseUserAnswers
//              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
//              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(WhatIsConsigneeEoriPage)(eoriNumber)
              .unsafeSetVal(ConsigneeAddressPage)(address)
              .unsafeRemove(ConsigneeNamePage)

            val result = UserAnswersReader[Option[ConsigneeDetails]].run(userAnswers)

            result mustEqual None
        }

      }

      "when name and address are answered but eori is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
            val userAnswers = baseUserAnswers
//              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
//              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
//              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
//              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeSetVal(ConsigneeAddressPage)(address)
              .unsafeRemove(WhatIsConsigneeEoriPage)

            val result = UserAnswersReader[Option[ConsigneeDetails]].run(userAnswers)

            result mustEqual None
        }

      }
    }

    "when the eori is not known" - {
      "when there is consignee nameaddress" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeSetVal(ConsigneeAddressPage)(address)

            val result = UserAnswersReader[Option[ConsigneeDetails]].run(userAnswers).value

            val expectedAddress: Address = Address.prismAddressToConsigneeAddress(address)

            result.value mustEqual ConsigneeDetails(name, expectedAddress, None)

        }
      }

      "when name is answered but address is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeRemove(ConsigneeAddressPage)

            val result = UserAnswersReader[Option[ConsigneeDetails]].run(userAnswers)

            result mustEqual None
        }
      }

      "when address is answered but name is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], arb[ConsigneeAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber)
              .unsafeSetVal(AddConsignorPage)(false)
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
              .unsafeSetVal(ConsigneeAddressPage)(address)
              .unsafeRemove(ConsigneeNamePage)

            val result = UserAnswersReader[Option[ConsigneeDetails]].run(userAnswers)

            result mustEqual None
        }
      }
    }
  }

  "Parsing TraderDetail from UserAnswers" - {
    "for Normal" in {
      forAll(arb[UserAnswers], genTraderDetailsNormal) {
        (baseUserAnswers, traderDetails) =>
          val userAnswers = setTraderDetails(traderDetails)(
            baseUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
          )

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result mustEqual traderDetails
      }
    }

    "for Simplified" in {
      forAll(arb[UserAnswers], genTraderDetailsSimplified) {
        (baseUserAnswers, traderDetails) =>
          val userAnswers = setTraderDetails(traderDetails)(
            baseUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
          )

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result mustEqual traderDetails
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
      .unsafeSetPFn(IsConsignorEoriKnownPage)(traderDetails.consignor)({
        case Some(ConsignorDetails(_, _, eoriOpt)) => eoriOpt.isDefined
      })
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
      .unsafeSetPFn(IsConsigneeEoriKnownPage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(_, _, eoriOpt)) => eoriOpt.isDefined
      })
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
