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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.{MovementDetails, MovementDetailsSpec, RouteDetails, RouteDetailsSpec}
import models.reference.CountryCode
import models.{NormalMode, ProcedureType, Status}
import navigation.MovementDetailsNavigator
import org.scalacheck.{Arbitrary, Gen}
import pages.{CountryOfDispatchPage, ProcedureTypePage}

class TaskListViewModelSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with UserAnswersSpecHelper {

  val movementSectionName     = "declarationSummary.section.movementDetails"
  val tradersSectionName      = "declarationSummary.section.tradersDetails"
  val transportSectionName    = "declarationSummary.section.transport"
  val routeSectionName        = "declarationSummary.section.routes"
  val addItemsSectionName     = "declarationSummary.section.addItems"
  val goodsSummarySectionName = "declarationSummary.section.goodsSummary"
  val guaranteeSectionName    = "declarationSummary.section.guarantee"

  "TaskListViewModelSpec" - {

    "MovementDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(movementSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(movementSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[ProcedureType]) {
            procedure =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(procedure)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(movementSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {
          forAll(arb[MovementDetails]) {
            movementDetails =>
              val userAnswers = MovementDetailsSpec.setMovementDetails(movementDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(movementSectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[ProcedureType]) {
            procedure =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(procedure)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(movementSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {
          forAll(arb[MovementDetails]) {
            movementDetails =>
              val userAnswers = MovementDetailsSpec.setMovementDetails(movementDetails)(emptyUserAnswers)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(movementSectionName).value mustEqual expectedHref
          }

        }
      }
    }

    "RouteDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(routeSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(routeSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arb[CountryCode]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(routeSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {
          forAll(arb[TransitInformation]) {
            ti =>
              forAll(arbitraryRouteDetails(Arbitrary(Gen.const(ti))).arbitrary) {
                sectionDetails =>
                  val userAnswers = RouteDetailsSpec.setRouteDetails(sectionDetails, None)(emptyUserAnswers)

                  val viewModel = TaskListViewModel(userAnswers)

                  viewModel.getStatus(routeSectionName).value mustEqual Status.Completed
              }

          }
        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(routeSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[CountryCode]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(routeSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {
          forAll(arb[TransitInformation]) {
            ti =>
              forAll(arbitraryRouteDetails(Arbitrary(Gen.const(ti))).arbitrary) {
                sectionDetails =>
                  val userAnswers = RouteDetailsSpec.setRouteDetails(sectionDetails, None)(emptyUserAnswers)

                  val viewModel = TaskListViewModel(userAnswers)

                  val expectedHref: String = controllers.routeDetails.routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url

                  viewModel.getHref(routeSectionName).value mustEqual expectedHref
              }
          }
        }
      }
    }

    "TransportDetail" ignore {
      "section task is always included" ignore {}

      "status" - {
        "is Not started when there are no answers for the section" ignore {}

        "is InProgress when the first question for the section has been answered" ignore {}

        "is Completed when all the answers are completed" ignore {}
      }

      "navigation" - {
        "when the status is Not started, links to the first page" ignore {}

        "when the status is InProgress, links to the first page" ignore {}

        "when the status is Completed, links to the Check your answers page for the section" ignore {}
      }
    }

    "TraderDetails" ignore {
      "section task is always included" ignore {}

      "status" - {
        "is Not started when there are no answers for the section" ignore {}

        "is InProgress when the first question for the section has been answered" ignore {}

        "is Completed when all the answers are completed" ignore {}
      }

      "navigation" - {
        "when the status is Not started, links to the first page" ignore {}

        "when the status is InProgress, links to the first page" ignore {}

        "when the status is Completed, links to the Check your answers page for the section" ignore {}
      }
    }

    "SecurityDetails" ignore {
      "section task" - {
        "is included when user has chosen to add Security Details" ignore {}

        "is not included when user has chosen to not add Security Details" ignore {}
      }

      "status" - {
        "is Not started when there are no answers for the section" ignore {}

        "is InProgress when the first question for the section has been answered" ignore {}

        "is Completed when all the answers are completed" ignore {}
      }

      "navigation" - {
        "when the status is Not started, links to the first page" ignore {}

        "when the status is InProgress, links to the first page" ignore {}

        "when the status is Completed, links to the Check your answers page for the section" ignore {}
      }
    }

    "ItemsDetails" ignore {
      "section task is always included" ignore {}

      "status" - {
        "is Not started when there are no answers for the section" ignore {}

        "is InProgress when the first question for the section has been answered" ignore {}

        "is Completed when all the answers are completed" ignore {}
      }

      "navigation" - {
        "when the status is Not started, links to the first page" ignore {}

        "when the status is InProgress, links to the first page" ignore {}

        "when the status is Completed, links to the Check your answers page for the section" ignore {}
      }
    }

    "GoodsSummaryDetails" ignore {
      "section task is always included" ignore {}

      "status" - {
        "is Not started when there are no answers for the section" ignore {}

        "is InProgress when the first question for the section has been answered" ignore {}

        "is Completed when all the answers are completed" ignore {}
      }

      "navigation" - {
        "when the status is Not started, links to the first page" ignore {}

        "when the status is InProgress, links to the first page" ignore {}

        "when the status is Completed, links to the Check your answers page for the section" ignore {}
      }
    }

    "GuranteeDetails" ignore {
      "section task is always included" ignore {}

      "status" - {
        "is Not started when there are no answers for the section" ignore {}

        "is InProgress when the first question for the section has been answered" ignore {}

        "is Completed when all the answers are completed" ignore {}
      }

      "navigation" - {
        "when the status is Not started, links to the first page" ignore {}

        "when the status is InProgress, links to the first page" ignore {}

        "when the status is Completed, links to the Check your answers page for the section" ignore {}
      }
    }
  }
}

//object Foo {
//  vla asdf = {
//    "section task is always included" in {
//      val viewModel = TaskListViewModel(emptyUserAnswers)
//
//      viewModel.getSection(???) must be(defined)
//    }
//
//    "status" - {
//      "is Not started when there are no answers for the section" in {
//        val viewModel = TaskListViewModel(emptyUserAnswers)
//
//        viewModel.getStatus(???).value mustEqual Status.NotStarted
//      }
//
//      "is InProgress when the first question for the section has been answered" in {
//        forAll(arb[???]) {
//          pageAnswer =>
//            val userAnswers = emptyUserAnswers.unsafeSetVal(???)(pageAnswer)
//
//            val viewModel = TaskListViewModel(userAnswers)
//
//            viewModel.getStatus(???).value mustEqual Status.InProgress
//        }
//      }
//
//      "is Completed when all the answers are completed" in {
//        forAll(arb[MovementDetails]) {
//          sectionDetails =>
//            val userAnswers = ???(sectionDetails)(emptyUserAnswers)
//
//            val viewModel = TaskListViewModel(userAnswers)
//
//            viewModel.getStatus(???).value mustEqual Status.Completed
//        }
//      }
//    }
//
//    "navigation" - {
//      "when the status is Not started, links to the first page" in {
//        val viewModel = TaskListViewModel(emptyUserAnswers)
//
//        val expectedHref: String = controllers.???
//
//        viewModel.getHref(???).value mustEqual expectedHref
//      }
//
//      "when the status is InProgress, links to the first page" in {
//        forAll(arb[???]) {
//          procedure =>
//            val userAnswers = emptyUserAnswers.unsafeSetVal(???)(???)
//
//            val viewModel = TaskListViewModel(userAnswers)
//
//            val expectedHref: String = controllers.???
//
//            viewModel.getHref(???).value mustEqual expectedHref
//        }
//      }
//
//      "when the status is Completed, links to the Check your answers page for the section" in {
//        forAll(arb[???]) {
//          sectionDetails =>
//            val userAnswers = ???(movementDetails)(sectionDetails)
//
//            val viewModel = TaskListViewModel(userAnswers)
//
//            val expectedHref: String = controllers.???
//
//            viewModel.getHref(movementSectionName).value mustEqual expectedHref
//        }
//
//      }
//    }
//  }
//
//}
