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

import base.SpecBase
import models.PreviousDocumentTypeList
import models.reference.PreviousDocumentType
import pages._
import pages.addItems._

class AddItemsCheckYourAnswersViewModelSpec extends SpecBase {

  private val previousDocumentTypeList = PreviousDocumentTypeList(Seq(PreviousDocumentType("code", "name")))

  "AddItemsCheckYourAnswersViewModel" - {

    "display all user answers" in {
      val updatedAnswers = emptyUserAnswers
        .set(ItemDescriptionPage(index), "test")
        .success
        .value
        .set(ItemTotalGrossMassPage(index), "100.00")
        .success
        .value
        .set(AddTotalNetMassPage(index), true)
        .success
        .value
        .set(TotalNetMassPage(index), "20")
        .success
        .value
        .set(IsCommodityCodeKnownPage(index), true)
        .success
        .value
        .set(CommodityCodePage(index), "111111")
        .success
        .value

      val data = AddItemsCheckYourAnswersViewModel(updatedAnswers, index, previousDocumentTypeList)

      data.sections.head.sectionTitle mustBe defined
      data.sections.length mustEqual 4
      data.sections.head.rows.length mustEqual 6
    }
  }
}
