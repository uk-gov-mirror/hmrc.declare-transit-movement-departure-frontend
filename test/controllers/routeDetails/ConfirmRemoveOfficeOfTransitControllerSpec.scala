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
import controllers.{routes => mainRoutes}
import forms.ConfirmRemoveOfficeOfTransitFormProvider
import matchers.JsonMatchers
import models.reference.OfficeOfTransit
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.AddAnotherTransitOfficePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import queries.OfficeOfTransitQuery
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class ConfirmRemoveOfficeOfTransitControllerSpec
    extends SpecBase
    with MockNunjucksRendererApp
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with BeforeAndAfterEach {

  def onwardRoute = Call("GET", "/foo")

  val formProvider                                   = new ConfirmRemoveOfficeOfTransitFormProvider()
  val form                                           = formProvider()
  private val mockReferenceDataConnector             = mock[ReferenceDataConnector]
  private val officeOfTransit                        = OfficeOfTransit("id", "name")
  lazy val confirmRemoveOfficeOfTransitRoute: String = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, index, NormalMode).url

  override def beforeEach: Unit = {
    reset(mockReferenceDataConnector)
    super.beforeEach()
  }

  "ConfirmRemoveOfficeOfTransit Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))

      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), "id").toOption.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"            -> form,
        "mode"            -> NormalMode,
        "officeOfTransit" -> s"${officeOfTransit.name} (${officeOfTransit.id})",
        "lrn"             -> lrn,
        "radios"          -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "confirmRemoveOfficeOfTransit.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must return error page when user tries to remove a office of transit that does not exists" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))

      val userAnswers = emptyUserAnswers
        .set(AddAnotherTransitOfficePage(index), "id")
        .toOption
        .value
        .remove(OfficeOfTransitQuery(index))
        .toOption
        .value
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "pageTitle"    -> msg"concurrent.remove.error.title".withArgs("officeOfTransit"),
        "pageHeading"  -> msg"concurrent.remove.error.heading".withArgs("officeOfTransit"),
        "linkText"     -> msg"concurrent.remove.error.noOfficeOfTransit.link.text",
        "redirectLink" -> ""
      )
      templateCaptor.getValue mustEqual "concurrentRemoveError.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must return error page when there are multiple office of transit and user tries to remove the last office of transit that is already removed" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))

      val updatedAnswer = emptyUserAnswers
        .set(AddAnotherTransitOfficePage(index), "id1")
        .success
        .value
        .set(AddAnotherTransitOfficePage(Index(1)), "id2")
        .success
        .value
        .remove(AddAnotherTransitOfficePage(Index(1)))
        .success
        .value

      val confirmRemoveRoute = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, Index(1), NormalMode).url
      val application = applicationBuilder(userAnswers = Some(updatedAnswer))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()
      val request        = FakeRequest(GET, confirmRemoveRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "pageTitle"    -> msg"concurrent.remove.error.title".withArgs("officeOfTransit"),
        "pageHeading"  -> msg"concurrent.remove.error.heading".withArgs("officeOfTransit"),
        "linkText"     -> msg"concurrent.remove.error.multipleOfficeOfTransit.link.text",
        "redirectLink" -> ""
      )

      templateCaptor.getValue mustEqual "concurrentRemoveError.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), "id").toOption.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, confirmRemoveOfficeOfTransitRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val newUserAnswers = UserAnswers(
        id         = userAnswers.id,
        eoriNumber = userAnswers.eoriNumber,
        userAnswers.remove(OfficeOfTransitQuery(index)).success.value.data,
        userAnswers.lastUpdated
      )

      verify(mockSessionRepository, times(1)).set(newUserAnswers)

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getOfficeOfTransit(any())(any(), any())).thenReturn(Future.successful(officeOfTransit))
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), "id").toOption.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
        .build()

      val request        = FakeRequest(POST, confirmRemoveOfficeOfTransitRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"            -> boundForm,
        "mode"            -> NormalMode,
        "officeOfTransit" -> s"${officeOfTransit.name} (${officeOfTransit.id})",
        "lrn"             -> lrn,
        "radios"          -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "confirmRemoveOfficeOfTransit.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, confirmRemoveOfficeOfTransitRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
