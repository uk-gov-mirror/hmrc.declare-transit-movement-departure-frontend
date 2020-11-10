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
import connectors.ReferenceDataConnector
import matchers.JsonMatchers
import models.PreviousDocumentTypeList
import models.reference.PreviousDocumentType
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class ItemsCheckYourAnswersControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with JsonMatchers {

  private val mockRefDataConnector = mock[ReferenceDataConnector]
  private val template             = "addItems/itemsCheckYourAnswers.njk"

  private val documentTypeList = PreviousDocumentTypeList(
    Seq(
      PreviousDocumentType("T1", "Description T1"),
      PreviousDocumentType("T2F", "Description T2F")
    )
  )

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))

  override def beforeEach: Unit = {
    reset(mockRefDataConnector)
    super.beforeEach
  }

  "ItemsCheckYourAnswers Controller" - {

    "return OK and the correct view for a GET" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getPreviousDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request        = FakeRequest(GET, routes.ItemsCheckYourAnswersController.onPageLoad(lrn, index).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"         -> lrn,
        "nextPageUrl" -> routes.AddAnotherItemController.onPageLoad(lrn).url
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)
    }
  }
}
