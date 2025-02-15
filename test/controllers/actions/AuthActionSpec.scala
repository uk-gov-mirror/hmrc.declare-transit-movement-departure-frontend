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

package controllers.actions

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.EnrolmentStoreConnector
import controllers.actions.AuthActionSpec._
import controllers.routes
import matchers.JsonMatchers.containJson
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import play.api.test.Helpers._
import play.twirl.api.Html
import renderer.Renderer
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{~, Retrieval}
import uk.gov.hmrc.auth.{core => authClient}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase with AppWithDefaultMockFixtures {

  class Harness(authAction: IdentifierAction) {

    def onPageLoad(): Action[AnyContent] = authAction {
      _ =>
        Results.Ok
    }
  }

  val mockAuthConnector: AuthConnector                     = mock[AuthConnector]
  val mockEnrolmentStoreConnector: EnrolmentStoreConnector = mock[EnrolmentStoreConnector]
  val mockUIRender: Renderer                               = mock[Renderer]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
      .overrides(bind[EnrolmentStoreConnector].toInstance(mockEnrolmentStoreConnector))
      .overrides(bind[Renderer].toInstance(mockUIRender))

  val enrolmentsWithoutEori: Enrolments = Enrolments(
    Set(
      Enrolment(
        key = "IR-SA",
        identifiers = Seq(
          EnrolmentIdentifier(
            "UTR",
            "123"
          )
        ),
        state = "Activated"
      ),
      Enrolment(
        key         = "HMCE-NCTS-ORG",
        identifiers = Seq.empty,
        state       = "Activated"
      ),
      Enrolment(
        key = "IR-CT",
        identifiers = Seq(
          EnrolmentIdentifier(
            "UTR",
            "456"
          )
        ),
        state = "Activated"
      )
    )
  )

  val enrolmentsWithEori: Enrolments = Enrolments(
    Set(
      Enrolment(
        key = "IR-SA",
        identifiers = Seq(
          EnrolmentIdentifier(
            "UTR",
            "123"
          )
        ),
        state = "Activated"
      ),
      Enrolment(
        key = "HMCE-NCTS-ORG",
        identifiers = Seq(
          EnrolmentIdentifier(
            "VATRegNoTURN",
            "123"
          )
        ),
        state = "NotYetActivated"
      ),
      Enrolment(
        key = "HMCE-NCTS-ORG",
        identifiers = Seq(
          EnrolmentIdentifier(
            "VATRegNoTURN",
            "456"
          )
        ),
        state = "Activated"
      )
    )
  )

  val enrolmentsWithEoriButNoActivated: Enrolments = Enrolments(
    Set(
      Enrolment(
        key = "IR-SA",
        identifiers = Seq(
          EnrolmentIdentifier(
            "UTR",
            "123"
          )
        ),
        state = "Activated"
      ),
      Enrolment(
        key = "HMCE-NCTS-ORG",
        identifiers = Seq(
          EnrolmentIdentifier(
            "VATRegNoTURN",
            "123"
          )
        ),
        state = "NotYetActivated"
      )
    )
  )

  "Auth Action" - {

    "when the user hasn't logged in" - {

      "must redirect the user to log in " in {

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken),
                                                           frontendAppConfig,
                                                           bodyParsers,
                                                           mockEnrolmentStoreConnector,
                                                           mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "when the user's session has expired" - {

      "must redirect the user to log in " in {

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired),
                                                           frontendAppConfig,
                                                           bodyParsers,
                                                           mockEnrolmentStoreConnector,
                                                           mockUIRender)
        val controller             = new Harness(authAction)
        val result: Future[Result] = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "when the user doesn't have sufficient enrolments" - {

      "must redirect the user to the unauthorised page" in {

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments),
                                                           frontendAppConfig,
                                                           bodyParsers,
                                                           mockEnrolmentStoreConnector,
                                                           mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "when the user doesn't have sufficient confidence level" - {

      "must redirect the user to the unauthorised page" in {

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel),
                                                           frontendAppConfig,
                                                           bodyParsers,
                                                           mockEnrolmentStoreConnector,
                                                           mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "when the user used an unaccepted auth provider" - {

      "must redirect the user to the unauthorised page" in {

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider),
                                                           frontendAppConfig,
                                                           bodyParsers,
                                                           mockEnrolmentStoreConnector,
                                                           mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "when the user has an unsupported affinity group" - {

      "must redirect the user to the unauthorised page" in {

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAffinityGroup),
                                                           frontendAppConfig,
                                                           bodyParsers,
                                                           mockEnrolmentStoreConnector,
                                                           mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "when the user has an unsupported credential role" - {

      "must redirect the user to the unauthorised page" in {

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole),
                                                           frontendAppConfig,
                                                           bodyParsers,
                                                           mockEnrolmentStoreConnector,
                                                           mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "AuthAction" - {
      "must return Ok when given enrolments with eori" in {

        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(enrolmentsWithEori ~ Some("testName")))

        setNoExistingUserAnswers()

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers, mockEnrolmentStoreConnector, mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe OK
      }

      "must redirect to unauthorised page when given enrolments without eori" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(enrolmentsWithoutEori ~ Some("testName")))

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers, mockEnrolmentStoreConnector, mockUIRender)

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }

      "must redirect to unauthorised page with group access when given user has no active enrolments but group has" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(enrolmentsWithEoriButNoActivated ~ Some("testName")))
        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), any())(any())).thenReturn(Future.successful(true))
        when(mockUIRender.render(any())(any())).thenReturn(Future.successful(Html("")))

        val templateCaptor = ArgumentCaptor.forClass(classOf[String])

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers, mockEnrolmentStoreConnector, mockUIRender)
        val controller = new Harness(authAction)

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe UNAUTHORIZED

        verify(mockUIRender, times(1)).render(templateCaptor.capture())(any())
        templateCaptor.getValue mustBe "unauthorisedWithGroupAccess.njk"
      }

      "must redirect to unauthorised page with group access when given user has no enrolments but group has" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), any())(any())).thenReturn(Future.successful(true))
        when(mockUIRender.render(any())(any())).thenReturn(Future.successful(Html("")))

        val templateCaptor = ArgumentCaptor.forClass(classOf[String])

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers, mockEnrolmentStoreConnector, mockUIRender)
        val controller = new Harness(authAction)

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe UNAUTHORIZED

        verify(mockUIRender, times(1)).render(templateCaptor.capture())(any())
        templateCaptor.getValue mustBe "unauthorisedWithGroupAccess.njk"
      }

      "must redirect to unauthorised page without group access when given both user and group has no enrolments" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), any())(any())).thenReturn(Future.successful(false))
        when(mockUIRender.render(any(), any())(any())).thenReturn(Future.successful(Html("")))

        val templateCaptor = ArgumentCaptor.forClass(classOf[String])
        val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers, mockEnrolmentStoreConnector, mockUIRender)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe UNAUTHORIZED

        verify(mockUIRender, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

        val expectedJson = Json.obj("requestAccessToNCTSUrl" -> frontendAppConfig.enrolmentManagementFrontendEnrolUrl)

        templateCaptor.getValue mustEqual "unauthorisedWithoutGroupAccess.njk"
        jsonCaptor.getValue must containJson(expectedJson)
      }

      "must redirect to unauthorised page without group access when given user has no enrolments and there is no group" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(Enrolments(Set.empty) ~ None))
        when(mockUIRender.render(any(), any())(any())).thenReturn(Future.successful(Html("")))
        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), any())(any())).thenReturn(Future.successful(false))

        val templateCaptor = ArgumentCaptor.forClass(classOf[String])
        val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

        val bodyParsers       = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers, mockEnrolmentStoreConnector, mockUIRender)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe UNAUTHORIZED

        verify(mockUIRender, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

        val expectedJson = Json.obj("requestAccessToNCTSUrl" -> frontendAppConfig.enrolmentManagementFrontendEnrolUrl)

        templateCaptor.getValue mustEqual "unauthorisedWithoutGroupAccess.njk"
        jsonCaptor.getValue must containJson(expectedJson)

      }
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuthConnector)
    reset(mockEnrolmentStoreConnector)
    reset(mockUIRender)
  }
}

object AuthActionSpec {

  implicit class RetrievalsUtil[A](val retrieval: A) extends AnyVal {
    def `~`[B](anotherRetrieval: B): A ~ B = authClient.retrieve.~(retrieval, anotherRetrieval)
  }
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
