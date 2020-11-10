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
import derivable.DeriveNumberOfDocuments
import models.{Index, NormalMode, UserAnswers}
import pages.Page
import pages.addItems.AddDocumentsPage
import play.api.mvc.Call

class DocumentNavigator extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddDocumentsPage(index) =>
      ua =>
        Some(controllers.addItems.routes.DocumentTypeController.onPageLoad(ua.id, index, Index(count(index, ua)), NormalMode))

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???

  private def count(index: Index, ua: UserAnswers): Int = ua.get(DeriveNumberOfDocuments(index)).getOrElse(0)
}
