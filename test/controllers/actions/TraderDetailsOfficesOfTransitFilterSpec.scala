package controllers.actions

import base.{SpecBase, UserAnswersSpecHelper}
import controllers.routes
import models.{Index, UserAnswers}
import models.reference.CountryCode
import pages.{AddAnotherTransitOfficePage, AddSecurityDetailsPage, OfficeOfTransitCountryPage}
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global

class TraderDetailsOfficesOfTransitFilterSpec extends SpecBase with UserAnswersSpecHelper {

  class Harness(filter: TraderDetailsOfficesOfTransitFilter) {

    def onPageLoad(userAnswers:UserAnswers): Action[AnyContent] = filter {
      _ =>
        Results.Ok
    }
  }
  "Should return OK when previous loop is complete"
  "Should redirect to first page of previous loop when previous loop is incomplete"
  "Should redirect to Add transit office page when loop number is more than maximum " in {
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

    val controller = new Harness(new TraderDetailsOfficesOfTransitFilter(implicitly))
    val result     = controller.onPageLoad(userAnswers)(fakeRequest)

    status(result) mustBe SEE_OTHER

    redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
  }

}
