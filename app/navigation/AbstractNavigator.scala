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

import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import play.api.mvc.Call
import controllers.routes

trait Navigator {
  def defaultPage: Call = routes.IndexController.onPageLoad()

  def nextOptionalPage(page: Page, mode: Mode, userAnswers: UserAnswers): Option[Call]

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    nextOptionalPage(page, mode, userAnswers).getOrElse(defaultPage)
}

abstract class AbstractNavigator extends Navigator {
  protected def normalRoutes: Page => UserAnswers => Call

  protected def checkRouteMap: Page => UserAnswers => Call

  override def nextOptionalPage(page: Page, mode: Mode, userAnswers: UserAnswers): Option[Call] = mode match {
    case NormalMode =>
      Some(normalRoutes(page)(userAnswers))
    case CheckMode =>
      Some(checkRouteMap(page)(userAnswers))
  }
}

