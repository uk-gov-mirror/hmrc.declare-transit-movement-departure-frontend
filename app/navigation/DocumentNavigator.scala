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
import models.{Index, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.addItems.{AddDocumentsPage, AddExtraDocumentInformationPage, DocumentExtraInformationPage, DocumentReferencePage, DocumentTypePage}
import play.api.mvc.Call
import controllers.addItems.routes
import javax.inject.{Inject, Singleton}

@Singleton
class DocumentNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddDocumentsPage(index) => ua => addDocumentsPageRoute(ua, index)
    case DocumentTypePage(index, documentIndex) => ua => Some(routes.DocumentReferenceController.onPageLoad(ua.id, index, NormalMode))
    case DocumentReferencePage(index) => ua => Some(routes.AddExtraDocumentInformationController.onPageLoad(ua.id, index, Index(count(index)(ua)), NormalMode))
    case AddExtraDocumentInformationPage(index, documentIndex) => ua => addExtraDocumentInformationRoute(ua, index, documentIndex, NormalMode)
    case DocumentExtraInformationPage(index, documentIndex) => ua => Some(routes.AddAnotherDocumentController.onPageLoad(ua.id, index, NormalMode))

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???

  def addExtraDocumentInformationRoute(ua:UserAnswers, index:Index, documentIndex:Index, mode:Mode) =
    ua.get(AddExtraDocumentInformationPage(index, documentIndex)) match {
      case Some(true) => Some(routes.DocumentExtraInformationController.onPageLoad(ua.id, index,documentIndex, NormalMode))
      case Some(false) => Some(routes.AddAnotherDocumentController.onPageLoad(ua.id, index, NormalMode))
    }

  def addDocumentsPageRoute(ua: UserAnswers, index: Index) =
    ua.get(AddDocumentsPage(index)) match {
      case Some(true)  => Some(routes.DocumentTypeController.onPageLoad(ua.id, index,Index(count(index)(ua)), NormalMode))
      case Some(false) => ???
    }

  private val count: Index => UserAnswers => Int =
    index => ua => ua.get(DeriveNumberOfDocuments(index)).getOrElse(0)

  // format: on

}
