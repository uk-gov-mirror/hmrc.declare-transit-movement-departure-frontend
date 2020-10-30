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
import controllers.addItems.traderDetails.{routes => traderRoutes}
import generators.Generators
import models.reference.PackageType
import models.{CheckMode, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.traderDetails._
import queries.ItemsQuery
import controllers.{routes => mainRoutes}

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
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
              .set(AddTotalNetMassPage(index), true).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from add total net mass page to IsCommodityCodeKnownPage if the answer is 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false).success.value
              .remove(TotalNetMassPage(index)).success.value
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
              .mustBe(routes.AddItemsSameConsignorForAllItemsController.onPageLoad(answers.id, index, NormalMode))
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

      "must go from CommodityCodePage to Trader details page" in { //todo update when traderdetails pages built

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CommodityCodePage(index), NormalMode, answers)
              .mustBe(routes.AddItemsSameConsignorForAllItemsController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      //Trader details
      "Trader Details" - {
        //Consignor
        "must go from addItemsSameConsignorForAllItems to" - {
          "AddItemsSameConsigneeForAllItems when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), true).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }

          "ConsignorEoriKnown when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), false).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
        }

        "must go from ConsignorEoriKnown to" - {
          "ConsignorEoriNumber when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage(index), true).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }

          "ConsignorName when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage(index), false).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
        }

        "must go from ConsignorEoriNumber to AddItemsSameConsigneeForAllItems" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorEoriNumberPage(index), NormalMode, answers)
                .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsignorName to ConsignorAddress" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorNamePage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsignorAddress to AddItemsSameConsigneeForAllItems" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorAddressPage(index), NormalMode, answers)
                .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        //Consignee
        "must go from AddItemsSameConsigneeForAllItems to" - {
          "PackageType when All items same Consignor and Consignee true " in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), true).success.value
                  .set(AddItemsSameConsigneeForAllItemsPage(index), true).success.value
                navigator
                  .nextPage(AddItemsSameConsigneeForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.id, index, Index(0), NormalMode))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is false" in {
            (forAll(arbitrary[UserAnswers], arbitrary[Boolean])) {
              (answers, addItemsSameConsigneeForAllItems) =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), false).success.value
                  .set(AddItemsSameConsigneeForAllItemsPage(index), addItemsSameConsigneeForAllItems).success.value
                navigator
                  .nextPage(AddItemsSameConsigneeForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is true but AddItemsSameConsigneeForAllItems is false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsigneeForAllItemsPage(index), false).success.value
                navigator
                  .nextPage(AddItemsSameConsigneeForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }
        }
          "must go from ConsigneeEoriKnown to" - {
            "ConsigneeEoriNumber when true" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(TraderDetailsConsigneeEoriKnownPage(index), true).success.value
                  navigator
                    .nextPage(TraderDetailsConsigneeEoriKnownPage(index), NormalMode, updatedAnswers)
                    .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
              }
            }

            "ConsigneeName when false" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(TraderDetailsConsigneeEoriKnownPage(index), false).success.value
                  navigator
                    .nextPage(TraderDetailsConsigneeEoriKnownPage(index), NormalMode, updatedAnswers)
                    .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
              }
            }
          }

          "must go from ConsigneeEoriNumber to AddItemsSameConsigneeForAllItems" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(TraderDetailsConsigneeEoriNumberPage(index), NormalMode, answers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go from ConsigneeName to ConsigneeAddress" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(TraderDetailsConsigneeNamePage(index), NormalMode, answers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go from ConsigneeAddress to ItemsCYA" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(TraderDetailsConsigneeAddressPage(index), NormalMode, answers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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

      //Trader details
      "Trader Details" - {
        //Consignor
        "must go from addItemsSameConsignorForAllItems to" - {
          "AddItemsSameConsigneeForAllItems when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), true).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, CheckMode))
            }
          }

          "ConsignorEoriKnown when false and ConsignorEoriKnown is empty" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), false).success.value
                  .remove(TraderDetailsConsignorEoriKnownPage(index)).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage(index), CheckMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(updatedAnswers.id, index, CheckMode))
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

                "Items CYA when false and ConsignorEoriKnown is answered" in {
                  forAll(arbitrary[UserAnswers]) {
                    answers =>
                      val updatedAnswers = answers
                        .set(AddItemsSameConsignorForAllItemsPage(index), false).success.value
                        .set(TraderDetailsConsignorEoriNumberPage(index), eoriNumber.value).success.value
                      navigator
                        .nextPage(AddItemsSameConsignorForAllItemsPage(index), CheckMode, updatedAnswers)
                        .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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

            "must go from ConsignorEoriKnown to" - {
              "ConsignorEoriNumber when true and EoriNumber is empty" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(TraderDetailsConsignorEoriKnownPage(index), true).success.value
                      .remove(TraderDetailsConsignorEoriNumberPage(index)).success.value
                    navigator
                      .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                      .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
                }
              }

              "Items CYA  when true and EoriNumber is answered" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(TraderDetailsConsignorEoriKnownPage(index), true).success.value
                      .set(TraderDetailsConsignorEoriNumberPage(index), eoriNumber.value).success.value
                    navigator
                      .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
                }
              }

              "ConsignorName when false and consignorName is empty" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(TraderDetailsConsignorEoriKnownPage(index), false).success.value
                      .remove(TraderDetailsConsignorNamePage(index)).success.value
                    navigator
                      .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                      .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
                }
              }

              "Items CYA when false and ConsignorName is answered" in { //todo: recheck this logic when we merge with packages
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(TraderDetailsConsignorEoriKnownPage(index), false).success.value
                      .set(TraderDetailsConsignorNamePage(index), "name").success.value
                    navigator
                      .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
                }
              }
            }

            "must go from ConsignorEoriNumber to Items CYA" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  navigator
                    .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, answers)
                    .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
              }
            }

            "must go from ConsignorName to" - {
              "ConsignorAddress when Address is empty" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val userAnswers = answers
                      .remove(TraderDetailsConsignorAddressPage(index)).success.value
                    navigator
                      .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
                      .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(userAnswers.id, index, CheckMode))
                }
              }

              "Items CYA when Address is Populated" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val userAnswers = answers
                      .set(TraderDetailsConsignorAddressPage(index), "address").success.value //todo: move to correct model when page completed
                    navigator
                      .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
                }
              }
            }

            "must go from ConsignorAddress to AddItemsSameConsigneeForAllItems" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  navigator
                    .nextPage(TraderDetailsConsignorAddressPage(index), CheckMode, answers)
                    .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, CheckMode))
              }
            }

            //Consignee
            "must go from AddItemsSameConsigneeForAllItems to" - {
              "PackageType when All items same Consignor and Consignee true " in { //todo: move to correct model when page completed
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(AddItemsSameConsignorForAllItemsPage(index), true).success.value
                      .set(AddItemsSameConsigneeForAllItemsPage(index), true).success.value
                    navigator
                      .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, updatedAnswers)
                      .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, CheckMode))
                }
              }

              "AddItems CYA when AddItemsSameConsignorForAllItems is false" in {
                (forAll(arbitrary[UserAnswers], arbitrary[Boolean])) {
                  (answers, addItemsSameConsigneeForAllItems) =>
                    val updatedAnswers = answers
                      .set(AddItemsSameConsignorForAllItemsPage(index), false).success.value
                      .set(AddItemsSameConsigneeForAllItemsPage(index), addItemsSameConsigneeForAllItems).success.value
                    navigator
                      .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, updatedAnswers)
                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
                }
              }

              "AddItems CYA when AddItemsSameConsignorForAllItems is true but AddItemsSameConsigneeForAllItems is false" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(AddItemsSameConsigneeForAllItemsPage(index), false).success.value
                    navigator
                      .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, updatedAnswers)
                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
                }
              }

              "must go from ConsigneeEoriKnown to" - {
                "ConsigneeEoriNumber when true and ConsigneeEoriNumber is empty" in {
                  forAll(arbitrary[UserAnswers]) {
                    answers =>
                      val updatedAnswers = answers
                        .set(TraderDetailsConsigneeEoriKnownPage(index), true).success.value
                        .remove(TraderDetailsConsigneeEoriNumberPage(index)).success.value
                      navigator
                        .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                        .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
                  }

                  "Items CYA when true and ConsigneeEoriNumber is populated" in {
                    forAll(arbitrary[UserAnswers]) {
                      answers =>
                        val updatedAnswers = answers
                          .set(TraderDetailsConsigneeEoriKnownPage(index), true).success.value
                          .set(TraderDetailsConsigneeEoriNumberPage(index), eoriNumber.value).success.value
                        navigator
                          .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                          .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
                    }

                  }

                  "ConsigneeName when false and ConsigneeName is empty" in {
                    forAll(arbitrary[UserAnswers]) {
                      answers =>
                        val updatedAnswers = answers
                          .set(TraderDetailsConsigneeEoriKnownPage(index), false).success.value
                          .remove(TraderDetailsConsigneeNamePage(index)).success.value
                        navigator
                          .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                          .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
                    }
                  }

                  "Items CYA when false and ConsigneeName is empty" in {
                    forAll(arbitrary[UserAnswers]) {
                      answers =>
                        val updatedAnswers = answers
                          .set(TraderDetailsConsigneeEoriKnownPage(index), false).success.value
                          .remove(TraderDetailsConsigneeNamePage(index)).success.value
                        navigator
                          .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                          .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
                    }
                  }
                }

                "must go from ConsigneeEoriNumber to" - { //todo: Check logic when packages added
                  "CYA" in {
                    forAll(arbitrary[UserAnswers]) {
                      answers =>
                        navigator
                          .nextPage(TraderDetailsConsigneeEoriNumberPage(index), CheckMode, answers)
                          .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, CheckMode))
                    }
                  }
                }

                "must go from ConsigneeName to" - {
                  "ConsigneeAddress when address is empty" in {
                    forAll(arbitrary[UserAnswers]) {
                      answers =>
                        val userAnswers = answers
                          .remove(TraderDetailsConsigneeAddressPage(index)).success.value
                        navigator
                          .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
                          .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(userAnswers.id, index, CheckMode))
                    }
                  }

                  "Items CYA when address is populated" in {
                    forAll(arbitrary[UserAnswers]) {
                      answers =>
                        val userAnswers = answers
                          .set(TraderDetailsConsigneeAddressPage(index), "address").success.value //todo: change to correct model
                        navigator
                          .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
                          .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
                    }
                  }
                }

                "must go from ConsigneeAddress to ItemsCYA" in {
                  forAll(arbitrary[UserAnswers]) {
                    answers =>
                      navigator
                        .nextPage(TraderDetailsConsigneeAddressPage(index), CheckMode, answers)
                        .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
                  }
                }
              }
            }
          }
        }
        // format: on
      }
    }
  }
}
