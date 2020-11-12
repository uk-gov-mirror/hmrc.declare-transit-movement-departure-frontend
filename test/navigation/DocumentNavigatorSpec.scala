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
import generators.Generators
import models.NormalMode
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.{AddDocumentsPage, AddExtraDocumentInformationPage, DocumentExtraInformationPage, DocumentReferencePage, DocumentTypePage}
import controllers.addItems.routes

class DocumentNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
  val navigator = new DocumentNavigator

  "Document navigator" - {
    "in Normal Mode" - {
      "AddDocumentPage must go to [Page not yes implemented] when user selects 'no'" ignore {

            }

      "AddDocumentPage must go to DocumentTypePage when user selects 'yes'" in {

            val updatedAnswers = emptyUserAnswers
               .set(AddDocumentsPage(index), true).success.value

            navigator
              .nextPage(AddDocumentsPage(index), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.routes.DocumentTypeController.onPageLoad(updatedAnswers.id, index, index, NormalMode))
        }

      "DocumentTypePage must go to DocumentReferencePage" in {

        val updatedAnswers = emptyUserAnswers
          .set(DocumentTypePage(index, documentIndex), "test").success.value

        navigator
          .nextPage(DocumentTypePage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(routes.DocumentReferenceController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }

      "DocumentReferencePage must go to AddExtraDocumentInformation page" in {

        val updatedAnswers = emptyUserAnswers
          .set(DocumentReferencePage(index), "test").success.value

        navigator
          .nextPage(DocumentReferencePage(index), NormalMode, updatedAnswers)
          .mustBe(routes.AddExtraDocumentInformationController.onPageLoad(updatedAnswers.id, index, documentIndex, NormalMode))
      }
        "AddExtraDocumentInformation page must go to" - {
          "DocumentExtraInformationPage when user selects 'Yes' " in {
            val updatedAnswers = emptyUserAnswers
              .set(AddExtraDocumentInformationPage(index, documentIndex), true).success.value

            navigator
              .nextPage(AddExtraDocumentInformationPage(index, documentIndex), NormalMode, updatedAnswers)
              .mustBe(routes.DocumentExtraInformationController.onPageLoad(updatedAnswers.id, index, documentIndex, NormalMode))


          }
          "AddAnotherDocument page when user selects 'No' " in {
            val updatedAnswers = emptyUserAnswers
              .set(AddExtraDocumentInformationPage(index, documentIndex), false).success.value

            navigator
              .nextPage(AddExtraDocumentInformationPage(index, documentIndex), NormalMode, updatedAnswers)
              .mustBe(routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.id, index, NormalMode))


          }
        }
      "DocumentExtraInformationPage must go to AddAnotherDocument" in {

        val updatedAnswers = emptyUserAnswers
          .set(DocumentExtraInformationPage(index, documentIndex), "test").success.value

        navigator
          .nextPage(DocumentExtraInformationPage(index, documentIndex), NormalMode, updatedAnswers)
          .mustBe(routes.AddAnotherDocumentController.onPageLoad(updatedAnswers.id, index, NormalMode))
      }


      }
    }


  // format: on

}
