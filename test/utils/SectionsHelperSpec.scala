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

package utils

import java.time.LocalDate

import base.SpecBase
import controllers.goodsSummary.{routes => goodsSummaryRoutes}
import controllers.guaranteeDetails.{routes => guaranteeDetailsRoutes}
import controllers.movementDetails.{routes => movementDetailsRoutes}
import controllers.routeDetails.{routes => routeDetailsRoutes}
import controllers.traderDetails.{routes => traderDetailsRoutes}
import controllers.transportDetails.{routes => transportDetailsRoutes}
import models.GuaranteeType.{CashDepositGuarantee, GuaranteeNotRequired, GuaranteeWaiver}
import models.ProcedureType.{Normal, Simplified}
import models.Status.{Completed, InProgress, NotStarted}
import models._
import models.reference.{Country, CountryCode, TransportMode}
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}

class SectionsHelperSpec extends SpecBase {

  "SectionsHelper" - {
    "GetSections" - {
      "must include SafetyAndSecurity section when answer Yes to AddSecurityDetailsPage" in {

        val userAnswers              = emptyUserAnswers.set(AddSecurityDetailsPage, true).toOption.value
        val sectionsHelper           = new SectionsHelper(userAnswers)
        val url                      = movementDetailsRoutes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url
        val sectionName              = "declarationSummary.section.movementDetails"
        val safetyAndSecuritySection = "declarationSummary.section.safetyAndSecurity"
        val expectedSections: Seq[SectionDetails] = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted)) :+
          SectionDetails(safetyAndSecuritySection, "", NotStarted)

        sectionsHelper.getSections mustBe expectedSections

      }
    }

    "MovementDetails" - {
      "must return movement details section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

        val url              = movementDetailsRoutes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.movementDetails"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return movement details section with status as InProgress" in {

        val userAnswers = emptyUserAnswers
          .set(DeclarationTypePage, DeclarationType.values.head)
          .toOption
          .value
          .set(ProcedureTypePage, ProcedureType.values.head)
          .toOption
          .value

        val url             = movementDetailsRoutes.ContainersUsedPageController.onPageLoad(lrn, NormalMode).url
        val expectedSection = SectionDetails("declarationSummary.section.movementDetails", url, InProgress)
        val expectedResult  = updateSectionsWithExpectedValue(expectedSection)

        val sectionsHelper = new SectionsHelper(userAnswers)
        val result         = sectionsHelper.getSections

        result mustBe expectedResult
      }

      "must return movement details section with status as Complete" in {

        val userAnswers = emptyUserAnswers
          .set(DeclarationTypePage, DeclarationType.values.head)
          .toOption
          .value
          .set(ProcedureTypePage, ProcedureType.values.head)
          .toOption
          .value
          .set(ContainersUsedPage, true)
          .toOption
          .value
          .set(DeclarationPlacePage, "answers")
          .toOption
          .value
          .set(DeclarationForSomeoneElsePage, true)
          .toOption
          .value
          .set(RepresentativeNamePage, "name")
          .toOption
          .value
          .set(RepresentativeCapacityPage, RepresentativeCapacity.Direct)
          .toOption
          .value

        val url             = movementDetailsRoutes.MovementDetailsCheckYourAnswersController.onPageLoad(lrn).url
        val expectedSection = SectionDetails("declarationSummary.section.movementDetails", url, Completed)
        val expectedResult  = updateSectionsWithExpectedValue(expectedSection)

        val sectionsHelper = new SectionsHelper(userAnswers)
        val result         = sectionsHelper.getSections

        result mustBe expectedResult
      }
    }

    "Trader Details" - {
      "must return trader's details section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

        val url              = traderDetailsRoutes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.tradersDetails"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return trader's details section with status as In Progress" in {
        val userAnswers    = emptyUserAnswers.set(IsPrincipalEoriKnownPage, true).success.value
        val sectionsHelper = new SectionsHelper(userAnswers)

        val url              = traderDetailsRoutes.WhatIsPrincipalEoriController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.tradersDetails"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, InProgress))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return trader's details section with status as Completed" in {
        val userAnswers = emptyUserAnswers
          .set(IsPrincipalEoriKnownPage, true)
          .success
          .value
          .set(WhatIsPrincipalEoriPage, "GB123456")
          .success
          .value
          .set(AddConsignorPage, true)
          .success
          .value
          .set(IsConsignorEoriKnownPage, true)
          .success
          .value
          .set(ConsignorEoriPage, "GB123456")
          .success
          .value
          .set(AddConsigneePage, true)
          .success
          .value
          .set(IsConsigneeEoriKnownPage, true)
          .success
          .value
          .set(WhatIsConsigneeEoriPage, "GB123456")
          .success
          .value

        val sectionsHelper = new SectionsHelper(userAnswers)

        val url              = traderDetailsRoutes.TraderDetailsCheckYourAnswersController.onPageLoad(lrn).url
        val sectionName      = "declarationSummary.section.tradersDetails"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, Completed))

        val result: Seq[SectionDetails] = sectionsHelper.getSections

        result mustBe expectedSections
      }
    }

    "Transport Details" - {
      "must return transport details section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

        val url              = transportDetailsRoutes.InlandModeController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.transport"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result: Seq[SectionDetails] = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return transport details section with status as In Progress" in {

        val transportMode: TransportMode = TransportMode("1", "Sea transport")
        val userAnswers: UserAnswers     = emptyUserAnswers.set(InlandModePage, transportMode.code).success.value
        val sectionsHelper               = new SectionsHelper(userAnswers)
        val url                          = transportDetailsRoutes.AddIdAtDepartureController.onPageLoad(lrn, NormalMode).url
        val sectionName                  = "declarationSummary.section.transport"

        val result: Seq[SectionDetails] = sectionsHelper.getSections
        result must contain(SectionDetails(sectionName, url, InProgress))
      }
      "must return transport details section with status as Completed" in {

        val country = Country(CountryCode("GB"), "United Kingdom")
        val mode    = TransportMode("1", "Sea transport")
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(InlandModePage, mode.code)
          .success
          .value
          .set(AddIdAtDeparturePage, true)
          .success
          .value
          .set(IdAtDeparturePage, "Kev")
          .success
          .value
          .set(ChangeAtBorderPage, true)
          .success
          .value
          .set(NationalityAtDeparturePage, country.code)
          .success
          .value
          .set(ModeAtBorderPage, mode.code)
          .success
          .value
          .set(IdCrossingBorderPage, "1")
          .success
          .value
          .set(ModeCrossingBorderPage, mode.code)
          .success
          .value
          .set(NationalityCrossingBorderPage, country.code)
          .success
          .value

        val sectionsHelper = new SectionsHelper(userAnswers)

        val url         = transportDetailsRoutes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url
        val sectionName = "declarationSummary.section.transport"

        val result = sectionsHelper.getSections

        result must contain(SectionDetails(sectionName, url, Completed))
      }

    }

    "Routes Details" - {
      "must return routes section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

        val url              = routeDetailsRoutes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.routes"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return routes section with status as InProgress" in {
        val userAnswers    = emptyUserAnswers.set(CountryOfDispatchPage, CountryCode("GB")).toOption.value
        val sectionsHelper = new SectionsHelper(userAnswers)

        val url              = routeDetailsRoutes.OfficeOfDepartureController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.routes"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, InProgress))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return routes section with status as Completed" in {
        val userAnswers = emptyUserAnswers
          .set(CountryOfDispatchPage, CountryCode("GB"))
          .toOption
          .value
          .set(OfficeOfDeparturePage, "GB00010")
          .toOption
          .value
          .set(DestinationCountryPage, CountryCode("GB"))
          .toOption
          .value
          .set(DestinationOfficePage, "GB00010")
          .toOption
          .value
          .set(AddAnotherTransitOfficePage(index), "1")
          .toOption
          .value
        val sectionsHelper = new SectionsHelper(userAnswers)

        val url              = routeDetailsRoutes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url
        val sectionName      = "declarationSummary.section.routes"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, Completed))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }
    }

    "Goods Summary" - {
      "must Goods Summary section with status as NotStarted" in {
        val sectionsHelper = new SectionsHelper(emptyUserAnswers)

        val url              = goodsSummaryRoutes.DeclarePackagesController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "goodsSummary.section.routes"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }
      "must return goodssummary section with status as In Progress" in {
        val userAnswers    = emptyUserAnswers.set(DeclarePackagesPage, true).success.value
        val sectionsHelper = new SectionsHelper(userAnswers)

        val url              = goodsSummaryRoutes.TotalPackagesController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.goodsSummary"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, InProgress))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must goods summary section with status as Completed on a Normal Journey" in {
        val userAnswers = emptyUserAnswers
          .set(DeclarePackagesPage, true)
          .toOption
          .value
          .set(TotalPackagesPage, 100)
          .success
          .value
          .set(TotalGrossMassPage, "100.123")
          .success
          .value
          .set(ProcedureTypePage, Normal)
          .toOption
          .value
          .set(AddCustomsApprovedLocationPage, true)
          .success
          .value
          .set(CustomsApprovedLocationPage, "testlocation")
          .success
          .value
          .set(AddSealsPage, true)
          .toOption
          .value
          .set(SealIdDetailsPage(Index(0)), sealDomain)
          .success
          .value
          .set(SealsInformationPage, false)
          .toOption
          .value
        val sectionsHelper = new SectionsHelper(userAnswers)

        val url              = goodsSummaryRoutes.GoodsSummaryCheckYourAnswersController.onPageLoad(lrn).url
        val sectionName      = "declarationSummary.section.goodsSummary"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, Completed))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must goods summary section with status as Completed on a Simplified Journey" in {
        val date = LocalDate.now

        val userAnswers = emptyUserAnswers
          .set(DeclarePackagesPage, true)
          .toOption
          .value
          .set(TotalPackagesPage, 100)
          .success
          .value
          .set(TotalGrossMassPage, "100.123")
          .success
          .value
          .set(ProcedureTypePage, Simplified)
          .toOption
          .value
          .set(AuthorisedLocationCodePage, "testcode")
          .success
          .value
          .set(ControlResultDateLimitPage, date)
          .success
          .value
          .set(AddSealsPage, true)
          .toOption
          .value
          .set(SealIdDetailsPage(sealIndex), sealDomain)
          .success
          .value
          .set(SealsInformationPage, false)
          .toOption
          .value
        val sectionsHelper = new SectionsHelper(userAnswers)

        val url              = goodsSummaryRoutes.GoodsSummaryCheckYourAnswersController.onPageLoad(lrn).url
        val sectionName      = "declarationSummary.section.goodsSummary"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, Completed))

        val result = sectionsHelper.getSections

        result mustBe expectedSections
      }

    }

    "Guarantee Details" - {
      "must return guarantee details section with status as NotStarted" in {
        val sectionsHelper   = new SectionsHelper(emptyUserAnswers)
        val url              = guaranteeDetailsRoutes.GuaranteeTypeController.onPageLoad(lrn, NormalMode).url
        val sectionName      = "declarationSummary.section.guarantee"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, NotStarted))
        val result           = sectionsHelper.getSections

        result mustBe expectedSections
      }

      "must return guarantee details section with status as InProgress" in {

        val userAnswers = emptyUserAnswers
          .set(GuaranteeTypePage, GuaranteeWaiver)
          .success
          .value

        val sectionsHelper   = new SectionsHelper(userAnswers)
        val url              = guaranteeDetailsRoutes.GuaranteeReferenceController.onPageLoad(lrn, NormalMode).url
        val result           = sectionsHelper.getSections
        val sectionName      = "declarationSummary.section.guarantee"
        val expectedSections = updateSectionsWithExpectedValue(SectionDetails(sectionName, url, InProgress))

        result mustBe expectedSections
      }

      "must return guarantee details section with status as Complete when Guarantee Reference path completed" in {

        val userAnswers = emptyUserAnswers
          .set(GuaranteeTypePage, GuaranteeWaiver)
          .toOption
          .value
          .set(GuaranteeReferencePage, "12345")
          .toOption
          .value
          .set(LiabilityAmountPage, "100.00")
          .toOption
          .value
          .set(AccessCodePage, "1234")
          .toOption
          .value

        val url             = guaranteeDetailsRoutes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn).url
        val expectedSection = SectionDetails("declarationSummary.section.guarantee", url, Completed)
        val expectedResult  = updateSectionsWithExpectedValue(expectedSection)

        val sectionsHelper = new SectionsHelper(userAnswers)
        val result         = sectionsHelper.getSections

        result mustBe expectedResult
      }

      "must return guarantee details section with status as Complete when Other Reference path completed" in {

        val userAnswers = emptyUserAnswers
          .set(GuaranteeTypePage, CashDepositGuarantee)
          .success
          .value
          .set(OtherReferencePage, "54321")
          .toOption
          .value
          .set(OtherReferenceLiabilityAmountPage, "10.50")
          .toOption
          .value

        val url             = guaranteeDetailsRoutes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn).url
        val expectedSection = SectionDetails("declarationSummary.section.guarantee", url, Completed)
        val expectedResult  = updateSectionsWithExpectedValue(expectedSection)

        val sectionsHelper = new SectionsHelper(userAnswers)
        val result         = sectionsHelper.getSections

        result mustBe expectedResult
      }
    }
  }

  private def updateSectionsWithExpectedValue(sectionDtls: SectionDetails): Seq[SectionDetails] = {
    val sections: Seq[SectionDetails] = Seq(
      SectionDetails("declarationSummary.section.movementDetails", movementDetailsRoutes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url, NotStarted),
      SectionDetails("declarationSummary.section.routes", routeDetailsRoutes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url, NotStarted),
      SectionDetails("declarationSummary.section.transport", transportDetailsRoutes.InlandModeController.onPageLoad(lrn, NormalMode).url, NotStarted),
      SectionDetails("declarationSummary.section.tradersDetails",
                     traderDetailsRoutes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url,
                     NotStarted),
      SectionDetails("declarationSummary.section.goodsSummary", goodsSummaryRoutes.DeclarePackagesController.onPageLoad(lrn, NormalMode).url, NotStarted),
      SectionDetails("declarationSummary.section.guarantee", guaranteeDetailsRoutes.GuaranteeTypeController.onPageLoad(lrn, NormalMode).url, NotStarted)
    )
    sections.map {
      section =>
        if (section.name == sectionDtls.name) sectionDtls else section
    }
  }
}
