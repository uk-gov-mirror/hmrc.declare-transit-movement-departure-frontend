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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.UserAnswers
import models.domain.Address
import models.journeyDomain.PackagesSpec.UserAnswersNoErrorSet
import models.journeyDomain.SafetyAndSecurity.{PersonalInformation, TraderEori}
import models.journeyDomain.SafetyAndSecuritySpec.{setSafetyAndSecurity, setSafetyAndSecurityMinimal}
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages.ModeAtBorderPage
import pages.safetyAndSecurity._

class SafetyAndSecuritySpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {

  "SafetyAndSecurity can be parsed from UserAnswers" - {

    "when the minimal UserAnswers has been answered" in {
      forAll(arb[UserAnswers]) {
        baseUserAnswers =>
          val userAnswers = setSafetyAndSecurityMinimal(baseUserAnswers)

          val result = UserAnswersParser[Option, SafetyAndSecurity].run(userAnswers).value

          result mustEqual SafetyAndSecurity(None, None, None, None, None, None, None, None)
      }
    }

    "when the full UserAnswers has been answered and modeAtBorder is 4 or 40" in {

      val genModeAtBorder = Gen.oneOf(Seq(4, 40))

      forAll(genModeAtBorder) {
        mode =>
          forAll(arb[UserAnswers], genSecurityDetails(genModeAtBorder.map(_.toString))) {
            (baseUserAnswers, safetyAndSecurity) =>
              val updatedUserAnswers = baseUserAnswers.unsafeSetVal(ModeAtBorderPage)(mode.toString)

              val userAnswers: UserAnswers = setSafetyAndSecurity(safetyAndSecurity)(updatedUserAnswers)

              val result = UserAnswersParser[Option, SafetyAndSecurity].run(userAnswers).value

              result mustEqual safetyAndSecurity
          }
      }

    }

    "when the full UserAnswers has been answered and modeAtBorder is not 4 or 40" in {

      forAll(arb[UserAnswers], arb[SafetyAndSecurity], arb[String].suchThat(mode => mode != "4" | mode != "40")) {
        (baseUserAnswers, safetyAndSecurity, mode) =>
          val updatedUserAnswers = baseUserAnswers.unsafeSetVal(ModeAtBorderPage)(mode)

          val userAnswers = setSafetyAndSecurity(safetyAndSecurity)(updatedUserAnswers)

          val result = UserAnswersParser[Option, SafetyAndSecurity].run(userAnswers).value

          result mustEqual safetyAndSecurity
      }
    }
  }

}

object SafetyAndSecuritySpec extends UserAnswersSpecHelper {

  def setSafetyAndSecurity(safetyAndSecurity: Option[SafetyAndSecurity])(startUserAnswers: UserAnswers): UserAnswers =
    safetyAndSecurity.map(x => setSafetyAndSecurity(x)(startUserAnswers)).getOrElse(setSafetyAndSecurityMinimal(startUserAnswers))

  def setSafetyAndSecurity(safetyAndSecurity: SafetyAndSecurity)(startUserAnswers: UserAnswers): UserAnswers = {
    val ua = startUserAnswers
    // Set summary details
      .unsafeSetVal(AddCircumstanceIndicatorPage)(safetyAndSecurity.circumstanceIndicator.isDefined)
      .unsafeSetOpt(CircumstanceIndicatorPage)(safetyAndSecurity.circumstanceIndicator)
      .unsafeSetVal(AddTransportChargesPaymentMethodPage)(safetyAndSecurity.paymentMethod.isDefined)
      .unsafeSetOpt(TransportChargesPaymentMethodPage)(safetyAndSecurity.paymentMethod)
      .unsafeSetVal(AddCommercialReferenceNumberPage)(safetyAndSecurity.commercialReferenceNumber.isDefined)
      .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(safetyAndSecurity.commercialReferenceNumber.isDefined)
      .unsafeSetOpt(CommercialReferenceNumberAllItemsPage)(safetyAndSecurity.commercialReferenceNumber)
      .unsafeSetVal(AddPlaceOfUnloadingCodePage)(safetyAndSecurity.placeOfUnloading.isDefined)
      .unsafeSetOpt(PlaceOfUnloadingCodePage)(safetyAndSecurity.placeOfUnloading)
      // Set Consignor
      .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(safetyAndSecurity.consignor.isDefined)
      .unsafeSetPFn(AddSafetyAndSecurityConsignorEoriPage)(safetyAndSecurity.consignor)({
        case Some(TraderEori(_)) => true
        case Some(_)             => false
      })
      .unsafeSetPFn(SafetyAndSecurityConsignorEoriPage)(safetyAndSecurity.consignor)({
        case Some(TraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(SafetyAndSecurityConsignorNamePage)(safetyAndSecurity.consignor)({
        case Some(PersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(SafetyAndSecurityConsignorAddressPage)(safetyAndSecurity.consignor)({
        case Some(PersonalInformation(_, address)) =>
          Address.prismAddressToConsignorAddress.getOption(address).get
      })
      // Set Consignee
      .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(safetyAndSecurity.consignee.isDefined)
      .unsafeSetPFn(AddSafetyAndSecurityConsigneeEoriPage)(safetyAndSecurity.consignee)({
        case Some(TraderEori(_)) => true
        case Some(_)             => false
      })
      .unsafeSetPFn(SafetyAndSecurityConsigneeEoriPage)(safetyAndSecurity.consignee)({
        case Some(TraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(SafetyAndSecurityConsigneeNamePage)(safetyAndSecurity.consignee)({
        case Some(PersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(SafetyAndSecurityConsigneeAddressPage)(safetyAndSecurity.consignee)({
        case Some(PersonalInformation(_, address)) =>
          Address.prismAddressToConsigneeAddress.getOption(address).get
      })
      // Set Carrier
      .unsafeSetVal(AddCarrierPage)(safetyAndSecurity.carrier.isDefined)
      .unsafeSetPFn(AddCarrierEoriPage)(safetyAndSecurity.carrier)({
        case Some(TraderEori(_)) => true
        case Some(_)             => false
      })
      .unsafeSetPFn(CarrierEoriPage)(safetyAndSecurity.carrier)({
        case Some(TraderEori(eori)) => eori.value
      })
      .unsafeSetPFn(CarrierNamePage)(safetyAndSecurity.carrier)({
        case Some(PersonalInformation(name, _)) => name
      })
      .unsafeSetPFn(CarrierAddressPage)(safetyAndSecurity.carrier)({
        case Some(PersonalInformation(_, address)) =>
          Address.prismAddressToCarrierAddress.getOption(address).get
      })

    val updatedUserAnswers = ua.get(ModeAtBorderPage) match {
      case Some("4") | Some("40") =>
        ua.unsafeSetOpt(ConveyanceReferenceNumberPage)(safetyAndSecurity.conveyanceReferenceNumber)
      case _ =>
        ua.unsafeSetVal(AddConveyanceReferenceNumberPage)(safetyAndSecurity.conveyanceReferenceNumber.isDefined)
          .unsafeSetOpt(ConveyanceReferenceNumberPage)(safetyAndSecurity.conveyanceReferenceNumber)
    }
    println("--------updatedUserAnswers-----------------" + updatedUserAnswers)
    updatedUserAnswers
  }

  def setSafetyAndSecurityMinimal(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
      .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
      .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
      .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
      .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(false)
      .unsafeSetVal(ModeAtBorderPage)("1")
      .unsafeSetVal(AddConveyanceReferenceNumberPage)(false)
      .unsafeSetVal(AddPlaceOfUnloadingCodePage)(false)
      .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
      .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
      .unsafeSetVal(AddCarrierPage)(false)

}
