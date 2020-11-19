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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import generators.MessagesModelGenerators
import helper.WireMockServerHandler
import models.messages.DeclarationRequest
import models.{
  DeclarationRejectionMessage,
  DepartureId,
  GuaranteeNotValidMessage,
  InvalidGuaranteeCode,
  InvalidGuaranteeReasonCode,
  MessagesLocation,
  MessagesSummary
}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future
import scala.xml.NodeSeq

class DepartureMovementConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with MessagesModelGenerators {

  val stubUrl = "/transits-movements-trader-at-departure/movements/departures/"

  lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.departures.port" -> server.port()
    )
    .build()

  private lazy val connector                = app.injector.instanceOf[DepartureMovementConnector]
  private val errorResponsesCodes: Gen[Int] = Gen.chooseNum(400, 599)

  "DepartureMovementConnector" - {

    "submitDepartureMovement" - {
      "must return status as OK for submission of valid arrival movement" in {

        stubResponse(ACCEPTED)

        forAll(arbitrary[DeclarationRequest]) {
          departureMovementRequest =>
            val result: Future[HttpResponse] = connector.submitDepartureMovement(departureMovementRequest)
            result.futureValue.status mustBe ACCEPTED
        }
      }

      "must return an error status when an error response is returned from submitArrivalMovement" in {
        forAll(arbitrary[DeclarationRequest], errorResponsesCodes) {
          (departureMovementRequest, errorResponseCode) =>
            stubResponse(errorResponseCode)

            val result = connector.submitDepartureMovement(departureMovementRequest)
            result.futureValue.status mustBe errorResponseCode
        }
      }
    }

    "getSummary" - {

      "must be return summary of messages" in {
        val json = Json.obj(
          "departureId" -> departureId.value,
          "messages" -> Json.obj(
            "IE015" -> s"/movements/departures/${departureId.value}/messages/3",
            "IE055" -> s"/movements/departures/${departureId.value}/messages/5",
            "IE016" -> s"/movements/departures/${departureId.value}/messages/7"
          )
        )

        val messageAction =
          MessagesSummary(
            departureId,
            MessagesLocation(
              s"/movements/departures/${departureId.value}/messages/3",
              Some(s"/movements/departures/${departureId.value}/messages/5"),
              Some(s"/movements/departures/${departureId.value}/messages/7")
            )
          )

        server.stubFor(
          get(urlEqualTo(s"/transits-movements-trader-at-departure/movements/departures/${departureId.value}/messages/summary"))
            .willReturn(
              okJson(json.toString)
            )
        )
        connector.getSummary(departureId).futureValue mustBe Some(messageAction)
      }

      "must return 'None' when an error response is returned from getSummary" in {
        forAll(errorResponsesCodes) {
          errorResponseCode: Int =>
            stubGetResponse(errorResponseCode, "/transits-movements-trader-at-departure/movements/departures/1/messages/summary")

            connector.getSummary(departureId).futureValue mustBe None
        }
      }
    }

    "getGuaranteeNotValidMessage" - {
      "must return valid 'guarantee not valid message'" in {
        val location = s"/transits-movements-trader-at-departure-stub/movements/departures/${departureId.value}/messages/1"

        forAll(Gen.oneOf(InvalidGuaranteeCode.values)) {
          invalidCode =>
            val xml: NodeSeq = <CC055A>
              <HEAHEA>
                <DocNumHEA5>{lrn.toString}</DocNumHEA5>
              </HEAHEA>
            <GUAREF2>
              <GuaRefNumGRNREF21>GuaRefNumber1</GuaRefNumGRNREF21>
              <INVGUARNS>
                <InvGuaReaCodRNS11>{invalidCode.value}</InvGuaReaCodRNS11>
              </INVGUARNS>
            </GUAREF2>
          </CC055A>

            val json = Json.obj("message" -> xml.toString())

            server.stubFor(
              get(urlEqualTo(location))
                .willReturn(
                  okJson(json.toString)
                )
            )
            val expectedResult = Some(GuaranteeNotValidMessage(lrn.toString, Seq(InvalidGuaranteeReasonCode("GuaRefNumber1", invalidCode, None))))

            connector.getGuaranteeNotValidMessage(location).futureValue mustBe expectedResult
        }
      }

      "must return None for malformed input'" in {
        val location              = s"/transits-movements-trader-at-departure/movements/departures/${departureId.value}/messages/1"
        val rejectionXml: NodeSeq = <CC055A>
          <GUAREF2>
            <GuaRefNumGRNREF21>GuaRefNumber1</GuaRefNumGRNREF21>
            <INVGUARNS>
              <InvGuaReaCodRNS11>notvalid</InvGuaReaCodRNS11>
            </INVGUARNS>
          </GUAREF2>
        </CC055A>

        val json = Json.obj("message" -> rejectionXml.toString())

        server.stubFor(
          get(urlEqualTo(location))
            .willReturn(
              okJson(json.toString)
            )
        )

        connector.getGuaranteeNotValidMessage(location).futureValue mustBe None
      }

      "must return None when an error response is returned from getGuaranteeNotValidMessage" in {
        val location: String = "/transits-movements-trader-at-departure/movements/departures/1/messages/1"
        forAll(errorResponsesCodes) {
          errorResponseCode =>
            stubGetResponse(errorResponseCode, location)

            connector.getGuaranteeNotValidMessage(location).futureValue mustBe None
        }
      }
    }

    "getDeclarationRejectionMessage" - {
      "must return valid 'declaration reject message'" in {
        val location = s"/transits-movements-trader-at-departure-stub/movements/departures/${departureId.value}/messages/2"

        val xml: NodeSeq = <CC016A>
              <HEAHEA>
                <RefNumHEA4>05CTC20190913113500</RefNumHEA4>
                <DecRejDatHEA159>20190913</DecRejDatHEA159>
                <DecRejReaHEA252>The IE015 message received was invalid</DecRejReaHEA252>
              </HEAHEA>
              <FUNERRER1>
                <ErrTypER11>15</ErrTypER11>
                <ErrPoiER12>GUA(2).REF(1).Other guarantee reference</ErrPoiER12>
                <ErrReaER13>C130</ErrReaER13>
              </FUNERRER1>
            </CC016A>

        val json = Json.obj("message" -> xml.toString())

        server.stubFor(
          get(urlEqualTo(location))
            .willReturn(
              okJson(json.toString)
            )
        )
        val expectedResult =
          Some(DeclarationRejectionMessage("05CTC20190913113500", LocalDate.parse("2019-09-13"), "The IE015 message received was invalid", Seq.empty))

        connector.getDeclarationRejectionMessage(location).futureValue mustBe expectedResult

      }

      "must return None for malformed input'" in {
        val location              = s"/transits-movements-trader-at-departure/movements/departures/${departureId.value}/messages/2"
        val rejectionXml: NodeSeq = <CC016A>
          <FUNERRER1>
            <ErrTypER11>15</ErrTypER11>
            <ErrPoiER12>not valid</ErrPoiER12>
            <ErrReaER13>malformed</ErrReaER13>
          </FUNERRER1>
        </CC016A>

        val json = Json.obj("message" -> rejectionXml.toString())

        server.stubFor(
          get(urlEqualTo(location))
            .willReturn(
              okJson(json.toString)
            )
        )
        connector.getDeclarationRejectionMessage(location).futureValue mustBe None
      }

      "must return None when an error response is returned from getGuaranteeNotValidMessage" in {
        val location: String = "/transits-movements-trader-at-departure/movements/departures/1/messages/2"
        forAll(errorResponsesCodes) {
          errorResponseCode =>
            stubGetResponse(errorResponseCode, location)
            connector.getDeclarationRejectionMessage(location).futureValue mustBe None
        }
      }
    }

  }

  private def stubGetResponse(errorResponseCode: Int, serviceUrl: String) =
    server.stubFor(
      get(urlEqualTo(serviceUrl))
        .willReturn(
          aResponse()
            .withStatus(errorResponseCode)
        ))

  private def stubResponse(expectedStatus: Int): StubMapping =
    server.stubFor(
      post(urlEqualTo(stubUrl))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
        )
    )
}
