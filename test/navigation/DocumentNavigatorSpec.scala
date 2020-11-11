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
import pages.addItems.{AddDocumentsPage, DocumentTypePage}

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


      }
    }


  // format: on

}
