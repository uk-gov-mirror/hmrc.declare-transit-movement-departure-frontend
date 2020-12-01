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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends UserAnswersEntryGenerators with TryValues {
  self: Generators =>

  /**
    * The max number of QuestionPage and valid answers that are generated
    *
    * The larger this number the more QuestionPages and answers are generated
    * used in the UserAnswers that is returned. Larger values will increase test
    * runtime and should therefore have a sensible value that still provide
    * confidence that the tests are robust.
    *
    * @note The value must be greater than 0 but less than the number of elements in the
    *       class member `generators`.
    */
  val maxNumberOfGeneratedPageAnswers: Int = 1

  final val userAnswersGeneratorPairs: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitraryCarrierNameUserAnswersEntry.arbitrary ::
      arbitraryCarrierEoriUserAnswersEntry.arbitrary ::
      arbitraryCarrierAddressUserAnswersEntry.arbitrary ::
      arbitraryAddCarrierUserAnswersEntry.arbitrary ::
      arbitraryAddCarrierEoriUserAnswersEntry.arbitrary ::
      arbitrarySafetyAndSecurityConsigneeNameUserAnswersEntry.arbitrary ::
      arbitrarySafetyAndSecurityConsigneeEoriUserAnswersEntry.arbitrary ::
      arbitrarySafetyAndSecurityConsigneeAddressUserAnswersEntry.arbitrary ::
      arbitraryAddSafetyAndSecurityConsigneeEoriUserAnswersEntry.arbitrary ::
      arbitraryAddSafetyAndSecurityConsigneeUserAnswersEntry.arbitrary ::
      arbitrarySafetyAndSecurityConsignorNameUserAnswersEntry.arbitrary ::
      arbitrarySafetyAndSecurityConsignorEoriUserAnswersEntry.arbitrary ::
      arbitrarySafetyAndSecurityConsignorAddressUserAnswersEntry.arbitrary ::
      arbitraryAddSafetyAndSecurityConsignorEoriUserAnswersEntry.arbitrary ::
      arbitraryAddSafetyAndSecurityConsignorUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherCountryOfRoutingUserAnswersEntry.arbitrary ::
      arbitraryCountryOfRoutingUserAnswersEntry.arbitrary ::
      arbitraryPlaceOfUnloadingCodeUserAnswersEntry.arbitrary ::
      arbitraryAddPlaceOfUnloadingCodeUserAnswersEntry.arbitrary ::
      arbitraryConveyanceReferenceNumberUserAnswersEntry.arbitrary ::
      arbitraryAddConveyancerReferenceNumberUserAnswersEntry.arbitrary ::
      arbitraryCommercialReferenceNumberAllItemsUserAnswersEntry.arbitrary ::
      arbitraryAddCommercialReferenceNumberAllItemsUserAnswersEntry.arbitrary ::
      arbitraryTransportChargesPaymentMethodUserAnswersEntry.arbitrary ::
      arbitraryCircumstanceIndicatorUserAnswersEntry.arbitrary ::
      arbitraryAddCommercialReferenceNumberUserAnswersEntry.arbitrary ::
      arbitraryAddTransportChargesPaymentMethodUserAnswersEntry.arbitrary ::
      arbitraryAddCircumstanceIndicatorUserAnswersEntry.arbitrary ::
      arbitrarySecurityConsigneeNameUserAnswersEntry.arbitrary ::
      arbitrarySecurityConsignorNameUserAnswersEntry.arbitrary ::
      arbitrarySecurityConsigneeAddressUserAnswersEntry.arbitrary ::
      arbitrarySecurityConsigneeEoriUserAnswersEntry.arbitrary ::
      arbitrarySecurityConsignorAddressUserAnswersEntry.arbitrary ::
      arbitrarySecurityConsigneeEoriUserAnswersEntry.arbitrary ::
      arbitrarySecurityConsignorEoriUserAnswersEntry.arbitrary ::
      arbitraryAddSecurityConsigneesEoriUserAnswersEntry.arbitrary ::
      arbitraryAddSecurityConsignorsEoriUserAnswersEntry.arbitrary ::
      arbitraryDangerousGoodsCodeUserAnswersEntry.arbitrary ::
      arbitraryAddDangerousGoodsCodeUserAnswersEntry.arbitrary ::
      arbitraryCommercialReferenceNumberUserAnswersEntry.arbitrary ::
      arbitraryTransportChargesUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherDocumentUserAnswersEntry.arbitrary ::
      arbitraryDocumentExtraInformationUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemoveDocumentUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemovePreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryDocumentReferenceUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemovePreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryAddExtraDocumentInformationUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemovePreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryAddDocumentsUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemovePreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryExtraInformationUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherPreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryRemoveSpecialMentionUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherSpecialMentionUserAnswersEntry.arbitrary ::
      arbitrarySpecialMentionAdditionalInfoUserAnswersEntry.arbitrary ::
      arbitrarySpecialMentionTypeUserAnswersEntry.arbitrary ::
      arbitraryAddSpecialMentionUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherPreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryContainerNumberUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherPreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryAddItemsSameConsignorForAllItemsUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherPreviousAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryPreviousReferenceUserAnswersEntry.arbitrary ::
      arbitraryExtraInformationUserAnswersEntry.arbitrary ::
      arbitraryPreviousReferenceUserAnswersEntry.arbitrary ::
      arbitraryAddExtraInformationUserAnswersEntry.arbitrary ::
      arbitraryAddItemsSameConsignorForAllItemsUserAnswersEntry.arbitrary ::
      arbitraryHowManyPackagesUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherPackageUserAnswersEntry.arbitrary ::
      arbitraryDeclareMarkUserAnswersEntry.arbitrary ::
      arbitraryAddMarkUserAnswersEntry.arbitrary ::
      arbitraryTotalPiecesUserAnswersEntry.arbitrary ::
      arbitraryDeclareNumberOfPackagesUserAnswersEntry.arbitrary ::
      arbitraryPreviousReferenceUserAnswersEntry.arbitrary ::
      arbitraryAddExtraInformationUserAnswersEntry.arbitrary ::
      arbitraryReferenceTypeUserAnswersEntry.arbitrary ::
      arbitraryDocumentTypeUserAnswersEntry.arbitrary ::
      arbitraryAddAdministrativeReferenceUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemoveItemUserAnswersEntry.arbitrary ::
      arbitraryTotalPackagesUserAnswersEntry.arbitrary ::
      arbitraryTotalNetMassUserAnswersEntry.arbitrary ::
      arbitraryCommodityCodeUserAnswersEntry.arbitrary ::
      arbitraryTotalNetMassUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignorEoriNumberUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignorEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsignorAddressUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsigneeNameUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsigneeEoriNumberUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsigneeEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryTraderDetailsConsigneeAddressUserAnswersEntry.arbitrary ::
      arbitraryTotalNetMassUserAnswersEntry.arbitrary ::
      arbitraryAddTotalNetMassUserAnswersEntry.arbitrary ::
      arbitraryIsCommodityCodeKnownUserAnswersEntry.arbitrary ::
      arbitraryAddTotalNetMassUserAnswersEntry.arbitrary ::
      arbitraryItemDescriptionUserAnswersEntry.arbitrary ::
      arbitraryOtherReferenceLiabilityAmountUserAnswersEntry.arbitrary ::
      arbitraryItemTotalGrossMassUserAnswersEntry.arbitrary ::
      arbitraryPreLodgeDeclarationUserAnswersEntry.arbitrary ::
      arbitraryItemDescriptionUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemoveSealsUserAnswersEntry.arbitrary ::
      arbitraryGuaranteeTypeUserAnswersEntry.arbitrary ::
      arbitraryOtherReferenceUserAnswersEntry.arbitrary ::
      arbitraryGuaranteeReferenceUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemoveOfficeOfTransitUserAnswersEntry.arbitrary ::
      arbitrarySealsInformationUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherTransitOfficeUserAnswersEntry.arbitrary ::
      arbitraryArrivalTimesAtOfficeUserAnswersEntry.arbitrary ::
      arbitraryControlResultDateLimitUserAnswersEntry.arbitrary ::
      arbitrarySealIdDetailsUserAnswersEntry.arbitrary ::
      arbitraryAddSealsUserAnswersEntry.arbitrary ::
      arbitraryCustomsApprovedLocationUserAnswersEntry.arbitrary ::
      arbitraryAddCustomsApprovedLocationUserAnswersEntry.arbitrary ::
      arbitraryTotalGrossMassUserAnswersEntry.arbitrary ::
      arbitraryAuthorisedLocationCodeUserAnswersEntry.arbitrary ::
      arbitraryTotalPackagesUserAnswersEntry.arbitrary ::
      arbitraryDeclarePackagesUserAnswersEntry.arbitrary ::
      arbitraryAddTransitOfficeUserAnswersEntry.arbitrary ::
      arbitraryModeAtBorderUserAnswersEntry.arbitrary ::
      arbitraryNationalityCrossingBorderUserAnswersEntry.arbitrary ::
      arbitraryModeCrossingBorderUserAnswersEntry.arbitrary ::
      arbitraryInlandModeUserAnswersEntry.arbitrary ::
      arbitraryDestinationOfficeUserAnswersEntry.arbitrary ::
      arbitraryAddIdAtDepartureUserAnswersEntry.arbitrary ::
      arbitraryIdCrossingBorderUserAnswersEntry.arbitrary ::
      arbitraryDestinationCountryUserAnswersEntry.arbitrary ::
      arbitraryChangeAtBorderUserAnswersEntry.arbitrary ::
      arbitraryNationalityAtDepartureUserAnswersEntry.arbitrary ::
      arbitraryIdAtDepartureUserAnswersEntry.arbitrary ::
      arbitraryConsigneeAddressUserAnswersEntry.arbitrary ::
      arbitraryPrincipalAddressUserAnswersEntry.arbitrary ::
      arbitraryConsignorAddressUserAnswersEntry.arbitrary ::
      arbitraryOfficeOfDepartureUserAnswersEntry.arbitrary ::
      arbitraryConsigneeNameUserAnswersEntry.arbitrary ::
      arbitraryWhatIsConsigneeEoriUserAnswersEntry.arbitrary ::
      arbitraryCountryOfDispatchUserAnswersEntry.arbitrary ::
      arbitraryConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryAddConsigneeUserAnswersEntry.arbitrary ::
      arbitraryIsConsigneeEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryConsignorEoriUserAnswersEntry.arbitrary ::
      arbitraryAddConsignorUserAnswersEntry.arbitrary ::
      arbitraryIsConsignorEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryPrincipalNameUserAnswersEntry.arbitrary ::
      arbitraryIsPrincipalEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryWhatIsPrincipalEoriUserAnswersEntry.arbitrary ::
      arbitraryRepresentativeCapacityUserAnswersEntry.arbitrary ::
      arbitraryRepresentativeNameUserAnswersEntry.arbitrary ::
      arbitraryContainersUsedUserAnswersEntry.arbitrary ::
      arbitraryDeclarationForSomeoneElseUserAnswersEntry.arbitrary ::
      arbitraryDeclarationPlaceUserAnswersEntry.arbitrary ::
      arbitraryProcedureTypeUserAnswersEntry.arbitrary ::
      arbitraryDeclarationTypeUserAnswersEntry.arbitrary ::
      arbitraryAddSecurityDetailsUserAnswersEntry.arbitrary ::
      arbitraryAccessCodeUserAnswersEntry.arbitrary ::
      arbitraryOtherReferenceUserAnswersEntry.arbitrary ::
      arbitraryLiabilityAmountUserAnswersEntry.arbitrary ::
      arbitraryGuaranteeReferenceUserAnswersEntry.arbitrary ::
      arbitraryConfirmRemoveOfficeOfTransitUserAnswersEntry.arbitrary ::
      arbitrarySealsInformationUserAnswersEntry.arbitrary ::
      arbitraryAddAnotherTransitOfficeUserAnswersEntry.arbitrary ::
      arbitraryArrivalTimesAtOfficeUserAnswersEntry.arbitrary ::
      arbitraryControlResultDateLimitUserAnswersEntry.arbitrary ::
      arbitrarySealIdDetailsUserAnswersEntry.arbitrary ::
      arbitraryAddSealsUserAnswersEntry.arbitrary ::
      arbitraryCustomsApprovedLocationUserAnswersEntry.arbitrary ::
      arbitraryAddCustomsApprovedLocationUserAnswersEntry.arbitrary ::
      arbitraryTotalGrossMassUserAnswersEntry.arbitrary ::
      arbitraryAuthorisedLocationCodeUserAnswersEntry.arbitrary ::
      arbitraryTotalPackagesUserAnswersEntry.arbitrary ::
      arbitraryDeclarePackagesUserAnswersEntry.arbitrary ::
      arbitraryAddTransitOfficeUserAnswersEntry.arbitrary ::
      arbitraryModeAtBorderUserAnswersEntry.arbitrary ::
      arbitraryNationalityCrossingBorderUserAnswersEntry.arbitrary ::
      arbitraryModeCrossingBorderUserAnswersEntry.arbitrary ::
      arbitraryInlandModeUserAnswersEntry.arbitrary ::
      arbitraryDestinationOfficeUserAnswersEntry.arbitrary ::
      arbitraryAddIdAtDepartureUserAnswersEntry.arbitrary ::
      arbitraryIdCrossingBorderUserAnswersEntry.arbitrary ::
      arbitraryDestinationCountryUserAnswersEntry.arbitrary ::
      arbitraryChangeAtBorderUserAnswersEntry.arbitrary ::
      arbitraryNationalityAtDepartureUserAnswersEntry.arbitrary ::
      arbitraryIdAtDepartureUserAnswersEntry.arbitrary ::
      arbitraryConsigneeAddressUserAnswersEntry.arbitrary ::
      arbitraryPrincipalAddressUserAnswersEntry.arbitrary ::
      arbitraryConsignorAddressUserAnswersEntry.arbitrary ::
      arbitraryOfficeOfDepartureUserAnswersEntry.arbitrary ::
      arbitraryConsigneeNameUserAnswersEntry.arbitrary ::
      arbitraryWhatIsConsigneeEoriUserAnswersEntry.arbitrary ::
      arbitraryCountryOfDispatchUserAnswersEntry.arbitrary ::
      arbitraryConsignorNameUserAnswersEntry.arbitrary ::
      arbitraryAddConsigneeUserAnswersEntry.arbitrary ::
      arbitraryIsConsigneeEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryConsignorEoriUserAnswersEntry.arbitrary ::
      arbitraryAddConsignorUserAnswersEntry.arbitrary ::
      arbitraryIsConsignorEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryPrincipalNameUserAnswersEntry.arbitrary ::
      arbitraryIsPrincipalEoriKnownUserAnswersEntry.arbitrary ::
      arbitraryWhatIsPrincipalEoriUserAnswersEntry.arbitrary ::
      arbitraryRepresentativeCapacityUserAnswersEntry.arbitrary ::
      arbitraryRepresentativeNameUserAnswersEntry.arbitrary ::
      arbitraryContainersUsedUserAnswersEntry.arbitrary ::
      arbitraryDeclarationForSomeoneElseUserAnswersEntry.arbitrary ::
      arbitraryDeclarationPlaceUserAnswersEntry.arbitrary ::
      arbitraryProcedureTypeUserAnswersEntry.arbitrary ::
      arbitraryDeclarationTypeUserAnswersEntry.arbitrary ::
      arbitraryAddSecurityDetailsUserAnswersEntry.arbitrary ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id         <- arbitrary[LocalReferenceNumber]
        eoriNumber <- arbitrary[EoriNumber]
        data <- userAnswersGeneratorPairs match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(userAnswersGeneratorPairs))
        }
      } yield
        UserAnswers(
          id         = id,
          eoriNumber = eoriNumber,
          data = data.foldLeft(Json.obj()) {
            case (obj, (path, value)) =>
              obj.setObject(path.path, value).get
          }
        )
    }
  }
}
