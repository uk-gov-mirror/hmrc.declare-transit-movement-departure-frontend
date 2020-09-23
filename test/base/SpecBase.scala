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

package base

import config.FrontendAppConfig
import controllers.actions._
import models.domain.SealDomain
import models.{EoriNumber, Index, LocalReferenceNumber, PrincipalAddress, UserAnswers}
import org.mockito.Mockito
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.Json
import play.api.test.Helpers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nunjucks.NunjucksRenderer

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with OptionValues
    with GuiceOneAppPerSuite
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with BeforeAndAfterEach {

  override def beforeEach {
    Mockito.reset(mockRenderer)
  }

  val userAnswersId             = "id"
  val eoriNumber: EoriNumber    = EoriNumber("EoriNumber")
  val lrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").get
  val sealIndex = Index(0)
  val sealDomain: SealDomain           = SealDomain("sealNumber")
  val sealDomain2: SealDomain           = SealDomain("sealNumber2")

  val index = Index(0)

  val emptyUserAnswers: UserAnswers = UserAnswers(lrn, eoriNumber, Json.obj())

  val principalName: String = "principalName"

  val principalAddress: PrincipalAddress = PrincipalAddress("numberAndStreet", "town", "SW1A 1AA")

  val mockRenderer: NunjucksRenderer = mock[NunjucksRenderer]

  val configKey = "config"

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit def messages: Messages = Helpers.stubMessages()

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalActionProvider]
          .toInstance(new FakeDataRetrievalActionProvider(userAnswers)),
        bind[NunjucksRenderer].toInstance(mockRenderer),
        bind[MessagesApi].toInstance(Helpers.stubMessagesApi())
      )
}
