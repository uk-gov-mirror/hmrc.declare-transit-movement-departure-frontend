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
import pages.addItems.traderSecurityDetails.{SecurityConsigneeAddressPage, SecurityConsigneeEoriPage, SecurityConsigneeNamePage, SecurityConsignorAddressPage, SecurityConsignorEoriPage, SecurityConsignorNamePage}
import pages.safetyAndSecurity.{AddAnotherCountryOfRoutingPage, AddCarrierEoriPage, AddCarrierPage, AddCircumstanceIndicatorPage, AddCommercialReferenceNumberAllItemsPage, AddCommercialReferenceNumberPage, AddConveyancerReferenceNumberPage, AddPlaceOfUnloadingCodePage, AddSafetyAndSecurityConsigneeEoriPage, AddSafetyAndSecurityConsigneePage, AddSafetyAndSecurityConsignorEoriPage, AddSafetyAndSecurityConsignorPage, AddTransportChargesPaymentMethodPage, CarrierAddressPage, CarrierEoriPage, CarrierNamePage, CircumstanceIndicatorPage, CommercialReferenceNumberAllItemsPage, ConveyanceReferenceNumberPage, CountryOfRoutingPage, PlaceOfUnloadingCodePage, SafetyAndSecurityConsigneeAddressPage, SafetyAndSecurityConsigneeEoriPage, SafetyAndSecurityConsigneeNamePage, SafetyAndSecurityConsignorAddressPage, SafetyAndSecurityConsignorEoriPage, SafetyAndSecurityConsignorNamePage, TransportChargesPaymentMethodPage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def carrierName: Option[Row] = userAnswers.get(CarrierNamePage) map {
    answer =>
      Row(
        key     = Key(msg"carrierName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"carrierName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def carrierEori: Option[Row] = userAnswers.get(CarrierEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"carrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"carrierEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def carrierAddress: Option[Row] = userAnswers.get(CarrierAddressPage) map {
    answer =>
      Row(
        key     = Key(msg"carrierAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"carrierAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addCarrier: Option[Row] = userAnswers.get(AddCarrierPage) map {
    answer =>
      Row(
        key     = Key(msg"addCarrier.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCarrierController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addCarrier.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addCarrierEori: Option[Row] = userAnswers.get(AddCarrierEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"addCarrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCarrierEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addCarrierEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def safetyAndSecurityConsigneeName: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeNamePage) map {
    answer =>
      Row(
        key     = Key(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def safetyAndSecurityConsigneeEori: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def safetyAndSecurityConsigneeAddress: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeAddressPage) map {
    answer =>
      Row(
        key     = Key(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSafetyAndSecurityConsigneeEori: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsigneeEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSafetyAndSecurityConsignee: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsigneePage) map {
    answer =>
      Row(
        key     = Key(msg"addSafetyAndSecurityConsignee.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsigneeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSafetyAndSecurityConsignee.checkYourAnswersLabel"))
          )
        )
      )
  }

  def safetyAndSecurityConsignorName: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorNamePage) map {
    answer =>
      Row(
        key     = Key(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def safetyAndSecurityConsignorEori: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def safetyAndSecurityConsignorAddress: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorAddressPage) map {
    answer =>
      Row(
        key     = Key(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSafetyAndSecurityConsignorEori: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsignorEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel"))
          )
        )
      )
  }

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
