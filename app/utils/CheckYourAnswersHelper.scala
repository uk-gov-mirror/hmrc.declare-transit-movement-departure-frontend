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

package utils

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, Index, LocalReferenceNumber, UserAnswers}
import pages._
import pages.addItems.traderSecurityDetails.{
  SecurityConsigneeAddressPage,
  SecurityConsigneeEoriPage,
  SecurityConsigneeNamePage,
  SecurityConsignorAddressPage,
  SecurityConsignorEoriPage,
  SecurityConsignorNamePage
}
import pages.safetyAndSecurity.{
  AddAnotherCountryOfRoutingPage,
  AddCircumstanceIndicatorPage,
  AddCommercialReferenceNumberAllItemsPage,
  AddCommercialReferenceNumberPage,
  AddConveyancerReferenceNumberPage,
  AddPlaceOfUnloadingCodePage,
  AddTransportChargesPaymentMethodPage,
  CircumstanceIndicatorPage,
  CommercialReferenceNumberAllItemsPage,
  ConveyanceReferenceNumberPage,
  CountryOfRoutingPage,
  PlaceOfUnloadingCodePage,
  TransportChargesPaymentMethodPage
}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def addSecurityDetails: Option[Row] = userAnswers.get(AddSecurityDetailsPage) map {
    answer =>
      Row(
        key   = Key(msg"addSecurityDetails.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSecurityDetailsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityDetails.checkYourAnswersLabel")),
            attributes         = Map("id" -> s"""change-add-security-details""")
          )
        )
      )
  }

  def procedureType: Option[Row] = userAnswers.get(ProcedureTypePage) map {
    answer =>
      Row(
        key   = Key(msg"procedureType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(msg"procedureType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ProcedureTypeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"procedureType.checkYourAnswersLabel")),
            attributes         = Map("id" -> s"""change-procedure-type""")
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id

}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
