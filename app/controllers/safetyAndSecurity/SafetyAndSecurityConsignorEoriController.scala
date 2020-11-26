package controllers.safetyAndSecurity

import controllers.actions._
import forms.safetyAndSecurity.SafetyAndSecurityConsignorEoriFormProvider
import javax.inject.Inject
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.safetyAndSecurity.SafetyAndSecurityConsignorEoriPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class SafetyAndSecurityConsignorEoriController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       @SafetyAndSecurity navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: SafetyAndSecurityConsignorEoriFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "safetyAndSecurityConsignorEori.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(SafetyAndSecurityConsignorEoriPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "lrn"  -> lrn,
        "mode" -> mode
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "lrn"  -> lrn,
            "mode" -> mode
          )

          renderer.render(template, json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SafetyAndSecurityConsignorEoriPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SafetyAndSecurityConsignorEoriPage, mode, updatedAnswers))
      )
  }
}
