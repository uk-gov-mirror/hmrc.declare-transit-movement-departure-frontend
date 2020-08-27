package controllers

import controllers.actions._
import javax.inject.Inject
import models.{LocalReferenceNumber, UserAnswers}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import viewModels.sections.Section

import scala.concurrent.ExecutionContext

class TraderDetailsCheckYourAnswersController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val sections: Seq[Section] = createSections(request.userAnswers)
      val json = Json.obj("lrn" -> lrn,
        "sections" -> Json.toJson(sections)
      )

      renderer.render("traderDetailsCheckYourAnswers.njk", json).map(Ok(_))
  }

  private def createSections(userAnswers: UserAnswers)(implicit messages: Messages): Seq[Section] = {
    val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers)

    Seq(Section(
      Seq(checkYourAnswersHelper.isPrincipalEoriKnown,
        checkYourAnswersHelper.principalName,
        checkYourAnswersHelper.principalAddress,
        checkYourAnswersHelper.addConsignor,
        checkYourAnswersHelper.isConsignorEoriKnown,
        checkYourAnswersHelper.consignorName,
        checkYourAnswersHelper.consignorEori,
        checkYourAnswersHelper.addConsignee,
        checkYourAnswersHelper.isConsigneeEoriKnown,
        checkYourAnswersHelper.consigneeName,
        checkYourAnswersHelper.whatIsConsigneeEori,
      ).flatten
    ))
  }
}
