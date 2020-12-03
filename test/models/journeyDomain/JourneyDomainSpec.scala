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

package models.journeyDomain

import java.time.LocalDateTime

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import generators.JourneyModelGenerators
import models.DeclarationType.{Option1, Option4}
import models.GuaranteeType.ComprehensiveGuarantee
import models.ProcedureType.{Normal, Simplified}
import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.GoodSummaryNormalDetails
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.journeyDomain.MovementDetails._
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TraderDetails.{PersonalInformation, RequiredDetails, TraderEori}
import models.journeyDomain.TransportDetails.{DetailsAtBorder, ModeCrossingBorder}
import models.journeyDomain.TransportDetails.DetailsAtBorder.NewDetailsAtBorder
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, Rail}
import models.journeyDomain._
import models.reference.{CountryCode, PackageType}
import models.{EoriNumber, GuaranteeType, LocalReferenceNumber, ProcedureType, RepresentativeCapacity, UserAnswers}

class JourneyDomainSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "JourneyDomain" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arb[JourneyDomain]) {
          case journeyDomain =>
            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(emptyUserAnswers)
            val result            = UserAnswersReader[JourneyDomain].run(updatedUserAnswer)

            result.value.preTaskList mustEqual journeyDomain.preTaskList
            result.value.movementDetails mustEqual journeyDomain.movementDetails
            result.value.routeDetails mustEqual journeyDomain.routeDetails
            result.value.transportDetails mustEqual journeyDomain.transportDetails
            result.value.traderDetails mustEqual journeyDomain.traderDetails
            result.value.itemDetails mustEqual journeyDomain.itemDetails
            result.value.goodsSummary mustEqual journeyDomain.goodsSummary
            result.value.guarantee mustEqual journeyDomain.guarantee
            result.value.safetyAndSecurity mustEqual journeyDomain.safetyAndSecurity
        }
      }
    }

    "cannot be parsed" - {
      "when some answers is missing" in {
        forAll(arb[ItemSection], arb[UserAnswers]) {
          case (itemSection, ua) =>
            val userAnswers                 = ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index)(ua)
            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(userAnswers)

            result mustBe None
        }
      }

      "when using this specific JourneyDomain" in {

        val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(woa)(emptyUserAnswers)
        val result            = UserAnswersReader[JourneyDomain].run(updatedUserAnswer)

        result.value.preTaskList mustEqual woa.preTaskList
        result.value.movementDetails mustEqual woa.movementDetails
        result.value.routeDetails mustEqual woa.routeDetails
        result.value.transportDetails mustEqual woa.transportDetails
        result.value.traderDetails mustEqual woa.traderDetails
        result.value.itemDetails mustEqual woa.itemDetails
        result.value.goodsSummary mustEqual woa.goodsSummary
        result.value.guarantee mustEqual woa.guarantee
        result.value.safetyAndSecurity mustEqual woa.safetyAndSecurity
      }
    }
  }

  val woa = JourneyDomain(
    PreTaskListDetails(LocalReferenceNumber("luMir").get, Normal, true),
    NormalMovementDetails(Option1, false, true, "asdf", DeclarationForSelf),
    RouteDetails(
      CountryCode("PT"),
      "asdf",
      CountryCode("EW"),
      "asdfas",
      NonEmptyList(TransitInformation("asdfa", Some(LocalDateTime.now())), List.empty)
    ),
    TransportDetails(
      Mode5or7(70, CountryCode("XS")),
      NewDetailsAtBorder("730934818", "asdfasd", ModeCrossingBorder.ModeExemptNationality)
    ),
    TraderDetails(TraderEori(EoriNumber("맖膄ύ얹ᵡ鱋颼")), Some(TraderEori(EoriNumber("㢻芔콾㾠禟盾늠"))), None),
    NonEmptyList(
      ItemSection(
        ItemDetails("asdf", "-2147483648", Some("2147483647"), Some("㢨諹")),
        Some(ItemTraderDetails.RequiredDetails(EoriNumber("asdfa"))),
        Some(ItemTraderDetails.RequiredDetails(EoriNumber("asefas"))),
        NonEmptyList(UnpackedPackages(PackageType("asdf", "aasd"), None, 5, Some("")),
                     List(UnpackedPackages(PackageType("asdf", "aasd"), Some(3), 2, Some("إ쫯ㄯ栢린놱೗꓎")))),
        Some(NonEmptyList(Container("⚉獍욀妙伥㷀墲捺純ꢖ퀢껸玄楧簲삮鄯ᛰㇽฝ፤"), List.empty)),
        Some(NonEmptyList(SpecialMention("asdf", "asdf"), List(SpecialMention("asdf", "asdfasd")))),
        None
      ),
      List(
        ItemSection(
          ItemDetails("asd", "-528298836", Some("1"), Some("ῇ袋⚿彖Ұ圝")),
          Some(ItemTraderDetails.RequiredDetails(EoriNumber("ⰹ࿎"))),
          Some(ItemTraderDetails.RequiredDetails(EoriNumber("쓷쯖↬꘼㒛"))),
          NonEmptyList(BulkPackages(PackageType("asdf", "aasd"), None, Some("넟⍆Ṝ")), List(OtherPackages(PackageType("asdf", "asdf"), 4, "asdf"))),
          Some(NonEmptyList(Container("asdf"), List(Container("asdfas")))),
          Some(NonEmptyList(SpecialMention("asdfg", "asrdf"), List.empty)),
          None
        )
      )
    ),
    GoodsSummary(Some(53),
                 "59",
                 None,
                 GoodSummaryNormalDetails(Some("Asdfa")),
                 List(SealDomain("asdf"), SealDomain("asdfa"), SealDomain("asdfasd"), SealDomain("asdfasd"))),
    GuaranteeReference(ComprehensiveGuarantee, "asdf", "asdf", "asdf"),
    Some(
      SafetyAndSecurity(
        None,
        Some("asdf"),
        Some("asdf"),
        None,
        Some("assdf"),
        Some(SafetyAndSecurity.SecurityTraderDetails(EoriNumber("asdfasd"))),
        None,
        Some(SafetyAndSecurity.SecurityTraderDetails(EoriNumber("asdfasdf")))
      )
    )
  )

}

object JourneyDomainSpec {

  def setJourneyDomain(journeyDomain: JourneyDomain)(startUserAnswers: UserAnswers): UserAnswers =
    (
      PreTaskListDetailsSpec.setPreTaskListDetails(journeyDomain.preTaskList) _ andThen
        RouteDetailsSpec.setRouteDetails(journeyDomain.routeDetails, Some(journeyDomain.preTaskList.addSecurityDetails)) andThen
        TransportDetailsSpec.setTransportDetail(journeyDomain.transportDetails) andThen
        ItemSectionSpec.setItemSections(journeyDomain.itemDetails.toList) andThen
        GoodsSummarySpec.setGoodsSummary(journeyDomain.goodsSummary, Some(journeyDomain.preTaskList.addSecurityDetails)) andThen
        GuaranteeDetailsSpec.setGuaranteeDetails(journeyDomain.guarantee) andThen
        TraderDetailsSpec.setTraderDetails(journeyDomain.traderDetails) andThen
        MovementDetailsSpec.setMovementDetails(journeyDomain.movementDetails) andThen
        safetyAndSecurity(journeyDomain.safetyAndSecurity)
    )(startUserAnswers)

  def safetyAndSecurity(safetyAndSecurity: Option[SafetyAndSecurity])(startUserAnswers: UserAnswers): UserAnswers =
    safetyAndSecurity match {
      case Some(value) => SafetyAndSecuritySpec.setSafetyAndSecurity(value)(startUserAnswers)
      case None        => startUserAnswers
    }

}
