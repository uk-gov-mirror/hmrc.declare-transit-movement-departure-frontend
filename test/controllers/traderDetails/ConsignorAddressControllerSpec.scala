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

package controllers.traderDetails

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.ConsignorAddressFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{ConsignorAddress, CountryList, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.{ConsignorAddressPage, ConsignorNamePage}
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport
import navigation.annotations.TraderDetails

import scala.concurrent.Future

class ConsignorAddressControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute                                        = Call("GET", "/foo")
  val country                                            = Country(CountryCode("GB"), "United Kingdom")
  val countries                                          = CountryList(Seq(country))
  val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  val formProvider = new ConsignorAddressFormProvider()
  val form         = formProvider(countries)

  lazy val consignorAddressRoute = routes.ConsignorAddressController.onPageLoad(lrn, NormalMode).url

  val userAnswers = UserAnswers(
    lrn,
    eoriNumber,
    Json.obj(
      ConsignorAddressPage.toString -> Json.obj(
        "AddressLine1" -> "value 1",
        "AddressLine2" -> "value 2",
        "AddressLine3" -> "value 3",
        "country"      -> country
      )
    )
  )

  def jsonCountryList(preSelected: Boolean): Seq[JsObject] = Seq(
    Json.obj("text" -> "", "value"               -> ""),
    Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> preSelected)
  )

  override def beforeEach: Unit = {
    reset(mockReferenceDataConnector)
    super.beforeEach
  }

  "ConsignorAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers
        .set(ConsignorNamePage, "foo")
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(GET, consignorAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "consignorAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val consignorAddress: ConsignorAddress = ConsignorAddress("Address line 1", "Address line 2", "Address line 3", country)

      val userAnswers = emptyUserAnswers
        .set(ConsignorNamePage, "consignorName")
        .success
        .value
        .set(ConsignorAddressPage, consignorAddress)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(GET, consignorAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "AddressLine1" -> "Address line 1",
          "AddressLine2" -> "Address line 2",
          "AddressLine3" -> "Address line 3",
          "country"      -> "GB"
        )
      )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "consignorAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers
        .set(ConsignorNamePage, "consignorName")
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind(classOf[Navigator]).qualifiedWith(classOf[TraderDetails]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
          .build()

      val request =
        FakeRequest(POST, consignorAddressRoute)
          .withFormUrlEncodedBody(("AddressLine1", "value 1"), ("AddressLine2", "value 2"), ("AddressLine3", "value 3"), ("country", "GB"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers
        .set(ConsignorNamePage, "consignorName")
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(POST, consignorAddressRoute).withFormUrlEncodedBody(("value", "invalid value"))
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

      templateCaptor.getValue mustEqual "consignorAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, consignorAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, consignorAddressRoute)
          .withFormUrlEncodedBody(("Address line 1", "value 1"), ("Address line 2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
