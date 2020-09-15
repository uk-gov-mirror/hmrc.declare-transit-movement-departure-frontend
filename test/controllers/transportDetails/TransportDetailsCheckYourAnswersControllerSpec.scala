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
import matchers.JsonMatchers
import models.TransportModeList
import models.reference.TransportMode
import navigation.annotations.TransportDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatestplus.mockito.MockitoSugar
import pages.InlandModePage
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository

import scala.concurrent.Future

class TransportDetailsCheckYourAnswersControllerSpec extends SpecBase with MockitoSugar with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  lazy val transportDetailsRoute: String = routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockReferenceDataConnector)
  }

  "TransportDetailsCheckYourAnswers Controller" - {

    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val updatedAnswers  = emptyUserAnswers.set(InlandModePage, "1").success.value
      val application = applicationBuilder(userAnswers = Some(updatedAnswers)).build()
      val request = FakeRequest(GET, routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj("lrn" -> lrn)

      templateCaptor.getValue mustEqual "transportDetailsCheckYourAnswers.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" ignore {

      val mockSessionRepository = mock[SessionRepository]

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val updatedAnswers  = emptyUserAnswers.set(InlandModePage, "1").success.value

      val application: Application =
        applicationBuilder(userAnswers = Some(updatedAnswers))
          .overrides(
            bind(classOf[Navigator]).qualifiedWith(classOf[TransportDetails]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
          .build()

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, transportDetailsRoute)
          .withFormUrlEncodedBody(("1", "test"))

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

  }
}
