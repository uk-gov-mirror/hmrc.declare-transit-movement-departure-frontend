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

import java.time.{LocalDateTime, ZoneOffset}

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.ArrivalTimesAtOfficeFormProvider
import matchers.JsonMatchers
import models.reference.OfficeOfTransit
import models.{LocalDateTimeWithAMPM, NormalMode}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.{AddAnotherTransitOfficePage, ArrivalTimesAtOfficePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import viewModels.DateTimeInput

import scala.concurrent.Future

class ArrivalTimesAtOfficeControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  val formProvider                 = new ArrivalTimesAtOfficeFormProvider()
  private val officeOfTransit      = OfficeOfTransit("1", "name")
  private def form                 = formProvider(officeOfTransit.name)
  private val mockRefDataConnector = mock[ReferenceDataConnector]

  def onwardRoute = Call("GET", "/foo")

  val validAnswer: LocalDateTimeWithAMPM = LocalDateTimeWithAMPM(LocalDateTime.now(ZoneOffset.UTC).withHour(10), "am")

  lazy val arrivalTimesAtOfficeRoute = routes.ArrivalTimesAtOfficeController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockRefDataConnector))

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, arrivalTimesAtOfficeRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, arrivalTimesAtOfficeRoute)
      .withFormUrlEncodedBody(
        "value.day"    -> validAnswer.dateTime.getDayOfMonth.toString,
        "value.month"  -> validAnswer.dateTime.getMonthValue.toString,
        "value.year"   -> validAnswer.dateTime.getYear.toString,
        "value.hour"   -> validAnswer.dateTime.getHour.toString,
        "value.minute" -> validAnswer.dateTime.getMinute.toString,
        "value.amOrPm" -> validAnswer.amOrPm
      )

  "ArrivalTimesAtOffice Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), officeOfTransit.id).toOption.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateTimeInput.localDateTime(form("value"))

      val expectedJson = Json.obj(
        "form"     -> form,
        "mode"     -> NormalMode,
        "lrn"      -> lrn,
        "dateTime" -> viewModel
      )

      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers
        .set(AddAnotherTransitOfficePage(index), officeOfTransit.id)
        .toOption
        .value
        .set(ArrivalTimesAtOfficePage(index), validAnswer)
        .success
        .value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "value.day"    -> validAnswer.dateTime.getDayOfMonth.toString,
          "value.month"  -> validAnswer.dateTime.getMonthValue.toString,
          "value.year"   -> validAnswer.dateTime.getYear.toString,
          "value.hour"   -> validAnswer.dateTime.getHour.toString,
          "value.minute" -> validAnswer.dateTime.getMinute.toString,
          "value.amOrPm" -> validAnswer.amOrPm
        )
      )

      val viewModel = DateTimeInput.localDateTime(filledForm("value"))

      val expectedJson = Json.obj(
        "form"     -> filledForm,
        "mode"     -> NormalMode,
        "lrn"      -> lrn,
        "dateTime" -> viewModel
      )
      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), officeOfTransit.id).toOption.value
      dataRetrievalWithData(userAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), officeOfTransit.id).toOption.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))

      val request        = FakeRequest(POST, arrivalTimesAtOfficeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateTimeInput.localDateTime(boundForm("value"))

      val expectedJson = Json.obj(
        "form"     -> boundForm,
        "mode"     -> NormalMode,
        "lrn"      -> lrn,
        "dateTime" -> viewModel
      )

      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      dataRetrievalNoData()

      val result = route(app, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      dataRetrievalNoData()

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
