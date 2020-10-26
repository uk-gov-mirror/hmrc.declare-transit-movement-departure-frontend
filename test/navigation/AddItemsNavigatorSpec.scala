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
import controllers.addItems.{routes => addItemsRoutes}
import generators.Generators
import models.reference.PackageType
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new AddItemsNavigator

  "Add Items section" - {
    "in normal mode" - {
      "must go from item description page to total gross mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), NormalMode, answers)
              .mustBe(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from total gross mass page to add total net mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemTotalGrossMassPage(index), NormalMode, answers)
              .mustBe(addItemsRoutes.AddTotalNetMassController.onPageLoad(answers.id, index, NormalMode))
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
              .mustBe(addItemsRoutes.TotalNetMassController.onPageLoad(answers.id, index, NormalMode))
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
              .mustBe(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(answers.id, index, NormalMode))
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
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
              .mustBe(addItemsRoutes.CommodityCodeController.onPageLoad(answers.id, index, NormalMode))
        }
      }
      "must go from CommodityCodePage to CYA page" in { //todo update when traderdetails pages built

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CommodityCodePage(index), NormalMode, answers)
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage, packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.HowManyPackagesController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go to DeclareNumberOfPackages when PackageType code is bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkOrUnpackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage, packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.DeclareNumberOfPackagesController.onPageLoad(answers.id, NormalMode))
            }
          }
        }

        "HowManyPackages" - {

          "must go to DeclareMark when PackageType code isn't bulk or unpacked" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage, packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.DeclareMarkController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage, packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.AddMarkController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage, packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.TotalPiecesController.onPageLoad(answers.id, NormalMode))
            }
          }

        }

        "DeclareNumberOfPackages" - {

          "must go to HowManyPackages if answer is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(DeclareNumberOfPackagesPage, true)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.HowManyPackagesController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go to AddMark if answer is 'No' and PackageType is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(DeclareNumberOfPackagesPage, false)
                  .success
                  .value
                  .set(PackageTypePage, packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.AddMarkController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go to TotalPieces if answer is 'No' and PackageType is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(DeclareNumberOfPackagesPage, false)
                  .success
                  .value
                  .set(PackageTypePage, packageType.code)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.TotalPiecesController.onPageLoad(answers.id, NormalMode))
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
                  .nextPage(TotalPiecesPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.AddMarkController.onPageLoad(answers.id, NormalMode))
            }
          }
        }

        "AddMark" - {

          "must go to DeclareMark if answers is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage, true)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.DeclareMarkController.onPageLoad(answers.id, NormalMode))
            }
          }

          "must go to AddAnotherPackage if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage, false)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.AddAnotherPackageController.onPageLoad(answers.id, NormalMode))
            }
          }
        }

        "DeclareMark" - {

          "must go to AddAnotherPackage" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, declareMark) =>
                val updatedAnswers = answers
                  .set(DeclareMarkPage, declareMark)
                  .success
                  .value

                navigator
                  .nextPage(DeclareMarkPage, NormalMode, updatedAnswers)
                  .mustBe(addItemsRoutes.AddAnotherPackageController.onPageLoad(answers.id, NormalMode))
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
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total grass mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ItemTotalGrossMassPage(index), "100").success.value
            navigator
              .nextPage(ItemTotalGrossMassPage(index), CheckMode, updatedAnswers)
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total net mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(TotalNetMassPage(index), "100").success.value
            navigator
              .nextPage(TotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
              .mustBe(addItemsRoutes.TotalNetMassController.onPageLoad(answers.id, index, CheckMode))
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
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from commodity code page page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CommodityCodePage(index), CheckMode, answers)
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
              .mustBe(addItemsRoutes.CommodityCodeController.onPageLoad(answers.id, index, CheckMode))
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
              .mustBe(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

    }
  }

}
