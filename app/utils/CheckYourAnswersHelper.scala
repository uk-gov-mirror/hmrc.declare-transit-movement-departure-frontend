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
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def securityConsigneeName: Option[Row] = userAnswers.get(SecurityConsigneeNamePage) map {
    answer =>
      Row(
        key   = Key(msg"securityConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SecurityConsigneeNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsignorName: Option[Row] = userAnswers.get(SecurityConsignorNamePage) map {
    answer =>
      Row(
        key   = Key(msg"securityConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SecurityConsignorNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsigneeAddress: Option[Row] = userAnswers.get(SecurityConsigneeAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"securityConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SecurityConsigneeAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsignorAddress: Option[Row] = userAnswers.get(SecurityConsignorAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"securityConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SecurityConsignorAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsigneeEori: Option[Row] = userAnswers.get(SecurityConsigneeEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"securityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsignorEori: Option[Row] = userAnswers.get(SecurityConsignorEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"securityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.SecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorEori.checkYourAnswersLabel"))
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
