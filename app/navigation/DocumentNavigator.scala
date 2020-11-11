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
import pages.addItems.{AddDocumentsPage, DocumentTypePage}
import play.api.mvc.Call
import controllers.addItems.routes
import javax.inject.{Inject, Singleton}

@Singleton
class DocumentNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddDocumentsPage(index) => ua => addDocumentsPageRoute(ua, index)

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???

  def addDocumentsPageRoute(ua: UserAnswers, index: Index) =
    ua.get(AddDocumentsPage(index)) match {
      case Some(true)  => Some(routes.DocumentTypeController.onPageLoad(ua.id, index,Index(count(index)(ua)), NormalMode))
      case Some(false) => ???
    }

  private val count: Index => UserAnswers => Int =
    index => ua => ua.get(DeriveNumberOfDocuments(index)).getOrElse(0)

  // format: on

}
