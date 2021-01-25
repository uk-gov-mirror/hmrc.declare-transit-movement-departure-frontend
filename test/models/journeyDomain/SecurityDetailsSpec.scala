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
import models.journeyDomain.SecurityDetailsSpec.setSecurityDetailsUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.safetyAndSecurity.{AddCommercialReferenceNumberAllItemsPage, AddTransportChargesPaymentMethodPage}

class SecurityDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "SecurityDetails" - {

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[SecurityDetails], arbitrary[UserAnswers]) {
          case (securityDetails, userAnswers) =>
            val updatedUserAnswers = setSecurityDetailsUserAnswers(securityDetails, index)(userAnswers)
            val result             = UserAnswersReader[SecurityDetails](SecurityDetails.securityDetailsReader(index)).run(updatedUserAnswers)

            result.value mustEqual securityDetails
        }
      }
    }
  }
}

object SecurityDetailsSpec {

  def setSecurityDetailsUserAnswers(securityDetails: SecurityDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val methodOfPayment: UserAnswers = securityDetails.methodOfPayment match {
      case Some(value) =>
        startUserAnswers
          .set(AddTransportChargesPaymentMethodPage, false)
          .toOption
          .get
          .set(TransportChargesPage(index), value)
          .toOption
          .get
      case None =>
        startUserAnswers
          .set(AddTransportChargesPaymentMethodPage, true)
          .toOption
          .get
    }

    val commercialReferenceNumber = securityDetails.commercialReferenceNumber match {
      case Some(value) =>
        methodOfPayment
          .set(AddCommercialReferenceNumberAllItemsPage, false)
          .toOption
          .get
          .set(CommercialReferenceNumberPage(index), value)
          .toOption
          .get
      case None =>
        methodOfPayment
          .set(AddCommercialReferenceNumberAllItemsPage, true)
          .toOption
          .get
    }

    val dangerousGoodsCode = securityDetails.dangerousGoodsCode match {
      case Some(value) =>
        commercialReferenceNumber
          .set(AddDangerousGoodsCodePage(index), true)
          .toOption
          .get
          .set(DangerousGoodsCodePage(index), value)
          .toOption
          .get
      case None =>
        commercialReferenceNumber
          .set(AddDangerousGoodsCodePage(index), false)
          .toOption
          .get
    }
    dangerousGoodsCode
  }
}
