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

package controllers.addItems.traderDetails

import base.{MockNunjucksRendererApp, SpecBase}
import forms.addItems.traderDetails.TraderDetailsConsigneeAddressFormProvider
import generators.Generators
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{ConsigneeAddress, CountryList, NormalMode}
import navigation.annotations.AddItems
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.traderDetails.{TraderDetailsConsigneeAddressPage, TraderDetailsConsigneeNamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class TraderDetailsConsigneeAddressControllerSpec
    extends SpecBase
    with MockNunjucksRendererApp
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with Generators {

  def onwardRoute = Call("GET", "/foo")

  val consigneeName = "Test consignee"

  private val formProvider = new TraderDetailsConsigneeAddressFormProvider()
  private val country      = Country(CountryCode("GB"), "United Kingdom")
  private val countries    = CountryList(Seq(country))
  private val form         = formProvider(countries)
  private val template     = "addItems/traderDetails/traderDetailsConsigneeAddress.njk"

  lazy val traderDetailsConsigneeAddressRoute = routes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, index, NormalMode).url
  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItems]).toInstance(new FakeNavigator(onwardRoute)))

  "TraderDetailsConsigneeAddress Controller" - {

    "must return OK and the correct view for a GET" in {
      val answers = emptyUserAnswers
        .set(TraderDetailsConsigneeNamePage(index), consigneeName)
        .success
        .value
      dataRetrievalWithData(answers)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(GET, traderDetailsConsigneeAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "lrn"  -> lrn
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val address = arbitrary[ConsigneeAddress].sample.value
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers
        .set(TraderDetailsConsigneeNamePage(index), consigneeName)
        .success
        .value
        .set(TraderDetailsConsigneeAddressPage(index), address)
        .success
        .value

      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, traderDetailsConsigneeAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm =
        form.bind(
          Map(
            "AddressLine1" -> address.AddressLine1,
            "AddressLine2" -> address.AddressLine2,
            "AddressLine3" -> address.AddressLine3,
            "Country"      -> address.country.code.code,
          )
        )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      val address = arbitrary[ConsigneeAddress].sample.value
      val userAnswers = emptyUserAnswers
        .set(TraderDetailsConsigneeNamePage(index), consigneeName)
        .success
        .value
        .set(TraderDetailsConsigneeAddressPage(index), address)
        .success
        .value

      dataRetrievalWithData(userAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, traderDetailsConsigneeAddressRoute)
          .withFormUrlEncodedBody(
            ("AddressLine1", address.AddressLine1),
            ("AddressLine2", address.AddressLine2),
            ("AddressLine3", address.AddressLine3),
            ("Country", address.country.code.code)
          )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(POST, traderDetailsConsigneeAddressRoute).withFormUrlEncodedBody(("value", ""))
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

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      dataRetrievalNoData()

      val request = FakeRequest(GET, traderDetailsConsigneeAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController
        .onPageLoad()
        .url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()
      val request =
        FakeRequest(POST, traderDetailsConsigneeAddressRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController
        .onPageLoad()
        .url
    }
  }
}
