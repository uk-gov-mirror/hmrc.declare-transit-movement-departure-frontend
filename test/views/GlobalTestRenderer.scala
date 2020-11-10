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

package views

import com.typesafe.config.ConfigFactory
import config.RenderConfig
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.{Application, Configuration, Environment}
import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import renderer.Renderer
import uk.gov.hmrc.nunjucks.{DevelopmentNunjucksRoutesHelper, NunjucksConfiguration, NunjucksConfigurationProvider, NunjucksRenderer, NunjucksSetup}

import scala.concurrent.ExecutionContext.Implicits.global

//object GlobalTestRenderer extends ScalaFutures with IntegrationPatience {
//
//  val (renderer, messagesApi): (Renderer, MessagesApi) = {
//    val app: Application = new GuiceApplicationBuilder().build()
//
//    val rendererInst: Renderer = app.injector.instanceOf[Renderer]
//    val messagesApiInst        = app.injector.instanceOf[MessagesApi]
////    app
////      .stop()
////      .map(
////        _ => (rendererInst, messagesApiInst)
////      )
////      .futureValue
//
//    val renderConfig = new RenderConfig {
//      override def reportAProblemNonJSUrl: String = "reportAProblemNonJSUrl"
//
//      override def reportAProblemPartialUrl: String = "reportAProblemPartialUrl"
//
//      override def betaFeedbackUnauthenticatedUrl: String = "betaFeedbackUnauthenticatedUrl"
//
//      override def signOutUrl: String = "signOutUrl"
//    }
//
//    val environment   = Environment.simple()
//    val nunjucksSetup = new NunjucksSetup(environment)
//    val config        = Configuration(ConfigFactory.load(System.getProperty("config.resource")))
//
//    val asdf = play.api.test.Helpers._
//
//    val nunjucksRenderer: NunjucksRenderer =
//      new NunjucksRenderer(
//        setup = nunjucksSetup,
//        configuration = new NunjucksConfigurationProvider(config, nunjucksSetup).get(),
//        environment = environment,
//        reverseRoutes = new DevelopmentNunjucksRoutesHelper(environment),
//        messagesApi = ???
//      )
//
//    val rendererInst2: Renderer = new Renderer(renderConfig, nunjucksRenderer)
//
//    (rendererInst, messagesApiInst)
//  }
//
//}
