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

import controllers.transportDetails.routes
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class TransportDetailsNavigator @Inject()() extends Navigator {
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddIdAtDepartureLaterPage => ua => Some(routes.NationalityAtDepartureController.onPageLoad(ua.id, NormalMode))
    case InlandModePage => ua => Some(routes.AddIdAtDepartureController.onPageLoad(ua.id, NormalMode))
    case IdAtDeparturePage => ua => Some(routes.NationalityAtDepartureController.onPageLoad(ua.id, NormalMode))
    case NationalityAtDeparturePage => ua => Some(routes.ChangeAtBorderController.onPageLoad(ua.id, NormalMode))
    case ModeAtBorderPage => ua => Some(routes.IdCrossingBorderController.onPageLoad(ua.id, NormalMode))
    case IdCrossingBorderPage => ua => Some(routes.ModeCrossingBorderController.onPageLoad(ua.id, NormalMode))
    case ModeCrossingBorderPage => ua => Some(routes.NationalityCrossingBorderController.onPageLoad(ua.id, NormalMode) )//TODO update when ref data available
    case NationalityCrossingBorderPage => ua => Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case AddIdAtDeparturePage => ua => Some(addIdAtDepartureRoute(ua, NormalMode))
    case ChangeAtBorderPage => ua => Some(changeAtBorderRoute(ua, NormalMode))
    case _ => _ => None
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case InlandModePage => ua => Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case AddIdAtDeparturePage => ua => Some(addIdAtDepartureRoute(ua, CheckMode))
  }

private def addIdAtDepartureRoute (ua: UserAnswers, mode: Mode): Call = {
    (ua.get(AddIdAtDeparturePage), ua.get(IdAtDeparturePage), mode) match {
      case (Some(true), None, _)=> routes.IdAtDepartureController.onPageLoad(ua.id, mode)
      case (Some(true), Some(_), CheckMode )=> routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(false), _, _) => routes.AddIdAtDepartureLaterController.onPageLoad(ua.id)
      case _ => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }

  private def changeAtBorderRoute (ua: UserAnswers, mode: Mode): Call = {
    ua.get(ChangeAtBorderPage) match {
      case Some(true) => routes.ModeAtBorderController.onPageLoad(ua.id, mode)
      case _ => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
}

