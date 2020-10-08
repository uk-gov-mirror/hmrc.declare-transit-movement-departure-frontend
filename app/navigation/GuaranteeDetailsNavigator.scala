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

import akka.util.Helpers.Requiring
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call
import pages.guaranteeDetails._
import controllers.guaranteeDetails.routes
import models.GuaranteeType._

@Singleton
class GuaranteeDetailsNavigator @Inject()() extends Navigator {
  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case GuaranteeTypePage => ua => guaranteeTypeRoute(ua, NormalMode)
    case OtherReferencePage => ua => Some(routes.LiabilityAmountController.onPageLoad(ua.id, NormalMode))
    case GuaranteeReferencePage => ua => Some(routes.LiabilityAmountController.onPageLoad(ua.id, NormalMode))
    case LiabilityAmountPage => ua => liabilityAmountRoute(ua, NormalMode)
    case AccessCodePage => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case GuaranteeTypePage => ua => guaranteeTypeRoute(ua, CheckMode)
    case LiabilityAmountPage => ua => liabilityAmountRoute(ua, CheckMode)
    case OtherReferencePage => ua => otherReferenceRoute(ua, CheckMode)
    case GuaranteeReferencePage => ua => guaranteeReferenceRoutes(ua, CheckMode)
    case AccessCodePage => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case _ => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  def otherReferenceRoute (ua:UserAnswers,mode:Mode) =
    ua.get(LiabilityAmountPage) match {
      case None => Some(routes.LiabilityAmountController.onPageLoad(ua.id,CheckMode))
      case _ => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
    }


  def guaranteeReferenceRoutes(ua:UserAnswers,mode:Mode) =
    (ua.get(LiabilityAmountPage), ua.get(AccessCodePage)) match {
      case (None, _) => Some(routes.LiabilityAmountController.onPageLoad(ua.id, CheckMode))
      case (Some(_), None) => Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
      case _ =>  Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))

    }

  def liabilityAmountRoute(ua:UserAnswers, mode:Mode)  = 
    (ua.get(GuaranteeReferencePage),ua.get(AccessCodePage), mode) match {
      case (Some(_),_, NormalMode) => Some(routes.AccessCodeController.onPageLoad(ua.id, NormalMode))
      case (Some(guaranteeType), None,  CheckMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
      case _ =>  Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
    }
  
  def guaranteeTypeRoute(ua: UserAnswers, mode: Mode): Option[Call] = 

      (ua.get(GuaranteeTypePage), ua.get(GuaranteeReferencePage), mode) match {
      case (Some(guaranteeType), _, NormalMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, NormalMode))

      case (Some(guaranteeType), None, NormalMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, NormalMode))

      case (Some(guaranteeType), None, CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, CheckMode))

      case (Some(guaranteeType), Some(_), CheckMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, CheckMode))


      case (Some(guaranteeType), Some(_), CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))

      case (Some(guaranteeType), None , CheckMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, CheckMode))

      case _ => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
    }
  
  // format: on
}
