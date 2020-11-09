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

import base.SpecBase
import config.{ManageTransitMovementsService, Service}
import models.UserAnswers
import play.api.Configuration
import play.api.libs.json.Json
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import utils.SectionsHelper

class DeclarationSummaryViewModelSpec extends SpecBase with BeforeAndAfterEach {

  private val service: Service = Service("host", "port", "protocol", "startUrl")
  val mockConfiguration        = mock[Configuration]
  when(mockConfiguration.get[Service]("microservice.services.manageTransitMovementsFrontend")).thenReturn(service)

  val manageTransitMovementsService = new ManageTransitMovementsService(mockConfiguration)

  "when the declaration is incomplete" - {
    "returns lrn and sections and movement link" in {
      val userAnswers                      = emptyUserAnswers
      val sut: DeclarationSummaryViewModel = DeclarationSummaryViewModel(manageTransitMovementsService, userAnswers)

      val result = Json.toJsObject(sut)

      val expectedJson =
        Json.obj(
          "lrn"                    -> lrn,
          "sections"               -> new SectionsHelper(userAnswers).getSections,
          "backToTransitMovements" -> service.fullServiceUrl
        )

      result mustEqual expectedJson
    }
  }

  override def beforeEach(): Unit = {
    Mockito.reset(mockConfiguration)
    super.beforeEach()
  }

}
