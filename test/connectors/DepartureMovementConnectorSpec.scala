package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helper.WireMockServerHandler
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class DepartureMovementConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  val stubUrl = "/transits-movements-trader-at-departure/movements/departures"

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.departures.port" -> server.port()
    )
    .build()

  private val connector                     = app.injector.instanceOf[DepartureMovementConnector]
  private val errorResponsesCodes: Gen[Int] = Gen.chooseNum(400, 599)

  "DepartureMovementConnector" - {
    "submitDepartureMovement" - {
      "must return status as OK for submission of valid arrival movement" in {

        stubResponse(ACCEPTED)

        forAll(arbitrary[String]) {
          departureMovementRequest =>
            val result: Future[HttpResponse] = connector.submitDepartureMovement(departureMovementRequest)
            result.futureValue.status mustBe ACCEPTED
        }
      }

      "must return an error status when an error response is returned from submitArrivalMovement" in {
        forAll(arbitrary[String], errorResponsesCodes) {
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
