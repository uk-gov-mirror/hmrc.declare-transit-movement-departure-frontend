/*
 * Copyright 2021 HM Revenue & Customs
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
import generators.Generators
import models.GuaranteeType.GuaranteeWaiver
import models.Index
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages.{AccessCodePage, DefaultAmountPage, LiabilityAmountPage, OtherReferenceLiabilityAmountPage, OtherReferencePage}
import uk.gov.hmrc.viewmodels.Text.{Literal, Message}

class GuaranteeDetailsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "GuaranteeDetailsCheckYourAnswersViewModel" - {

    "display Guarantee Type when selected" in {
      val updatedAnswers = emptyUserAnswers.set(GuaranteeTypePage(Index(0)), GuaranteeWaiver).success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, Index(0))

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      val message = data.sections.head.rows.head.value.content.asInstanceOf[Message]
      message.key mustBe "guaranteeType.GuaranteeWaiver"
    }

    "display Guarantee Reference number when selected" in {

      val updatedAnswers = emptyUserAnswers.set(GuaranteeReferencePage(index), "test").success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, Index(0))

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("test")
    }

    "display Other Reference when selected" in {

      val updatedAnswers = emptyUserAnswers.set(OtherReferencePage(index), "test").success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, index)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("test")
    }

//    "OtherReferenceLiabilityAmount" - {
//
//      "must show default value of EUR 10000 when default value is yes" in {
//
//        val updatedAnswers = emptyUserAnswers
//          .set(OtherReferenceLiabilityAmountPage(index), "")
//          .success
//          .value
//          .set(DefaultAmountPage(index), true)
//          .success
//          .value
//
//        val data                          = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, index)
//        val otherReferenceLiabilityAmount = data.sections.head.rows.head.value.content.asInstanceOf[Message]
//        val defaultLiabilityMessage       = data.sections.head.rows(1).value.content.asInstanceOf[Message]
//
//        data.sections.head.sectionTitle must not be defined
//        data.sections.length mustEqual 1
//        data.sections.head.rows.length mustEqual 2
//        defaultLiabilityMessage.key mustBe "site.yes"
//        otherReferenceLiabilityAmount.key mustBe "guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"
//      }
//
//      "must show liabilityAmount when default value is no" in {
//
//        val updatedAnswers = emptyUserAnswers
//          .set(OtherReferenceLiabilityAmountPage(index), "123")
//          .success
//          .value
//          .set(DefaultAmountPage(index), false)
//          .success
//          .value
//
//        val data                    = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, index)
//        val defaultLiabilityMessage = data.sections.head.rows(1).value.content.asInstanceOf[Message]
//
//        data.sections.head.sectionTitle must not be defined
//        data.sections.length mustEqual 1
//        data.sections.head.rows.length mustEqual 2
//        defaultLiabilityMessage.key mustBe "site.no"
//        data.sections.head.rows.head.value.content mustEqual Literal("123")
//      }
//    }

    "display Liability Amount" - {
      "and amount as 10 when selected" in {
        val updatedAnswers = emptyUserAnswers.set(LiabilityAmountPage(index), "10.00").success.value
        val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, index)

        data.sections.head.sectionTitle must not be defined
        data.sections.length mustEqual 1
        data.sections.head.rows.length mustEqual 1
        data.sections.head.rows.head.value.content mustEqual Literal("10.00")
      }
    }
    "display Default Liability Amount when selected" in {

      val updatedAnswers = emptyUserAnswers
        .set(DefaultAmountPage(index), true)
        .success
        .value

      val data             = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, index)
      val message: Message = data.sections.head.rows.head.value.content.asInstanceOf[Message]

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      message.key mustBe "site.yes"
    }

    "display Default Liability Amount when no is selected" in {

      val updatedAnswers = emptyUserAnswers
        .set(DefaultAmountPage(index), false)
        .success
        .value

      val data             = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, index)
      val message: Message = data.sections.head.rows.head.value.content.asInstanceOf[Message]

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      message.key mustBe "site.no"

    }

    "display Access Code when selected" in {

      val updatedAnswers = emptyUserAnswers.set(AccessCodePage(index), "a1b2").success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers, index)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("••••")
    }
  }
}
