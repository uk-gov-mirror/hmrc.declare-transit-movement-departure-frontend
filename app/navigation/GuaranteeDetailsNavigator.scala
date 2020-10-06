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
    case LiabilityAmountPage => ua => Some(routes.AccessCodeController.onPageLoad(ua.id, NormalMode))
    case AccessCodePage => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case GuaranteeTypePage => ua => guaranteeTypeRoute(ua, CheckMode)
    case _ => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  def guaranteeTypeRoute(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(GuaranteeTypePage), ua.get(GuaranteeReferencePage), mode) match {
      case (Some(guaranteeType), _, NormalMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, NormalMode))

      case (Some(guaranteeType), None, NormalMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, NormalMode))

      case (Some(guaranteeType), Some(_), CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, CheckMode))

      case (Some(guaranteeType), None, CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))

      case (Some(FlatRateVoucher), Some(guaranteeReference), CheckMode) => 
        guaranteeReference.length match {
          case 24 => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
          case _  => Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, CheckMode))
        }

      case (Some(guaranteeType), Some(guaranteeReference), CheckMode) if guaranteeReferenceRoute.contains(guaranteeType)  => { //NOTE: this is the catch for ^^
        guaranteeReference.length match {
          case 17 => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))
          case _ => Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, CheckMode))
        }
      }

      case _ => Some(routes.AccessCodeController.onPageLoad(ua.id, mode))
    }
  // format: on
}
