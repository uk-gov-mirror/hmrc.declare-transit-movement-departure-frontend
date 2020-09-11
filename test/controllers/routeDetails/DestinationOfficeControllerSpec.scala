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

import base.SpecBase
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.DestinationOfficeFormProvider
import generators.Generators
import matchers.JsonMatchers
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{CountryList, CustomsOfficeList, NormalMode}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.{DestinationCountryPage, DestinationOfficePage}
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class DestinationOfficeControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers with BeforeAndAfterEach with Generators {

  def onwardRoute = Call("GET", "/foo")

  private val countryCode = CountryCode("GB")
  private val countries = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val customsOffice1: CustomsOffice = CustomsOffice("officeId", "someName", Seq.empty, None)
  private val customsOffice2: CustomsOffice = CustomsOffice("id", "name", Seq.empty, None)
  private val customsOffices: CustomsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))
  private val form = new DestinationOfficeFormProvider()(customsOffices, "United Kingdom")
  private val mockReferenceDataConnector = mock[ReferenceDataConnector]
   lazy val destinationOfficeRoute = routes.DestinationOfficeController.onPageLoad(lrn, NormalMode).url

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockReferenceDataConnector)
  }

  "DestinationOffice Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any())(any(),any()))
        .thenReturn(Future.successful(customsOffices))
      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.set(DestinationCountryPage, countryCode).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request = FakeRequest(GET, destinationOfficeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedCustomsOfficeJson = Seq(
        Json.obj("value" -> "", "text"         -> ""),
        Json.obj("value" -> "officeId", "text" -> "someName (officeId)", "selected" -> false),
        Json.obj("value" -> "id", "text"       -> "name (id)", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "countryName" -> "United Kingdom",
        "customsOffices" -> expectedCustomsOfficeJson
      )

      templateCaptor.getValue mustEqual "destinationOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to session expired when destination country value is 'None'" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any())(any(),any()))
        .thenReturn(Future.successful(customsOffices))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request = FakeRequest(GET, destinationOfficeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any())(any(),any()))
        .thenReturn(Future.successful(customsOffices))

      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.set(DestinationOfficePage, "officeId").success.value
        .set(DestinationCountryPage, countryCode).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request = FakeRequest(GET, destinationOfficeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "officeId"))

      val expectedCustomsOfficeJson = Seq(
        Json.obj("value" -> "", "text"         -> ""),
        Json.obj("value" -> "officeId", "text" -> "someName (officeId)", "selected" -> true),
        Json.obj("value" -> "id", "text"       -> "name (id)", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode,
        "countryName" -> "United Kingdom",
        "customsOffices" -> expectedCustomsOfficeJson
      )

      templateCaptor.getValue mustEqual "destinationOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any())(any(),any()))
        .thenReturn(Future.successful(customsOffices))

      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      val userAnswers = emptyUserAnswers.set(DestinationCountryPage, countryCode).success.value
      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
           bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector),
            bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, destinationOfficeRoute)
          .withFormUrlEncodedBody(("value", "id"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers.set(DestinationCountryPage, countryCode).success.value
      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any())(any(),any()))
        .thenReturn(Future.successful(customsOffices))
      when(mockReferenceDataConnector.getTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request = FakeRequest(POST, destinationOfficeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "destinationOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, destinationOfficeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, destinationOfficeRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

}
