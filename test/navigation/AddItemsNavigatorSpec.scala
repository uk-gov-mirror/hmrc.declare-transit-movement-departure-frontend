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

package navigation

import base.SpecBase
import controllers.addItems.routes
import generators.Generators
import models.reference.PackageType
import models.{CheckMode, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import queries.ItemsQuery

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new AddItemsNavigator

  "Add Items section" - {

    "in normal mode" - {

      "must go from item description page to total gross mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), NormalMode, answers)
              .mustBe(routes.ItemTotalGrossMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from total gross mass page to add total net mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemTotalGrossMassPage(index), NormalMode, answers)
              .mustBe(routes.AddTotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from add total net mass page to total net mass page if the answer is 'Yes' and no answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true)
              .success
              .value
              .remove(TotalNetMassPage(index))
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from add total net mass page to IsCommodityCodeKnownPage if the answer is 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false)
              .success
              .value
              .remove(TotalNetMassPage(index))
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.IsCommodityCodeKnownController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA if the answer is 'No'" in { //todo update when trader details route built

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), false)
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true)
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.CommodityCodeController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from CommodityCodePage to CYA page" in { //todo update when traderdetails pages built

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CommodityCodePage(index), NormalMode, answers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from AddAnotherItem page to" - {

        "ItemDescription page if the answer is 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddAnotherItemPage, false).success.value
              navigator
                .nextPage(AddAnotherItemPage, NormalMode, updatedAnswer)
                .mustBe(routes.DeclarationSummaryController.onPageLoad(answers.id))
          }
        }

        "task list page if the answer is 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers
                .set(AddAnotherItemPage, true)
                .success
                .value
                .set(ItemDescriptionPage(index), "test")
                .success
                .value

              navigator
                .nextPage(AddAnotherItemPage, NormalMode, updatedAnswer)
                .mustBe(routes.ItemDescriptionController.onPageLoad(answers.id, Index(1), NormalMode))
          }
        }
      }

      "must go from ConfirmRemoveItem page to " - {

        "AddAnotherItem page when 'No' is selected and there are more than one item" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ItemDescriptionPage(index), "item1")
                .success
                .value
                .set(ItemDescriptionPage(Index(1)), "item2")
                .success
                .value
                .set(AddAnotherItemPage, true)
                .success
                .value
                .set(ConfirmRemoveItemPage, false)
                .success
                .value
              navigator
                .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.id))
          }
        }

        "AddAnotherItem page when 'Yes' is selected and there are more than one item" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ItemDescriptionPage(index), "item1")
                .success
                .value
                .set(ItemDescriptionPage(Index(1)), "item2")
                .success
                .value
                .set(ConfirmRemoveItemPage, true)
                .success
                .value
              navigator
                .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.id))
          }
        }

        "ItemDescription page when 'Yes' is selected and when all the items are removed" in {

          val updatedAnswers = emptyUserAnswers
            .remove(ItemsQuery(index))
            .success
            .value
            .set(ConfirmRemoveItemPage, true)
            .success
            .value
          navigator
            .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
            .mustBe(routes.ItemDescriptionController.onPageLoad(updatedAnswers.id, index, NormalMode))
        }

      }

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to DeclareNumberOfPackages when PackageType code is bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkOrUnpackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareNumberOfPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }

        "HowManyPackages" - {

          "must go to DeclareMark when PackageType code isn't bulk or unpacked" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType], arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

        }
        "DeclareNumberOfPackages" - {
          "must go to HowManyPackages if answer is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(DeclareNumberOfPackagesPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to AddMark if answer is 'No' and PackageType is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to TotalPieces if answer is 'No' and PackageType is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

        }
        "TotalPieces" - {
          "must go to AddMark" in {
            forAll(arbitrary[UserAnswers], arbitrary[Int]) {
              (answers, totalPieces) =>
                val updatedAnswers = answers
                  .set(TotalPackagesPage, totalPieces)
                  .success
                  .value

                navigator
                  .nextPage(TotalPiecesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }
        "AddMark" - {
          "must go to DeclareMark if answers is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to AddAnotherPackage if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }
        "DeclareMark" - {
          "must go to AddAnotherPackage" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, declareMark) =>
                val updatedAnswers = answers
                  .set(DeclareMarkPage(index, index), declareMark)
                  .success
                  .value

                navigator
                  .nextPage(DeclareMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }
        "AddAnotherPackage" - {
          "must go to PackageType if the answer is 'Yes' and increment package index" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPackagePage(index, index), true)
                  .success
                  .value

                val nextPackageIndex = Index(index.position + 1)

                navigator
                  .nextPage(AddAnotherPackagePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, nextPackageIndex, NormalMode))
            }
          }
        }
      }
    }

    "in check mode" - {

      "must go from item description page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), CheckMode, answers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total grass mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ItemTotalGrossMassPage(index), "100").success.value
            navigator
              .nextPage(ItemTotalGrossMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total net mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(TotalNetMassPage(index), "100").success.value
            navigator
              .nextPage(TotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from add total net mass page to total net mass page if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true)
              .success
              .value
              .remove(TotalNetMassPage(index))
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true)
              .success
              .value
              .set(TotalNetMassPage(index), "100.123")
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false)
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from commodity code page page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CommodityCodePage(index), CheckMode, answers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true)
              .success
              .value
              .set(CommodityCodePage(index), "111111")
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true)
              .success
              .value
              .remove(CommodityCodePage(index))
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.CommodityCodeController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), false)
              .success
              .value
              .remove(CommodityCodePage(index))
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "PackageJourney" - {
        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to DeclareNumberOfPackages when PackageType code is bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkOrUnpackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareNumberOfPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
        }
        "HowManyPackages" - {

          "must go to DeclareMark when PackageType code isn't bulk or unpacked" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType], arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

        }
        "DeclareNumberOfPackages" - {
          "must go to HowManyPackages if answer is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(DeclareNumberOfPackagesPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to AddMark if answer is 'No' and PackageType is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to TotalPieces if answer is 'No' and PackageType is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType.code)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

        }
        "TotalPieces" - {
          "must go to AddMark" in {
            forAll(arbitrary[UserAnswers], arbitrary[Int]) {
              (answers, totalPieces) =>
                val updatedAnswers = answers
                  .set(TotalPackagesPage, totalPieces)
                  .success
                  .value

                navigator
                  .nextPage(TotalPiecesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
        }
        "AddMark" - {
          "must go to DeclareMark if answers is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to CheckYourAnswers if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }
        "DeclareMark" - {
          "must go to CheckYourAnswers" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, declareMark) =>
                val updatedAnswers = answers
                  .set(DeclareMarkPage(index, index), declareMark)
                  .success
                  .value

                navigator
                  .nextPage(DeclareMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }
        "AddAnotherPackage" - {
          "must go to PackageType if the answer is 'Yes' and increment package index" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPackagePage(index, index), true)
                  .success
                  .value

                val nextPackageIndex = Index(index.position + 1)

                navigator
                  .nextPage(AddAnotherPackagePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, nextPackageIndex, CheckMode))
            }
          }
          "must go to CheckYourAnswers if'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPackagePage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(AddAnotherPackagePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }
      }
    }
  }
}
