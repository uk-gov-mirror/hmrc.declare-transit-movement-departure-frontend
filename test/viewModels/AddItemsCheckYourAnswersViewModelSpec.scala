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
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.containers.ContainerNumberPage
import uk.gov.hmrc.viewmodels.MessageInterpolators

class AddItemsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {
  // format: off

  private val previousDocumentTypeList = PreviousDocumentTypeList(Seq(PreviousDocumentType("code", "name")))
  private val updatedAnswers = emptyUserAnswers
    .set(ItemDescriptionPage(index), "test").success.value
    .set(ItemTotalGrossMassPage(index), "100.00").success.value
    .set(AddTotalNetMassPage(index), true).success.value
    .set(TotalNetMassPage(index), "20").success.value
    .set(IsCommodityCodeKnownPage(index), true).success.value
    .set(CommodityCodePage(index), "111111").success.value
    .set(ContainerNumberPage(itemIndex, containerIndex), arbitrary[String].sample.value).success.value

  //val data = AddItemsCheckYourAnswersViewModel(updatedAnswers, index, previousDocumentTypeList)
  private val data = AddItemsCheckYourAnswersViewModel(updatedAnswers, index)


  "AddItemsCheckYourAnswersViewModel" - {

    "display the correct number of sections" in {
      data.sections.length mustEqual 4
      data.sections.head.rows.length mustEqual 6
    }
    
    "details section have title and contain all rows" in {
      data.sections(0).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.itemDetails"
      data.sections(0).rows.length mustEqual 6
    }

    "containers sections have title and contain all rows" in {
      data.sections(3).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.containers"
      data.sections(3).rows.length mustEqual 1
    }
  }
  // format: on
}
