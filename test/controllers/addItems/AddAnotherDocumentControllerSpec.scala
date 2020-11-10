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

package controllers.addItems

import base.{MockNunjucksRendererApp, SpecBase}
import controllers.{routes => mainRoutes}
import forms.addItems.AddAnotherDocumentFormProvider
import matchers.JsonMatchers
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.AddItems
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.{AddAnotherDocumentPage, DocumentTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.AddItemsCheckYourAnswersHelper

import scala.concurrent.Future

class AddAnotherDocumentControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new AddAnotherDocumentFormProvider()
  private val form         = formProvider(index)
  private val template     = "addItems/addAnotherDocument.njk"

  lazy val addAnotherDocumentRoute = routes.AddAnotherDocumentController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItems]).toInstance(new FakeNavigator(onwardRoute)))

  "AddAnotherDocument Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(DocumentTypePage(index, documentIndex), "test").success.value
      dataRetrievalWithData(userAnswers)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(GET, addAnotherDocumentRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])
      val result         = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> form,
        "lrn"       -> lrn,
        "pageTitle" -> msg"addAnotherDocument.title.plural".withArgs(1),
        "heading"   -> msg"addAnotherDocument.heading.plural".withArgs(1),
        "radios"    -> Radios.yesNo(form("value"))
      )
      templateCaptor.getValue mustEqual "addItems/addAnotherDocument.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(DocumentTypePage(index, documentIndex), "test").success.value

      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(POST, addAnotherDocumentRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> boundForm,
        "pageTitle" -> msg"addAnotherDocument.title.plural".withArgs(1),
        "heading"   -> msg"addAnotherDocument.heading.plural".withArgs(1),
        "lrn"       -> lrn,
        "radios"    -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherDocument.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, addAnotherDocumentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
