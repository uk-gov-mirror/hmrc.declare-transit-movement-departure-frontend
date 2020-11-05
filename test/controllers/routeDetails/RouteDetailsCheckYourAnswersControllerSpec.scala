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

package controllers.routeDetails

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import matchers.JsonMatchers
import models.reference.{Country, CountryCode, CustomsOffice, OfficeOfTransit}
import models.{CountryList, CustomsOfficeList, NormalMode, OfficeOfTransitList}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.DestinationCountryPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class RouteDetailsCheckYourAnswersControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with JsonMatchers {

  private val countries                                = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val customsOffice: CustomsOffice             = CustomsOffice("id", "name", Seq.empty, None)
  private val customsOffices: CustomsOfficeList        = CustomsOfficeList(Seq(customsOffice))
  private val officeOfTransit                          = OfficeOfTransit("1", "name")
  private val officeOfTransitList: OfficeOfTransitList = OfficeOfTransitList(Seq(officeOfTransit))
  lazy val routeDetailsCheckYourAnswersRoute           = mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
  val mockReferenceDataConnector                       = mock[ReferenceDataConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockReferenceDataConnector))

  "RouteDetailsCheckYourAnswers Controller" - {

    "return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(DestinationCountryPage, CountryCode("GB")).toOption.value
      dataRetrievalWithData(userAnswers)
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any())(any(), any())).thenReturn(Future.successful(customsOffices))
      when(mockReferenceDataConnector.getCustomsOffices()(any(), any())).thenReturn(Future.successful(customsOffices))
      when(mockReferenceDataConnector.getOfficeOfTransitList()(any(), any())).thenReturn(Future.successful(officeOfTransitList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(GET, routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"                    -> lrn,
        "nextPageUrl"            -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url,
        "addOfficesOfTransitUrl" -> routes.AddTransitOfficeController.onPageLoad(lrn, NormalMode).url
      )

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "routeDetailsCheckYourAnswers.njk"
      jsonCaptorWithoutConfig mustBe expectedJson
    }

    "must redirect to session reset page if DestinationCountry data is empty" in {
      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
