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

import models.{Index, UserAnswers}
import uk.gov.hmrc.viewmodels.MessageInterpolators
import utils.AddItemsCheckYourAnswersHelper
import viewModels.sections.Section

case class AddItemsCheckYourAnswersViewModel(sections: Seq[Section])

object AddItemsCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, index: Index): AddItemsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new AddItemsCheckYourAnswersHelper(userAnswers)

    AddItemsCheckYourAnswersViewModel(
      Seq(
        Section(
          msg"addItems.checkYourAnswersLabel.itemDetails",
          Seq(
          checkYourAnswersHelper.itemDescription(index),
          checkYourAnswersHelper.itemTotalGrossMass(index),
          checkYourAnswersHelper.addTotalNetMass(index),
          checkYourAnswersHelper.totalNetMass(index),
          checkYourAnswersHelper.isCommodityCodeKnown(index),
          checkYourAnswersHelper.commodityCode(index),
          checkYourAnswersHelper.addItemsSameConsigneeForAllItems(index),
          checkYourAnswersHelper.addItemsSameConsignorForAllItems(index),
          checkYourAnswersHelper.traderDetailsConsignorName(index),
          checkYourAnswersHelper.traderDetailsConsignorEoriNumber(index),
          checkYourAnswersHelper.traderDetailsConsignorEoriKnown(index),
          checkYourAnswersHelper.traderDetailsConsignorAddress(index),
          checkYourAnswersHelper.traderDetailsConsigneeName(index),
          checkYourAnswersHelper.traderDetailsConsigneeEoriNumber(index),
          checkYourAnswersHelper.traderDetailsConsigneeEoriKnown(index),
          checkYourAnswersHelper.traderDetailsConsigneeAddress(index),
        ).flatten)))
  }
}
