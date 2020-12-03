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

import controllers.safetyAndSecurity.routes
import models.{CheckMode, Index, LocalReferenceNumber, UserAnswers}
import pages.safetyAndSecurity._
import queries.CountriesOfRoutingQuery
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class SafetyAndSecurityCheckYourAnswerHelper(userAnswers: UserAnswers) {

  private def lrn: LocalReferenceNumber = userAnswers.id

  def addCarrierEori: Option[Row] = userAnswers.get(AddCarrierEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"addCarrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCarrierEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCarrierEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCarrier: Option[Row] = userAnswers.get(AddCarrierPage) map {
    answer =>
      Row(
        key   = Key(msg"addCarrier.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCarrierController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCarrier.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCircumstanceIndicator: Option[Row] = userAnswers.get(AddCircumstanceIndicatorPage) map {
    answer =>
      Row(
        key   = Key(msg"addCircumstanceIndicator.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCircumstanceIndicator.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCommercialReferenceNumberAllItems: Option[Row] = userAnswers.get(AddCommercialReferenceNumberAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"addCommercialReferenceNumberAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCommercialReferenceNumberAllItems.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addCommercialReferenceNumber: Option[Row] = userAnswers.get(AddCommercialReferenceNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"addCommercialReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddCommercialReferenceNumberController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addCommercialReferenceNumber.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addConveyanceReferenceNumber: Option[Row] = userAnswers.get(AddConveyanceReferenceNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"addConveyanceReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addConveyanceReferenceNumber.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addPlaceOfUnloadingCode: Option[Row] = userAnswers.get(AddPlaceOfUnloadingCodePage) map {
    answer =>
      Row(
        key   = Key(msg"addPlaceOfUnloadingCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddPlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addPlaceOfUnloadingCode.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addSafetyAndSecurityConsigneeEori: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsigneeEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addSafetyAndSecurityConsignee: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsigneePage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsignee.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsigneeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignee.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addSafetyAndSecurityConsignorEori: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsignorEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addSafetyAndSecurityConsignor: Option[Row] = userAnswers.get(AddSafetyAndSecurityConsignorPage) map {
    answer =>
      Row(
        key   = Key(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSafetyAndSecurityConsignorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def addTransportChargesPaymentMethod: Option[Row] = userAnswers.get(AddTransportChargesPaymentMethodPage) map {
    answer =>
      Row(
        key   = Key(msg"addTransportChargesPaymentMethod.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddTransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"addTransportChargesPaymentMethod.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def carrierAddress: Option[Row] = userAnswers.get(CarrierAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"carrierAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"carrierAddress.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def carrierEori: Option[Row] = userAnswers.get(CarrierEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"carrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"carrierEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def carrierName: Option[Row] = userAnswers.get(CarrierNamePage) map {
    answer =>
      Row(
        key   = Key(msg"carrierName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CarrierNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"carrierName.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def circumstanceIndicator: Option[Row] = userAnswers.get(CircumstanceIndicatorPage) map {
    answer =>
      Row(
        key   = Key(msg"circumstanceIndicator.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"circumstanceIndicator.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def commercialReferenceNumberAllItems: Option[Row] = userAnswers.get(CommercialReferenceNumberAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"commercialReferenceNumberAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"commercialReferenceNumberAllItems.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def conveyanceReferenceNumber: Option[Row] = userAnswers.get(ConveyanceReferenceNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"conveyanceReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"conveyanceReferenceNumber.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def placeOfUnloadingCode: Option[Row] = userAnswers.get(PlaceOfUnloadingCodePage) map {
    answer =>
      Row(
        key   = Key(msg"placeOfUnloadingCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.PlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"placeOfUnloadingCode.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsigneeAddress: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsigneeEori: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsigneeName: Option[Row] = userAnswers.get(SafetyAndSecurityConsigneeNamePage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsigneeNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsignorAddress: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsignorEori: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def safetyAndSecurityConsignorName: Option[Row] = userAnswers.get(SafetyAndSecurityConsignorNamePage) map {
    answer =>
      Row(
        key   = Key(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SafetyAndSecurityConsignorNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def transportChargesPaymentMethod: Option[Row] = userAnswers.get(TransportChargesPaymentMethodPage) map {
    answer =>
      Row(
        key   = Key(msg"transportChargesPaymentMethod.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"transportChargesPaymentMethod.checkYourAnswersLabel.visuallyHidden")
          )
        )
      )
  }

  def countriesOfRouting(index: Index): Option[Row] = userAnswers.get(CountryOfRoutingPage(index)).map {
    answer =>
      Row(
        key   = Key(lit"$answer"),
        value = Value(lit""),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"safetyAndSecurity.checkYourAnswersLabel.countriesOfRouting.change.visuallyHidden"),
            attributes         = Map("id" -> s"""change-country-${index.display}""")
          ),
//          Action(
//            content            = msg"site.delete",
//            href               = routes.ConfirmRemoveCountryController.onPageLoad(lrn, CheckMode).url,
//            visuallyHiddenText = Some(msg"safetyAndSecurity.checkYourAnswersLabel.countriesOfRouting.delete.visuallyHidden"),
//            attributes         = Map("id" -> s"""remove-country-${index.display}""")
//          ),

        )
      )
  }

}
