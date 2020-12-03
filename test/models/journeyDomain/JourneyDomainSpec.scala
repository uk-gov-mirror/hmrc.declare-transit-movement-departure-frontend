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
import cats.data.{NonEmptyList, ReaderT}
import generators.JourneyModelGenerators
import models.DeclarationType.Option1
import models.GuaranteeType.ComprehensiveGuarantee
import models.ProcedureType.Normal
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.GoodSummaryNormalDetails
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.journeyDomain.MovementDetails.{DeclarationForSelf, NormalMovementDetails}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TraderDetails.TraderEori
import models.journeyDomain.TransportDetails.DetailsAtBorder.NewDetailsAtBorder
import models.journeyDomain.TransportDetails.InlandMode.Mode5or7
import models.journeyDomain.TransportDetails.ModeCrossingBorder
import models.messages.guarantee.Guarantee
import models.reference.{CountryCode, PackageType}
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}

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

        implicit def fromUserAnswersParser[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersReader[A] =
          ReaderT[Option, UserAnswers, A](parser.run _)

        val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(woa)(emptyUserAnswers)

        val result1 = UserAnswersReader[PreTaskListDetails].run(updatedUserAnswer)
        val result2 = UserAnswersReader[MovementDetails].run(updatedUserAnswer)
        val result3 = UserAnswersReader[RouteDetails].run(updatedUserAnswer)
        val result4 = UserAnswersReader[TransportDetails].run(updatedUserAnswer)
        val result5 = UserAnswersReader[TraderDetails].run(updatedUserAnswer)
        val result6 = UserAnswersReader[NonEmptyList[ItemSection]].run(updatedUserAnswer)
        val result7 = UserAnswersReader[GoodsSummary].run(updatedUserAnswer)
        val result8 = UserAnswersReader[GuaranteeDetails].run(updatedUserAnswer)
        val result9 = UserAnswersReader[SafetyAndSecurity].run(updatedUserAnswer)

        result1.value mustEqual woa.preTaskList
        result2.value mustEqual woa.movementDetails
        result3.value mustEqual woa.routeDetails
        result4.value mustEqual woa.transportDetails
        result5.value mustEqual woa.traderDetails
        result6.value mustEqual woa.itemDetails
        result7.value mustEqual woa.goodsSummary
        result8.value mustEqual woa.guarantee
        result9 mustEqual woa.safetyAndSecurity
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
      List.empty
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
        MovementDetailsSpec.setMovementDetails(journeyDomain.movementDetails)
    )(startUserAnswers)

  def safetyAndSecurity(safetyAndSecurity: Option[SafetyAndSecurity])(startUserAnswers: UserAnswers): UserAnswers =
    safetyAndSecurity match {
      case Some(value) => SafetyAndSecuritySpec.setSafetyAndSecurity(value)(startUserAnswers)
      case None        => startUserAnswers
    }

}
