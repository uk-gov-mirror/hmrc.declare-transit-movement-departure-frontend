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

import base.{SpecBase, UserAnswersSpecHelper}
import controllers.routes
import models.{Index, NormalMode, UserAnswers}
import models.reference.CountryCode
import models.requests.DataRequest
import pages.{AddAnotherTransitOfficePage, AddSecurityDetailsPage, OfficeOfTransitCountryPage}
import play.api.mvc.{Action, AnyContent, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc.Results._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TraderDetailsOfficesOfTransitFilterSpec extends SpecBase with UserAnswersSpecHelper {

  private def fakeOkResult[A]: A => Future[Result] =
    a => Future.successful(Ok("fake ok result value"))

  "when the index in the request url" - {

    "is invalid, and there is an incomplete loop, must redirect to the first page of that loop" ignore {}

    "is invalid, and all previous loop are complete, must redirect to Add Transit Office page" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(AddSecurityDetailsPage)(false)
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(1)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(1)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(2)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(2)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(3)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(3)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(4)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(4)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(5)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(5)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(6)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(6)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(7)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(7)))("Test")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(8)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(8)))("Test")

      val actionFilter = new TraderDetailsOfficesOfTransitFilter(implicitly)
      val dataRequest  = DataRequest(fakeRequest, userAnswers.eoriNumber, userAnswers)
      val result       = actionFilter.invokeBlock(dataRequest, fakeOkResult)

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(userAnswers.id, NormalMode).url)
    }

    "is valid, and the previous loops are all complete, must return OK" in {
      val userAnswers = emptyUserAnswers
        .unsafeSetVal(AddSecurityDetailsPage)(false)
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("Test")

      val actionFilter = new TraderDetailsOfficesOfTransitFilter(implicitly)
      val dataRequest  = DataRequest(fakeRequest, userAnswers.eoriNumber, userAnswers)
      val result       = actionFilter.invokeBlock(dataRequest, fakeOkResult)

      status(result) mustBe Ok
      contentAsString(result) mustBe "fake ok result value"
    }

    "is valid, and there is an incomplete loop, must redirect to the first page of that loop" ignore {}

  }
}
