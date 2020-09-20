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

import java.time.{LocalDateTime, ZoneOffset}

import base.SpecBase
import controllers.{routes => mainRoutes}
import forms.ArrivalTimesAtOfficeFormProvider
import forms.mappings.LocalDateTimeWithAMPM
import matchers.JsonMatchers
import models.NormalMode
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.ArrivalTimesAtOfficePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}
import viewModels.DateTimeInput

import scala.concurrent.Future

class ArrivalTimesAtOfficeControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  val formProvider = new ArrivalTimesAtOfficeFormProvider()
  private def form = formProvider("") //TODO

  def onwardRoute = Call("GET", "/foo")

  val validAnswer: LocalDateTimeWithAMPM = LocalDateTimeWithAMPM(LocalDateTime.now(ZoneOffset.UTC), "am")

  lazy val arrivalTimesAtOfficeRoute = routes.ArrivalTimesAtOfficeController.onPageLoad(lrn, NormalMode).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, arrivalTimesAtOfficeRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, arrivalTimesAtOfficeRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.dateTime.getDayOfMonth.toString,
        "value.month" -> validAnswer.dateTime.getMonthValue.toString,
        "value.year"  -> validAnswer.dateTime.getYear.toString,
        "value.hour"  -> validAnswer.dateTime.getYear.toString,
        "value.minute"  -> validAnswer.dateTime.getYear.toString,
        "value.amOrPm"  -> validAnswer.amOrPm
      )

  "ArrivalTimesAtOffice Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateTimeInput.localDateTime(form("value"))

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )

      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers.set(ArrivalTimesAtOfficePage, validAnswer).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "value.day"   -> validAnswer.dateTime.getDayOfMonth.toString,
          "value.month" -> validAnswer.dateTime.getMonthValue.toString,
          "value.year"  -> validAnswer.dateTime.getYear.toString,
          "value.hour"  -> validAnswer.dateTime.getHour.toString,
          "value.minute"  -> validAnswer.dateTime.getMinute.toString,
          "value.amOrPm"  -> validAnswer.amOrPm
        )
      )

      val viewModel = DateTimeInput.localDateTime(filledForm("value"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )
println("-----------------------"+expectedJson)
println("-----------------------"+jsonCaptor.getValue)
      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, arrivalTimesAtOfficeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateInput.localDate(boundForm("value"))

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )

      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
