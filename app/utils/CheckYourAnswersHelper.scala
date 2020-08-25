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
import models.{CheckMode, LocalReferenceNumber, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def consignorName: Option[Row] = userAnswers.get(ConsignorNamePage) map {
    answer =>
      Row(
        key     = Key(msg"consignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConsignorNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"consignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def consignorEori: Option[Row] = userAnswers.get(ConsignorEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"consignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"consignorEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addConsignor: Option[Row] = userAnswers.get(AddConsignorPage) map {
    answer =>
      Row(
        key     = Key(msg"addConsignor.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddConsignorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addConsignor.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isConsignorEoriKnown: Option[Row] = userAnswers.get(IsConsignorEoriKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isConsignorEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsConsignorEoriKnownController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isConsignorEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def principalName: Option[Row] = userAnswers.get(PrincipalNamePage) map {
    answer =>
      Row(
        key     = Key(msg"principalName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.PrincipalNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"principalName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isPrincipalEoriKnown: Option[Row] = userAnswers.get(IsPrincipalEoriKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isPrincipalEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsPrincipalEoriKnownController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isPrincipalEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def whatIsPrincipalEori: Option[Row] = userAnswers.get(WhatIsPrincipalEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"whatIsPrincipalEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatIsPrincipalEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatIsPrincipalEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def representativeCapacity: Option[Row] = userAnswers.get(RepresentativeCapacityPage) map {
    answer =>
      Row(
        key     = Key(msg"representativeCapacity.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"representativeCapacity.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.RepresentativeCapacityController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"representativeCapacity.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-representative-capacity""")
          )
        )
      )
  }

  def representativeName: Option[Row] = userAnswers.get(RepresentativeNamePage) map {
    answer =>
      Row(
        key     = Key(msg"representativeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.RepresentativeNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"representativeName.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-representative-name""")
          )
        )
      )
  }

  def containersUsedPage: Option[Row] = userAnswers.get(ContainersUsedPage) map {
    answer =>
      Row(
        key = Key(msg"containersUsed.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.ContainersUsedPageController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"containersUsed.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-containers-used""")
          )
        )
      )
  }

  def declarationForSomeoneElse: Option[Row] = userAnswers.get(DeclarationForSomeoneElsePage) map {
    answer =>
      Row(
        key     = Key(msg"declarationForSomeoneElse.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DeclarationForSomeoneElseController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarationForSomeoneElse.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-declaration-for-someone-else""")
          )
        )
      )
  }

  def declarationPlace: Option[Row] = userAnswers.get(DeclarationPlacePage) map {
    answer =>
      Row(
        key     = Key(msg"declarationPlace.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DeclarationPlaceController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarationPlace.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-declaration-place""")
          )
        )
      )
  }

  def procedureType: Option[Row] = userAnswers.get(ProcedureTypePage) map {
    answer =>
      Row(
        key     = Key(msg"procedureType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"procedureType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ProcedureTypeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"procedureType.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-procedure-type""")
          )
        )
      )
  }

  def declarationType: Option[Row] = userAnswers.get(DeclarationTypePage) map {
    answer =>
      Row(
        key     = Key(msg"declarationType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"declarationType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DeclarationTypeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarationType.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-declaration-type""")
          )
        )
      )
  }

  def addSecurityDetails: Option[Row] = userAnswers.get(AddSecurityDetailsPage) map {
    answer =>
      Row(
        key     = Key(msg"addSecurityDetails.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSecurityDetailsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityDetails.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-add-security-details""")
          )
        )
      )
  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }

  def lrn: LocalReferenceNumber = userAnswers.id
}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
