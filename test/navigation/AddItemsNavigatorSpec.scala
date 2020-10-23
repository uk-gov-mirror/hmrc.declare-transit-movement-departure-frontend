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
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems.{AddItemsSameConsigneeForAllItemsPage, _}
import pages.addItems.traderDetails._

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
              .set(IsCommodityCodeKnownPage(index), false).success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }
      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true).success.value
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

      //Trader details
      "Trader Details" - {
        //Consignor
        "must go from addItemsSameConsignorForAllItems to" - {
          "AddItemsSameConsigneeForAllItems when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, true).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage, NormalMode, updatedAnswers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }

          "ConsignorEoriKnown when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, false).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage, NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
        }

        "must go from ConsignorEoriKnown to" - {
          "ConsignorEoriNumber when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage, true).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }

          "ConsignorName when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage, false).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
        }

        "must go from ConsignorEoriNumber to AddItemsSameConsigneeForAllItems" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorEoriNumberPage, NormalMode, answers)
                .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsignorName to ConsignorAddress" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorNamePage, NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsignorAddress to AddItemsSameConsigneeForAllItems" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorAddressPage, NormalMode, answers)
                .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        //Consignee
        "must go from AddItemsSameConsigneeForAllItems to" - {
          "PackageType when All items same Consignor and Consignee true " in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, true).success.value
                  .set(AddItemsSameConsigneeForAllItemsPage, true).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage, NormalMode, updatedAnswers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is false" in {
            (forAll(arbitrary[UserAnswers], arbitrary[Boolean])) {
              (answers, addItemsSameConsigneeForAllItems) =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, false).success.value
                  .set(AddItemsSameConsigneeForAllItemsPage, addItemsSameConsigneeForAllItems).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage, NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is true but AddItemsSameConsigneeForAllItems is false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsigneeForAllItemsPage, false).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage, NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "must go from ConsigneeEoriKnown to" - {
            "ConsigneeEoriNumber when true" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(TraderDetailsConsigneeEoriKnownPage, true).success.value
                  navigator
                    .nextPage(TraderDetailsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                    .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
              }
            }

            "ConsigneeName when false" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(TraderDetailsConsigneeEoriKnownPage, false).success.value
                  navigator
                    .nextPage(TraderDetailsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                    .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
              }
            }
          }

          "must go from ConsigneeEoriNumber to AddItemsSameConsigneeForAllItems" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(TraderDetailsConsigneeEoriNumberPage, NormalMode, answers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go from ConsigneeName to ConsigneeAddress" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(TraderDetailsConsigneeNamePage, NormalMode, answers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go from ConsigneeAddress to ItemsCYA" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(TraderDetailsConsigneeAddressPage, NormalMode, answers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
              .set(AddTotalNetMassPage(index), true).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true).success.value
              .set(TotalNetMassPage(index), "100.123").success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }
      "must go from add total net mass page to CYA page if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false).success.value
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
              .set(IsCommodityCodeKnownPage(index), true).success.value
              .set(CommodityCodePage(index), "111111").success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true).success.value
              .remove(CommodityCodePage(index)).success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.CommodityCodeController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), false).success.value
              .remove(CommodityCodePage(index)).success.value
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
                  .set(AddItemsSameConsignorForAllItemsPage, true).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage, CheckMode, updatedAnswers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, CheckMode))
            }
          }

          "ConsignorEoriKnown when false and ConsignorEoriKnown is empty" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, false).success.value
                  .remove(TraderDetailsConsignorEoriKnownPage).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage, CheckMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(updatedAnswers.id, index, CheckMode))
            }
          }

          "Items CYA when false and ConsignorEoriKnown is answered" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, false).success.value
                  .set(TraderDetailsConsignorEoriNumberPage, eoriNumber.value).success.value
                navigator
                  .nextPage(AddItemsSameConsignorForAllItemsPage, CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }
        }

        "must go from ConsignorEoriKnown to" - {
          "ConsignorEoriNumber when true and EoriNumber is empty" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage, true).success.value
                  .remove(TraderDetailsConsignorEoriNumberPage).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
            }
          }

          "Items CYA  when true and EoriNumber is answered" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage, true).success.value
                  .set(TraderDetailsConsignorEoriNumberPage, eoriNumber.value).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "ConsignorName when false and consignorName is empty" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage, false).success.value
                  .remove(TraderDetailsConsignorNamePage).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
            }
          }

          "Items CYA when false and ConsignorName is answered" in { //todo: recheck this logic when we merge with packages
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage, false).success.value
                  .set(TraderDetailsConsignorNamePage, "name").success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }
        }

        "must go from ConsignorEoriNumber to Items CYA" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorEoriNumberPage, CheckMode, answers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
          }
        }

        "must go from ConsignorName to" - {
          "ConsignorAddress when Address is empty" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .remove(TraderDetailsConsignorAddressPage).success.value
                navigator
                  .nextPage(TraderDetailsConsignorNamePage, CheckMode, userAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(userAnswers.id, index, CheckMode))
            }
          }

          "Items CYA when Address is Populated" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .set(TraderDetailsConsignorAddressPage, "address").success.value //todo: move to correct model when page completed
                navigator
                  .nextPage(TraderDetailsConsignorNamePage, CheckMode, userAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
            }
          }
        }

        "must go from ConsignorAddress to AddItemsSameConsigneeForAllItems" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorAddressPage, CheckMode, answers)
                .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, CheckMode))
          }
        }

        //Consignee
        "must go from AddItemsSameConsigneeForAllItems to" - {
          "PackageType when All items same Consignor and Consignee true " in { //todo: move to correct model when page completed
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, true).success.value
                  .set(AddItemsSameConsigneeForAllItemsPage, true).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage, CheckMode, updatedAnswers)
                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, CheckMode))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is false" in {
            (forAll(arbitrary[UserAnswers], arbitrary[Boolean])) {
              (answers, addItemsSameConsigneeForAllItems) =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage, false).success.value
                  .set(AddItemsSameConsigneeForAllItemsPage, addItemsSameConsigneeForAllItems).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage, CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is true but AddItemsSameConsigneeForAllItems is false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsigneeForAllItemsPage, false).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage, CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "must go from ConsigneeEoriKnown to" - {
            "ConsigneeEoriNumber when true and ConsigneeEoriNumber is empty" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(TraderDetailsConsigneeEoriKnownPage, true).success.value
                    .remove(TraderDetailsConsigneeEoriNumberPage).success.value
                  navigator
                    .nextPage(TraderDetailsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                    .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
              }

              "Items CYA when true and ConsigneeEoriNumber is populated" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(TraderDetailsConsigneeEoriKnownPage, true).success.value
                      .set(TraderDetailsConsigneeEoriNumberPage, eoriNumber.value).success.value
                    navigator
                      .nextPage(TraderDetailsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
                }

              }

              "ConsigneeName when false and ConsigneeName is empty" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(TraderDetailsConsigneeEoriKnownPage, false).success.value
                      .remove(TraderDetailsConsigneeNamePage).success.value
                    navigator
                      .nextPage(TraderDetailsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                      .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
                }
              }

              "Items CYA when false and ConsigneeName is empty" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val updatedAnswers = answers
                      .set(TraderDetailsConsigneeEoriKnownPage, false).success.value
                      .remove(TraderDetailsConsigneeNamePage).success.value
                    navigator
                      .nextPage(TraderDetailsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                      .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
                }
              }
            }

            "must go from ConsigneeEoriNumber to" - { //todo: Check logic when packages added
              "CYA" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    navigator
                      .nextPage(TraderDetailsConsigneeEoriNumberPage, CheckMode, answers)
                      .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, CheckMode))
                }
              }
            }

            "must go from ConsigneeName to" - {
              "ConsigneeAddress when address is empty" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                  val userAnswers = answers
                    .remove(TraderDetailsConsigneeAddressPage).success.value
                    navigator
                      .nextPage(TraderDetailsConsigneeNamePage, CheckMode, userAnswers)
                      .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(userAnswers.id, index, CheckMode))
                }
              }

              "Items CYA when address is populated" in {
                forAll(arbitrary[UserAnswers]) {
                  answers =>
                    val userAnswers = answers
                      .set(TraderDetailsConsigneeAddressPage, "address").success.value //todo: change to correct model
                    navigator
                      .nextPage(TraderDetailsConsigneeNamePage, CheckMode, userAnswers)
                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
                }
              }
            }

            "must go from ConsigneeAddress to ItemsCYA" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  navigator
                    .nextPage(TraderDetailsConsigneeAddressPage, CheckMode, answers)
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
