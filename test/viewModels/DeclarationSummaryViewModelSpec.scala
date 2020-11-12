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

package viewModels

import base.{GeneratorSpec, SpecBase}
import config.{ManageTransitMovementsService, Service}
import generators.JourneyModelGenerators
import models.EoriNumber
import models.journeyDomain.MovementDetailsSpec._
import models.journeyDomain.RouteDetailsSpec._
import models.journeyDomain.TraderDetailsSpec._
import models.journeyDomain.TransportDetailsSpec._
import models.journeyDomain.{MovementDetails, RouteDetails}
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.Configuration
import play.api.libs.json.Json
import utils.SectionsHelper

class DeclarationSummaryViewModelSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with BeforeAndAfterEach {

  private val service: Service = Service("host", "port", "protocol", "startUrl")
  val mockConfiguration        = mock[Configuration]
  when(mockConfiguration.get[Service]("microservice.services.manageTransitMovementsFrontend")).thenReturn(service)

  val manageTransitMovementsService = new ManageTransitMovementsService(mockConfiguration)

  "when the declaration is incomplete" - {
    "returns lrn, sections, movement link and indicator for incomplete declaration" in {
      val userAnswers                      = emptyUserAnswers
      val sut: DeclarationSummaryViewModel = DeclarationSummaryViewModel(manageTransitMovementsService, userAnswers)

      val result = Json.toJsObject(sut)

      val expectedJson =
        Json.obj(
          "lrn"                    -> lrn,
          "sections"               -> new SectionsHelper(userAnswers).getSections,
          "backToTransitMovements" -> service.fullServiceUrl,
          "isDeclarationComplete"  -> false
        )

      result mustEqual expectedJson
    }

    "returns lrn, sections, movement link and indicator for complete declaration" in {
      forAll(arb[MovementDetails], arb[RouteDetails], arb[EoriNumber]) {
        case (movement, routeDetails, principalEori) =>
          val userAnswers = (
            ((setMovementDetails(movement)) _) andThen
              (setRouteDetails(routeDetails) _) andThen
              (setTraderDetailsPrincipalEoriOnly(principalEori) _) andThen
              (setTransportDetailsRail(false) _)
          )(emptyUserAnswers)

          val sut = DeclarationSummaryViewModel(manageTransitMovementsService, userAnswers)

          val result = Json.toJsObject(sut)

          val expectedJson =
            Json.obj(
              "lrn"                    -> lrn,
              "sections"               -> new SectionsHelper(userAnswers).getSections,
              "backToTransitMovements" -> service.fullServiceUrl,
              "isDeclarationComplete"  -> true,
              "onSubmitUrl"            -> DeclarationSummaryViewModel.nextPage(lrn).url
            )

          result mustEqual expectedJson
      }
    }

  }

  override def beforeEach(): Unit = {
    Mockito.reset(mockConfiguration)
    super.beforeEach()
  }

}
