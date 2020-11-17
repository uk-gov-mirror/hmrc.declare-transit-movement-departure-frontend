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
import controllers.addItems.securityDetails._
import javax.inject.{Inject, Singleton}
import models._
import pages.Page
import pages.addItems.securityDetails.TransportChargesPage
import play.api.mvc.Call

@Singleton
class SecurityDetailsNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TransportChargesPage(index) => ua => Some(routes.UsingSameCommercialReferenceController.onPageLoad(ua.id, index, NormalMode))
  }


  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???

  // format: on

}
