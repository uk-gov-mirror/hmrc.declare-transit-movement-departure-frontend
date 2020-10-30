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
import controllers.{routes => mainRoute}
import forms.DestinationCountryFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{CountryList, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.DestinationCountryPage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, redirectLocation, route, status, _}
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport
import navigation.annotations.RouteDetails

import scala.concurrent.Future

class DestinationCountryControllerSpec
    extends SpecBase
    with MockNunjucksRendererApp
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with BeforeAndAfterEach {
  val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DestinationCountryFormProvider()
  val countries    = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  val form         = formProvider(countries)

  lazy val destinationCountryRoute = routes.DestinationCountryController.onPageLoad(lrn, NormalMode).url

  def jsonCountryList(preSelected: Boolean): Seq[JsObject] = Seq(
    Json.obj("text" -> "", "value"               -> ""),
    Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> preSelected)
  )

  override def beforeEach: Unit = {
    reset(mockReferenceDataConnector)
    super.beforeEach
  }

  "DestinationCountry Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()

      val request        = FakeRequest(GET, destinationCountryRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "lrn"         -> lrn,
        "mode"        -> NormalMode,
        "countries"   -> jsonCountryList(preSelected = false),
        "onSubmitUrl" -> routes.DestinationCountryController.onSubmit(lrn, NormalMode).url
      )

      templateCaptor.getValue mustEqual "destinationCountry.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val application = applicationBuilder(userAnswers = emptyUserAnswers.set(DestinationCountryPage, CountryCode("GB")).toOption)
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(GET, destinationCountryRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "value" -> "GB"
        )
      )

      val expectedJson = Json.obj(
        "form"        -> filledForm,
        "lrn"         -> lrn,
        "countries"   -> jsonCountryList(true),
        "mode"        -> NormalMode,
        "onSubmitUrl" -> routes.DestinationCountryController.onSubmit(lrn, NormalMode).url
      )

      templateCaptor.getValue mustEqual "destinationCountry.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector)
          )
          .build()

      val request =
        FakeRequest(POST, destinationCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(POST, destinationCountryRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "destinationCountry.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, destinationCountryRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, destinationCountryRoute)
          .withFormUrlEncodedBody(("value", "value 1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
