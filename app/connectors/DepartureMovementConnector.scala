package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class DepartureMovementConnector @Inject()(val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) {

  def submitDepartureMovement(departureMovement: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = { //TODO: make a movement when ready
    val serviceUrl = s"${appConfig.departureHost}/movements/departures/"
    val headers = Seq(("Content-Type", "application/xml"))

    http.POSTString[HttpResponse](serviceUrl, departureMovement, headers)
  }
}
