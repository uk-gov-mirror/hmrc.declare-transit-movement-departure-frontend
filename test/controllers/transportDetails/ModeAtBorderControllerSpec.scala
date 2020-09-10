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

package controllers.transportDetails

import base.SpecBase
import connectors.ReferenceDataConnector
import forms.ModeAtBorderFormProvider
import matchers.JsonMatchers
import models.{NormalMode, TransportModeList}
import navigation.{FakeNavigator, Navigator}
import org.mockito.{ArgumentCaptor, Mock}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.ModeAtBorderPage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport
import controllers.{routes => mainRoutes}
import models.reference.TransportMode
import navigation.annotations.TransportDetails
import utils.transportModesAsJson

import scala.concurrent.Future

class ModeAtBorderControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ModeAtBorderFormProvider()
  val transportMode = TransportMode("1", "Sea transport")
  val transportModes = TransportModeList(Seq(transportMode))
  val form = formProvider(transportModes)

  val mockReferenceDataConnector = mock[ReferenceDataConnector]

  lazy val modeAtBorderRoute = routes.ModeAtBorderController.onPageLoad(lrn, NormalMode).url

  override def beforeEach: Unit = {
    reset(mockReferenceDataConnector)
    super.beforeEach
  }


  "ModeAtBorder Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getTransportModes()(any(), any())).thenReturn(Future.successful(transportModes))


      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request = FakeRequest(GET, modeAtBorderRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "lrn"    -> lrn
      )

      templateCaptor.getValue mustEqual "modeAtBorder.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getTransportModes()(any(), any())).thenReturn(Future.successful(transportModes))

      val userAnswers = emptyUserAnswers.set(ModeAtBorderPage, "1").success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request = FakeRequest(GET, modeAtBorderRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "1"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "lrn"  -> lrn,
        "transportModes" -> transportModesAsJson(filledForm.value, transportModes.transportModes),
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "modeAtBorder.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockReferenceDataConnector.getTransportModes()(any(), any())).thenReturn(Future.successful(transportModes))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind(classOf[Navigator]).qualifiedWith(classOf[TransportDetails]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector)
      )
          .build()

      val request =
        FakeRequest(POST, modeAtBorderRoute)
          .withFormUrlEncodedBody(("value", "1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getTransportModes()(any(), any())).thenReturn(Future.successful(transportModes))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request = FakeRequest(POST, modeAtBorderRoute).withFormUrlEncodedBody(("value", ""))
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

      templateCaptor.getValue mustEqual "modeAtBorder.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, modeAtBorderRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, modeAtBorderRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
