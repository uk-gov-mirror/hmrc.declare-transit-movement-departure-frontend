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

import controllers.addItems.specialMentions.routes
import derivable.DeriveNumberOfSpecialMentions
import javax.inject.{Inject, Singleton}
import models.{Index, NormalMode, UserAnswers}
import pages.Page
import pages.addItems.specialMentions.{AddAnotherSpecialMentionPage, AddSpecialMentionPage, SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}
import play.api.mvc.Call

@Singleton
class SpecialMentionsNavigator @Inject()() extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSpecialMentionPage(index) =>
      userAnswers =>
        Some(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, index, Index(count(index)(userAnswers)), NormalMode))
    case SpecialMentionTypePage(itemIndex, referenceIndex) =>
      userAnswers =>
        Some(routes.SpecialMentionAdditionalInfoController.onPageLoad(userAnswers.id, itemIndex, referenceIndex, NormalMode))
    case SpecialMentionAdditionalInfoPage(itemIndex, _) =>
      userAnswers =>
        Some(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.id, itemIndex, NormalMode))
    case AddAnotherSpecialMentionPage(itemIndex) =>
      userAnswers =>
        Some(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, itemIndex, Index(count(itemIndex)(userAnswers)), NormalMode))

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???

  private val count: Index => UserAnswers => Int =
    index => userAnswers => userAnswers.get(DeriveNumberOfSpecialMentions(index)).getOrElse(0)
}
