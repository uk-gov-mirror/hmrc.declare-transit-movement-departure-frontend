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

package viewModels

import models.UserAnswers
import uk.gov.hmrc.viewmodels.MessageInterpolators
import utils.SafetyAndSecurityCheckYourAnswerHelper
import viewModels.sections.Section

object SafetyAndSecurityViewModel {

  def apply(userAnswers: UserAnswers): Seq[Section] = {
    val cyah = new SafetyAndSecurityCheckYourAnswerHelper(userAnswers)

    Seq(
      Section(
        Seq(
          cyah.addCircumstanceIndicator,
          cyah.circumstanceIndicator,
          cyah.addTransportChargesPaymentMethod,
          cyah.transportChargesPaymentMethod,
          cyah.addCommercialReferenceNumber,
          cyah.addCommercialReferenceNumberAllItems,
          cyah.commercialReferenceNumberAllItems,
          cyah.addConveyanceReferenceNumber,
          cyah.conveyanceReferenceNumber,
          cyah.addPlaceOfUnloadingCode,
          cyah.placeOfUnloadingCode
        ).flatten
      ),
      Section(
        msg"safetyAndSecurity.checkYourAnswersLabel.countriesOfRouting",
        Seq(
          cyah.countriesOfRouting
        ).flatten
      ),
      Section(
        msg"safetyAndSecurity.checkYourAnswersLabel.securityTraderDetails",
        Seq(
          //TODO Consignor subheading
          cyah.addSafetyAndSecurityConsignor,
          cyah.addSafetyAndSecurityConsignorEori,
          cyah.safetyAndSecurityConsignorEori,
          cyah.safetyAndSecurityConsignorName,
          cyah.safetyAndSecurityConsignorAddress,
          //TODO Consignee subheading
          cyah.addSafetyAndSecurityConsignee,
          cyah.addSafetyAndSecurityConsigneeEori,
          cyah.safetyAndSecurityConsigneeEori,
          cyah.safetyAndSecurityConsigneeName,
          cyah.safetyAndSecurityConsigneeAddress,
          //TODO Carrier subheading
          cyah.addCarrier,
          cyah.addCarrierEori,
          cyah.carrierEori,
          cyah.carrierName,
          cyah.carrierAddress
        ).flatten
      )
    )
  }

}
