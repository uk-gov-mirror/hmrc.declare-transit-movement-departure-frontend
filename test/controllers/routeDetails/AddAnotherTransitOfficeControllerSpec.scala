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

package controllers.routeDetails

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoute}
import forms.AddAnotherTransitOfficeFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode, CustomsOffice, OfficeOfTransit}
import models.{CountryList, CustomsOfficeList, NormalMode, OfficeOfTransitList}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.{AddAnotherTransitOfficePage, DestinationCountryPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class AddAnotherTransitOfficeControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val countryCode                       = CountryCode("GB")
  private val countries                         = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val customsOffice1: CustomsOffice     = CustomsOffice("officeId", "someName", CountryCode("GB"), Seq.empty, None)
  private val customsOffice2: CustomsOffice     = CustomsOffice("id", "name", CountryCode("GB"), Seq.empty, None)
  private val customsOffices: CustomsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))
  val form                                      = new AddAnotherTransitOfficeFormProvider()(customsOffices, "United Kingdom")

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  lazy val addAnotherTransitOfficeRoute: String = routes.AddAnotherTransitOfficeController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockRefDataConnector))

  "AddAnotherTransitOffice Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(DestinationCountryPage, countryCode).success.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(any())(any(), any())).thenReturn(Future.successful(customsOffices))

      val request        = FakeRequest(GET, addAnotherTransitOfficeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedCustomsOfficeJson = Seq(
        Json.obj("value" -> "", "text"  -> ""),
        Json.obj("value" -> "1", "text" -> "Transit1 (1)", "selected" -> false),
        Json.obj("value" -> "2", "text" -> "Transit2 (2)", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"                -> form,
        "mode"                -> NormalMode,
        "lrn"                 -> lrn,
        "officeOfTransitList" -> expectedCustomsOfficeJson
      )

      templateCaptor.getValue mustEqual "addAnotherTransitOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(DestinationCountryPage, countryCode).success.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(any())(any(), any()))
        .thenReturn(Future.successful(customsOffices))
      when(mockRefDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val request        = FakeRequest(GET, addAnotherTransitOfficeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedCustomsOfficeJson = Seq(
        Json.obj("value" -> "", "text"         -> ""),
        Json.obj("value" -> "officeId", "text" -> "someName (officeId)", "selected" -> false),
        Json.obj("value" -> "id", "text"       -> "name (id)", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"           -> form,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "countryName"    -> "United Kingdom",
        "customsOffices" -> expectedCustomsOfficeJson
      )

      templateCaptor.getValue mustEqual "destinationOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), "1").success.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(any())(any(), any())).thenReturn(Future.successful(customsOffices))

      val request        = FakeRequest(GET, addAnotherTransitOfficeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "1"))

      val expectedCustomsOfficeJson = Seq(
        Json.obj("value" -> "", "text"  -> ""),
        Json.obj("value" -> "1", "text" -> "Transit1 (1)", "selected" -> true),
        Json.obj("value" -> "2", "text" -> "Transit2 (2)", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"                -> filledForm,
        "lrn"                 -> lrn,
        "mode"                -> NormalMode,
        "officeOfTransitList" -> expectedCustomsOfficeJson
      )

      templateCaptor.getValue mustEqual "addAnotherTransitOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(DestinationCountryPage, countryCode).success.value
      dataRetrievalWithData(userAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(any())(any(), any())).thenReturn(Future.successful(customsOffices))

      val request =
        FakeRequest(POST, addAnotherTransitOfficeRoute)
          .withFormUrlEncodedBody(("value", "1"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(any())(any(), any())).thenReturn(Future.successful(customsOffices))

      val request        = FakeRequest(POST, addAnotherTransitOfficeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "addAnotherTransitOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      dataRetrievalNoData()

      val request = FakeRequest(GET, addAnotherTransitOfficeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      dataRetrievalNoData()

      val request =
        FakeRequest(POST, addAnotherTransitOfficeRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url
    }
  }
}
