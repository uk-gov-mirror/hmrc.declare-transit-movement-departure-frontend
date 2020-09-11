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

import config.FrontendAppConfig
import javax.inject.Inject
import models.reference.{Country, CountryCode, CustomsOffice, TransportMode}
import models.{CountryList, CustomsOfficeList, TransportModeList}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject()(config: FrontendAppConfig, http: HttpClient) {

  def getCustomsOffices()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CustomsOfficeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices"
    http.GET[Seq[CustomsOffice]](serviceUrl).map(CustomsOfficeList(_))
  }

  def getCustomsOfficesOfTheCountry(countryCode: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CustomsOfficeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices/${countryCode.code}"
    http.GET[Seq[CustomsOffice]](serviceUrl).map(CustomsOfficeList(_))
  }

  def getCountryList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CountryList] = {
    val serviceUrl = s"${config.referenceDataUrl}/countries-full-list"
    http.GET[Seq[Country]](serviceUrl).map(CountryList(_))
  }

  def getTransitCountryList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CountryList] = {
    val serviceUrl = s"${config.referenceDataUrl}/transit-countries"
    http.GET[Seq[Country]](serviceUrl).map(CountryList(_))
  }

  def getTransportModes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[TransportModeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/transport-modes"
    http.GET[Seq[TransportMode]](serviceUrl).map(TransportModeList)
  }

}
