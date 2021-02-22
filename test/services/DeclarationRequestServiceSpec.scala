/*
 * Copyright 2021 HM Revenue & Customs
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

package services

import java.time.LocalDateTime
import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import generators.JourneyModelGenerators
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import models.journeyDomain.GoodsSummary.GoodSummarySimplifiedDetails
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.journeyDomain.TransportDetails.DetailsAtBorder.{NewDetailsAtBorder, SameDetailsAtBorder}
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, NonSpecialMode, Rail}
import models.journeyDomain.TransportDetails.ModeCrossingBorder.{ModeExemptNationality, ModeWithNationality}
import models.journeyDomain.{JourneyDomain, JourneyDomainSpec, TransportDetails}
import models.messages.goodsitem.SpecialMentionGuaranteeLiabilityAmount
import models.messages.{DeclarationRequest, InterchangeControlReference}
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import repositories.InterchangeControlReferenceIdRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeclarationRequestServiceSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with BeforeAndAfterEach {

  val mockIcrRepository   = mock[InterchangeControlReferenceIdRepository]
  val mockDateTimeService = mock[DateTimeService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIcrRepository)
    reset(mockDateTimeService)
  }

  "DomainModelToSubmissionModel" - {
    "must convert JourneyDomain model to DeclarationRequest model" in {

      forAll(arb[UserAnswers], arb[JourneyDomain]) {
        (userAnswers, journeyDomain) =>
          val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

          when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
          when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

          val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
          service.convert(updatedUserAnswer).futureValue must be(defined)
      }
    }

    "must None when InterchangeControlReferenceIdRepository fails" in {

      forAll(arb[UserAnswers], arb[JourneyDomain]) {
        (userAnswers, journeyDomain) =>
          val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

          when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.failed(new Exception))
          when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

          val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
          service.convert(updatedUserAnswer).failed.futureValue mustBe an[Exception]
      }
    }

    "must None when mandatory pages are missing" in {
      val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

      when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
      when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

      service.convert(emptyUserAnswers).futureValue mustBe None
    }

    "Liability amount must always only add to first Goods Item and other Goods Items should not contain it" in {

      forAll(arb[UserAnswers], arb[JourneyDomain], nonEmptyListOf[GuaranteeReference](3)) {
        (userAnswers, journeyDomain, guaranteeReferences) =>
          val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

          when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
          when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

          val updatedJourneyDomain: JourneyDomain = journeyDomain.copy(guarantee = guaranteeReferences)

          val updatedUserAnswer                  = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)
          val result: Option[DeclarationRequest] = service.convert(updatedUserAnswer).futureValue
          result must be(defined)

          val firstGoodsItemSpecialMentionLiabilityAmount = result.get.goodsItems.head.specialMention.collect {
            case specialMention: SpecialMentionGuaranteeLiabilityAmount => specialMention
          }

          val expectedSpecialMention: NonEmptyList[SpecialMentionGuaranteeLiabilityAmount] = guaranteeReferences.map {
            case GuaranteeReference(_, guaranteeReferenceNumber, GuaranteeReference.defaultLiability, _) =>
              val defaultLiabilityAmount = s"${GuaranteeReference.defaultLiability}EUR$guaranteeReferenceNumber"
              SpecialMentionGuaranteeLiabilityAmount("CAL", defaultLiabilityAmount)

            case GuaranteeReference(_, guaranteeReferenceNumber, otherAmount, _) =>
              val notDefaultAmount = s"${otherAmount}GBP$guaranteeReferenceNumber"
              SpecialMentionGuaranteeLiabilityAmount("CAL", notDefaultAmount)

          }
          firstGoodsItemSpecialMentionLiabilityAmount mustBe expectedSpecialMention.toList

          val otherGoodsItemsSpecialMentionLiabilityAmount = result.get.goodsItems.tail.flatMap(
            _.specialMention.collect {
              case specialMention: SpecialMentionGuaranteeLiabilityAmount => specialMention
            }
          )
          otherGoodsItemsSpecialMentionLiabilityAmount mustBe Seq()
      }
    }

    "identityOfTransportAtCrossing" - {

      val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

      "must return id of crossing when there are new details at border" in {

        forAll(arb[UserAnswers], arb[NewDetailsAtBorder], arb[JourneyDomain]) {
          (userAnswers, newDetailsAtBorder, journeyDomain) =>
            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = newDetailsAtBorder)
            val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

            val result = service.convert(updatedUserAnswer).futureValue

            result.value.header.transportDetails.ideOfMeaOfTraCroHEA85.value mustBe newDetailsAtBorder.idCrossing
        }
      }

      "must return id of departure when there are no new details at border and inlandMode is a nonSpecialMode" in {

        forAll(arb[JourneyDomain], arb[NonSpecialMode]) {
          (journeyDomain, nonSpecialMode) =>
            val userAnswers = UserAnswers(LocalReferenceNumber("lrn").value, EoriNumber("1"))
            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = nonSpecialMode)
            val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

            val result = service.convert(updatedUserAnswer).futureValue

            result.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe nonSpecialMode.departureId
        }
      }

      "must return none when there are no id at departure or crossing" in {

        forAll(arb[JourneyDomain], arb[Rail]) {
          (journeyDomain, rail) =>
            val userAnswers = UserAnswers(LocalReferenceNumber("lrn").value, EoriNumber("1"))
            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = rail)
            val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

            val result = service.convert(updatedUserAnswer).futureValue

            result.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe None
        }
      }
    }

    "identityOfTransportAtCrossing" - {

      val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

      "must return nationality of crossing when there are new details at border and the mode is a mode with nationality" in {

        forAll(arb[UserAnswers], arb[NewDetailsAtBorder], arb[JourneyDomain], arb[ModeWithNationality]) {
          (userAnswers, newDetailsAtBorder, journeyDomain, modeWithNationality) =>
            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedNewDetailsAtBorder = newDetailsAtBorder.copy(modeCrossingBorder          = modeWithNationality)
            val updatedTransportDetails   = journeyDomain.transportDetails.copy(detailsAtBorder = updatedNewDetailsAtBorder)
            val updatedJourneyDomain      = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

            val result = service.convert(updatedUserAnswer).futureValue

            result.value.header.transportDetails.natOfMeaOfTraCroHEA87.value mustBe modeWithNationality.nationalityCrossingBorder.code
        }
      }

      "must return None when there are new details at border and the mode is a mode that is exempt from nationality" in {

        forAll(arb[UserAnswers], arb[NewDetailsAtBorder], arb[JourneyDomain], arb[ModeExemptNationality]) {
          (userAnswers, newDetailsAtBorder, journeyDomain, modeExemptNationality) =>
            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedNewDetailsAtBorder = newDetailsAtBorder.copy(modeCrossingBorder          = modeExemptNationality)
            val updatedTransportDetails   = journeyDomain.transportDetails.copy(detailsAtBorder = updatedNewDetailsAtBorder)
            val updatedJourneyDomain      = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

            val result = service.convert(updatedUserAnswer).futureValue

            result.value.header.transportDetails.natOfMeaOfTraCroHEA87 mustBe None
        }
      }

      "must return nationality of departure when there are no new details at border and the mode is NonSpecialMode" in {

        forAll(arb[UserAnswers], arb[JourneyDomain], arb[NonSpecialMode]) {
          (userAnswers, journeyDomain, nonSpecialMode) =>
            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = nonSpecialMode)
            val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

            val result = service.convert(updatedUserAnswer).futureValue

            result.value.header.transportDetails.natOfMeaOfTraCroHEA87.get mustBe nonSpecialMode.nationalityAtDeparture.get.code
        }
      }

      "must return None when there are no new details at border and the mode is Rail" in {

        forAll(arb[UserAnswers], arb[JourneyDomain], arb[Rail]) {
          (userAnswers, journeyDomain, rail) =>
            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = rail)
            val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

            val result = service.convert(updatedUserAnswer).futureValue

            result.value.header.transportDetails.natOfMeaOfTraCroHEA87 mustBe None
        }
      }
    }
  }
}
