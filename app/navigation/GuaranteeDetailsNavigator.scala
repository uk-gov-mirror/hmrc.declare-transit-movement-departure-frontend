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

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case GuaranteeTypePage =>
      ua =>
        guaranteeTypeRoute(ua, NormalMode)
    case OtherReferencePage =>
      ua =>
        Some(routes.LiabilityAmountController.onPageLoad(ua.id, NormalMode))
    case GuaranteeReferencePage =>
      ua =>
        Some(routes.LiabilityAmountController.onPageLoad(ua.id, NormalMode))
    case LiabilityAmountPage =>
      ua =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, NormalMode))
//    case AccessCodePage => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id))

  }
//TODO change to CYA when CYA is merged
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case GuaranteeTypePage =>
      ua =>
        guaranteeTypeRoute(ua, CheckMode)
    case OtherReferencePage =>
      ua =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
    case LiabilityAmountPage =>
      ua =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
    case AccessCodePage =>
      ua =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
    case GuaranteeReferencePage =>
      ua =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
  }

  def guaranteeTypeRoute(ua: UserAnswers, mode: Mode) =
    (ua.get(GuaranteeTypePage), ua.get(GuaranteeReferencePage), mode) match {
      case (Some(GuaranteeWaiver) | Some(ComprehensiveGuarantee) | Some(IndividualGuarantee) | Some(FlatRateVoucher) | Some(IndividualGuaranteeMultiple),
            _,
            NormalMode) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, NormalMode))

      case (Some(CashDepositGuarantee) | Some(GuaranteeNotRequired) | Some(GuaranteeWaivedRedirect) | Some(GuaranteeWaiverByAgreement) | Some(
              GuaranteeWaiverSecured),
            None,
            NormalMode) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, NormalMode))

      case (Some(CashDepositGuarantee) | Some(GuaranteeNotRequired) | Some(GuaranteeWaivedRedirect) | Some(GuaranteeWaiverByAgreement) | Some(
              GuaranteeWaiverSecured),
            Some(_),
            CheckMode) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, CheckMode))

      case (Some(CashDepositGuarantee) | Some(GuaranteeNotRequired) | Some(GuaranteeWaivedRedirect) | Some(GuaranteeWaiverByAgreement) | Some(
              GuaranteeWaiverSecured),
            None,
            CheckMode) =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))

      case (Some(FlatRateVoucher), Some(guaranteeReference), CheckMode) =>
        guaranteeReference.length match {
          case 24 => Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
          case _  => Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, CheckMode))
        }

      case (Some(GuaranteeWaiver) | Some(ComprehensiveGuarantee) | Some(IndividualGuarantee) | Some(IndividualGuaranteeMultiple),
            Some(guaranteeReference),
            CheckMode) =>
        guaranteeReference.length match {
          case 17 => Some(routes.AccessCodeController.onPageLoad(ua.id, CheckMode))
          case _  => Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, CheckMode))
        }
      case (Some(CashDepositGuarantee) | Some(GuaranteeNotRequired) | Some(GuaranteeWaivedRedirect) | Some(GuaranteeWaiverByAgreement) | Some(
              GuaranteeWaiverSecured),
            None,
            CheckMode) =>
        Some(routes.AccessCodeController.onPageLoad(ua.id, NormalMode))

      case _ => Some(routes.AccessCodeController.onPageLoad(ua.id, mode))

    }

}
