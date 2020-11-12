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

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import generators.MessagesModelGenerators
import helper.WireMockServerHandler
import models.messages.DeclarationRequest
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

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
  }

  private def stubResponse(expectedStatus: Int): StubMapping =
    server.stubFor(
      post(urlEqualTo(stubUrl))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
        )
    )
}
