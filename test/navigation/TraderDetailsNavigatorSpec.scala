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

package navigation

import base.{SpecBase, UserAnswersSpecHelper}
import controllers.traderDetails.{routes => traderDetailsRoute}
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ConsigneeAddressPage, _}

class TraderDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new TraderDetailsNavigator

  val genNormalProcedureUserAnswers: Gen[UserAnswers] =
    arbitrary[UserAnswers].map(_.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal))

  val genSimplifiedProcedureUserAnswers: Gen[UserAnswers] =
    arbitrary[UserAnswers].map(_.unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified))

  val genUserAnswersWithProcedure: Gen[UserAnswers] =
    Gen.oneOf(
      genNormalProcedureUserAnswers,
      genSimplifiedProcedureUserAnswers
    )

  "TraderDetailsNavigator" - {

    "in Normal mode" - {

      "Principal trader section" - {

        "for a Normal procedure declaration" - {

          "must go from Is principal eori known page " - {

            "to what is eori number page when 'YES' is selected" in {

              forAll(genNormalProcedureUserAnswers) {
                answers =>
                  val updatedAnswers = answers
                    .unsafeSetVal(IsPrincipalEoriKnownPage)(true)

                  navigator
                    .nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                    .mustBe(traderDetailsRoute.WhatIsPrincipalEoriController.onPageLoad(answers.id, NormalMode))
              }
            }

            "to principal name page when 'NO' is selected" in {

              forAll(genNormalProcedureUserAnswers) {
                answers =>
                  val updatedAnswers = answers
                    .unsafeSetVal(IsPrincipalEoriKnownPage)(false)

                  navigator
                    .nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                    .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.id, NormalMode))
              }
            }
          }

          "must go from Principal eori page to Add consignor page" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from Principal name page to Principal address page" in {
            forAll(genNormalProcedureUserAnswers) {
              answers =>
                navigator
                  .nextPage(PrincipalNamePage, NormalMode, answers)
                  .mustBe(traderDetailsRoute.PrincipalAddressController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go from Principal address page to Add consignor page" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                navigator
                  .nextPage(PrincipalAddressPage, NormalMode, answers)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.id, NormalMode))
            }
          }

        }

        "for a Simplified procedure declaration" - {

          "must go from Principal eori page to Add consignor page" in {
            forAll(genSimplifiedProcedureUserAnswers) {
              answers =>
                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, answers)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.id, NormalMode))
            }
          }

        }

      }

      "must go from Add consignor page to Is consignor eori known page when 'YES' is selected" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            val updatedAnswers = answers
              .unsafeSetVal(AddConsignorPage)(true)

            navigator
              .nextPage(AddConsignorPage, NormalMode, updatedAnswers)
              .mustBe(traderDetailsRoute.IsConsignorEoriKnownController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Add consignor page to Add consignee page when 'NO' is selected" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            val updatedAnswers = answers
              .unsafeSetVal(AddConsignorPage)(false)

            navigator
              .nextPage(AddConsignorPage, NormalMode, updatedAnswers)
              .mustBe(traderDetailsRoute.AddConsigneeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "when consignor eori is known" - {

        "must go from Is consignor eori known page to Consignor eori page when 'YES' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)

              navigator
                .nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from EORI Number page to Consignor Name page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(ConsignorEoriPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.id, NormalMode))
          }
        }

      }

      "when consignor eori is not known" - {
        "must go from Is consignor eori known page to Consignor name page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(false)

              navigator
                .nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.id, NormalMode))
          }
        }
      }

      "must go from Consignor name page to Consignor address page" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            navigator
              .nextPage(ConsignorNamePage, NormalMode, answers)
              .mustBe(traderDetailsRoute.ConsignorAddressController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Consignor address page to Add consignee page" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            navigator
              .nextPage(ConsignorAddressPage, NormalMode, answers)
              .mustBe(traderDetailsRoute.AddConsigneeController.onPageLoad(answers.id, NormalMode))
        }
      }

      "when consignee eori is known" - {
        "must go from Add consignee page to Is consignee eori known page when 'YES' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(true)

              navigator
                .nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsigneeEoriKnownController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignee eori known page to Consignee eori page when 'YES' is selected" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(true)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsConsigneeEoriController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Is consignee eori known page to Consignee name page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(false)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from What Is Consignee Eori Page Number page to Consignee Name page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(WhatIsConsigneeEoriPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignee name page to Consignee address page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(ConsigneeNamePage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsigneeAddressController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Consignee address page to Trader details cya page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(ConsigneeAddressPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

      }

      "when consignee eori is not known" - {
        "must go from Add consignee page to Trader details cya page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(false)

              navigator
                .nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }
    }

    "in Check mode" - {

      "must go from Is principal eori known page " - {

        "to What is principal eori page if the option selected is 'YES', if it was not previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeRemove(WhatIsPrincipalEoriPage)

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsPrincipalEoriController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers Page if the option selected is 'YES', if principal eori previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)("principalEori")

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "to What is principal's name page if the option selected is 'NO', if it was not previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeRemove(PrincipalNamePage)

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers Page if the option selected is 'NO', if principal's name previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeSetVal(PrincipalNamePage)("principalName")

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

      }

      "must go from What is Principal's Eori page to Check Your Answers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(WhatIsPrincipalEoriPage, CheckMode, answers)
              .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from Principal name page" - {
        "to Check Your Answers page if Principal's Address previously answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[PrincipalAddress]) {

            (answers, principalAddress) =>
              val updatedAnswers =
                answers.unsafeSetVal(PrincipalAddressPage)(principalAddress)

              navigator
                .nextPage(PrincipalNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "to Principals Address page if Principal's Address not previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers =
                answers.unsafeRemove(PrincipalAddressPage)

              navigator
                .nextPage(PrincipalNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.PrincipalAddressController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }
      }

      "must go from Add consignor page" - {
        "to Is consignor eori known page when 'YES' is selected, if it was not previously answered" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeRemove(IsConsignorEoriKnownPage)

              navigator
                .nextPage(AddConsignorPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsignorEoriKnownController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers Page when 'YES' is selected, if Is consignor eori known was previously answered" in {

          forAll(genUserAnswersWithProcedure, arbitrary[Boolean]) {
            (answers, consignorEoriKnown) =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(consignorEoriKnown)

              navigator
                .nextPage(AddConsignorPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }
        }

        "to Check Your Answers Page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(false)

              navigator
                .nextPage(AddConsignorPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }
        }

      }

      "must go from Is consignor eori known page" - {
        "to Consignor EORI Number page when 'YES' is selected, if it was not previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeRemove(ConsignorEoriPage)

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorEoriController.onPageLoad(answers.id, CheckMode))

          }
        }

        "to Check Your Answers Page when 'YES' is selected, if Consignor EORI Number was previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)("consignorEori")

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }
        }

        "to Consignor Name page when 'NO' is selected, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(false)
                .unsafeRemove(ConsignorNamePage)

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers page when 'NO' is selected, and when Consignor Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(false)
                .unsafeSetVal(ConsignorNamePage)("Davey Jones")

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

      }

      "must go from Consignor Eori Number page" - {
        "to Consignor Name page, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsignorNamePage)

              navigator
                .nextPage(ConsignorEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers page when Consignor Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsignorNamePage)("Davey Jones")

              navigator
                .nextPage(ConsignorEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "must go from Consignor Name page" - {
        "to Check Your Answers page, when Consignor Address exists" in {
          forAll(arbitrary[UserAnswers], arbitrary[ConsignorAddress]) {
            (answers, address) =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsignorAddressPage)(address)

              navigator
                .nextPage(ConsignorNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "to Consignor Address page, when Consignor Address does not exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsignorAddressPage)

              navigator
                .nextPage(ConsignorNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorAddressController.onPageLoad(answers.id, CheckMode))
          }
        }

      }

      "must go from Consignor Address page to Check Your Answers page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers

            navigator
              .nextPage(ConsignorAddressPage, CheckMode, updatedAnswers)
              .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from Add consignee page" - {
        "to Is consignee eori known page when 'YES' is selected, if it was not previously answered" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(true)
                .unsafeRemove(IsConsigneeEoriKnownPage)

              navigator
                .nextPage(AddConsigneePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsigneeEoriKnownController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers Page when 'YES' is selected, if is consignee eori was previously answered" in {

          forAll(genUserAnswersWithProcedure, arbitrary[Boolean]) {
            (answers, consigneeEoriKnown) =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(true)
                .unsafeSetVal(IsConsigneeEoriKnownPage)(consigneeEoriKnown)

              navigator
                .nextPage(AddConsigneePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }
        }

        "to Check Your Answers Page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(false)

              navigator
                .nextPage(AddConsigneePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }
        }

      }

      "must go from Is consignee eori known page" - {
        "to Consignee EORI Number page when 'YES' is selected, if it was not previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
                .unsafeRemove(WhatIsConsigneeEoriPage)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsConsigneeEoriController.onPageLoad(answers.id, CheckMode))

          }
        }

        "to Check Your Answers Page when 'YES' is selected, if Consignee EORI Number was previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
                .unsafeSetVal(WhatIsConsigneeEoriPage)("consigneeEori")

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))

          }
        }

        "to Consignee Name page when 'NO' is selected, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
                .unsafeRemove(ConsigneeNamePage)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers page when 'NO' is selected, and when Consignee Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
                .unsafeSetVal(ConsigneeNamePage)("Davey Jones")

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "must go from Consignee Eori Number page" - {
        "to Consignee Name page, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsigneeNamePage)

              navigator
                .nextPage(WhatIsConsigneeEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.id, CheckMode))
          }
        }

        "to Check Your Answers page when Consignee Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsigneeNamePage)("Davey Jones")

              navigator
                .nextPage(WhatIsConsigneeEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "must go from Consignee Name page to Check Your Answers page" - {

        "to Check Your Answers page, when Consignee Address exists" in {
          forAll(arbitrary[UserAnswers], arbitrary[ConsigneeAddress]) {
            (answers, address) =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsigneeAddressPage)(address)

              navigator
                .nextPage(ConsigneeNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }

        }

        "to Consignee Address page, when Consignee Address does not exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsigneeAddressPage)

              navigator
                .nextPage(ConsigneeNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeAddressController.onPageLoad(answers.id, CheckMode))
          }

        }

      }

      "must go from Consignee Address page to Check Your Answers page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers

            navigator
              .nextPage(ConsigneeAddressPage, CheckMode, updatedAnswers)
              .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }
    }
  }
}
