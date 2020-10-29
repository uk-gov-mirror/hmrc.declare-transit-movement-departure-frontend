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

import controllers.addItems.routes
import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import models.{CheckMode, Index, LocalReferenceNumber, Mode, NormalMode, UserAnswers}
import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import models.{CheckMode, Index, LocalReferenceNumber, UserAnswers}
import pages._
import pages.addItems.traderDetails._
import pages.addItems._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def addItemsSameConsignorForAllItems(index: Index): Option[Row] = userAnswers.get(AddItemsSameConsignorForAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"addItemsSameConsignorForAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddItemsSameConsignorForAllItemsController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addItemsSameConsignorForAllItems.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addItemsSameConsigneeForAllItems(index: Index): Option[Row] = userAnswers.get(AddItemsSameConsigneeForAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"addItemsSameConsigneeForAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addItemsSameConsigneeForAllItems.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorNamePage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriKnownPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorAddress(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeNamePage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriKnownPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeAddress(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def commodityCode(index: Index): Option[Row] = userAnswers.get(CommodityCodePage(index)) map {
    answer =>
      Row(
        key   = Key(msg"commodityCode.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CommodityCodeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"commodityCode.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-commodity-code")
          )
        )
      )
  }

  def totalNetMass(index: Index): Option[Row] = userAnswers.get(TotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display))),
            attributes         = Map("id" -> "change-total-net-mass")
          )
        )
      )
  }

  def isCommodityCodeKnown(index: Index): Option[Row] = userAnswers.get(IsCommodityCodeKnownPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"isCommodityCodeKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isCommodityCodeKnown.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-is-commodity-known")
          )
        )
      )
  }

  def addTotalNetMass(index: Index): Option[Row] = userAnswers.get(AddTotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"addTotalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTotalNetMass.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-add-total-net-mass")
          )
        )
      )
  }

  def itemTotalGrossMass(index: Index): Option[Row] = userAnswers.get(ItemTotalGrossMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemTotalGrossMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemTotalGrossMass.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-item-total-gross-mass")
          )
        )
      )
  }

  def itemDescription(index: Index): Option[Row] = userAnswers.get(ItemDescriptionPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemDescription.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemDescription.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-item-description")
          )
        )
      )
  }

  def itemRows(index: Index): Option[Row] =
    userAnswers.get(ItemDescriptionPage(index)).map {
      answer =>
        Row(
          key   = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.change.hidden".withArgs(answer)),
              attributes         = Map("id" -> s"""change-item-${index.display}""")
            ),
            Action(
              content            = msg"site.delete",
              href               = routes.ConfirmRemoveItemController.onPageLoad(userAnswers.id, index).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.delete.hidden".withArgs(answer)),
              attributes         = Map("id" -> s"""remove-item-${index.display}""")
            )
          )
        )
    }

  def addAdministrativeReference(index: Index, referenceIndex: Index): Option[Row] =
    userAnswers.get(AddAdministrativeReferencePage(index, referenceIndex)) map {
      answer =>
        Row(
          key   = Key(msg"addAdministrativeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAdministrativeReference.checkYourAnswersLabel"))
            )
          )
        )
    }

  def packageRows(itemIndex: Index, packageIndex: Index, mode: Mode): Option[Row] =
    userAnswers.get(PackageTypePage(itemIndex, packageIndex)).map {
      answer =>
        Row(
          key   = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.PackageTypeController.onPageLoad(userAnswers.id, itemIndex, packageIndex, mode).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.change.hidden".withArgs(answer)),
              attributes         = Map("id" -> s"""change-package-${packageIndex.display}""")
            ),
            Action(
              content            = msg"site.delete",
              href               = "", // TODO Create page
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.delete.hidden".withArgs(answer)),
              attributes         = Map("id" -> s"""remove-package-${packageIndex.display}""")
            )
          )
        )
    }

  def referenceType(itemIndex: Index, referenceIndex: Index): Option[Row] = userAnswers.get(ReferenceTypePage(itemIndex, referenceIndex)) map {
    answer =>
      Row(
        key   = Key(msg"referenceType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"referenceType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id

}
