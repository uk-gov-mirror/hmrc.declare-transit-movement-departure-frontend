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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import base.SpecBase
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import uk.gov.hmrc.viewmodels.Text.{Literal, Message}

class GoodsSummaryCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  "GoodsSummaryCheckYourAnswersViewModel" - {

    "display Yes when selected for packages to declare" in {

      val updatedAnswers = emptyUserAnswers.set(DeclarePackagesPage, true).success.value
      val data           = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      val message: Message = data.sections.head.rows.head.value.content.asInstanceOf[Message]
      message.key mustBe "site.yes"

    }
    "display Yes when selected for customs approved location" in {

      val updatedAnswers = emptyUserAnswers
        .set(AddCustomsApprovedLocationPage, true)
        .success
        .value
      val data = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      val message: Message = data.sections.head.rows.head.value.content.asInstanceOf[Message]
      message.key mustBe "site.yes"

    }

    "display Yes when selected foradd seal" in {

      val updatedAnswers = emptyUserAnswers
        .set(AddSealsPage, true)
        .success
        .value
      val data = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      val message: Message = data.sections.head.rows.head.value.content.asInstanceOf[Message]
      message.key mustBe "site.yes"

    }

    "display Number of packages declared" in {

      val updatedAnswers = emptyUserAnswers
        .set(DeclarePackagesPage, true)
        .success
        .value
        .set(TotalPackagesPage, 1000)
        .success
        .value
      val data = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 2
      data.sections.head.rows(1).value.content mustEqual Literal("1000")

    }
    "display Total gross mass declared" in {

      val updatedAnswers = emptyUserAnswers
        .set(DeclarePackagesPage, true)
        .success
        .value
        .set(TotalPackagesPage, 1000)
        .success
        .value
        .set(TotalGrossMassPage, "1000.123")
        .success
        .value
      val data = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 3
      data.sections.head.rows(2).value.content mustEqual Literal("1000.123")

    }

    "display Authorised location" in {

      val updatedAnswers = emptyUserAnswers
        .set(DeclarePackagesPage, true)
        .success
        .value
        .set(TotalPackagesPage, 1000)
        .success
        .value
        .set(TotalGrossMassPage, "1000.123")
        .success
        .value
        .set(AuthorisedLocationCodePage, "AuthCode")
        .success
        .value
      val data = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 4
      data.sections.head.rows(3).value.content mustEqual Literal("AuthCode")

    }
    "display Customs approve location" in {

      val updatedAnswers = emptyUserAnswers
        .set(DeclarePackagesPage, true)
        .success
        .value
        .set(TotalPackagesPage, 1000)
        .success
        .value
        .set(TotalGrossMassPage, "1000.123")
        .success
        .value
        .set(CustomsApprovedLocationPage, "ApprovedCode")
        .success
        .value
      val data = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 4
      data.sections.head.rows(3).value.content mustEqual Literal("ApprovedCode")

    }

    "display Control result date limit" in {

      val date                             = LocalDate.now
      val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val todaysDate: String               = dateFormatter.format(date)

      val updatedAnswers = emptyUserAnswers
        .set(DeclarePackagesPage, true)
        .success
        .value
        .set(TotalPackagesPage, 1000)
        .success
        .value
        .set(TotalGrossMassPage, "1000.123")
        .success
        .value
        .set(AuthorisedLocationCodePage, "AuthCode")
        .success
        .value
        .set(ControlResultDateLimitPage, date)
        .success
        .value
      val data = GoodsSummaryCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 5
      data.sections.head.rows(4).value.content mustEqual Literal(todaysDate)

    }

  }
}
