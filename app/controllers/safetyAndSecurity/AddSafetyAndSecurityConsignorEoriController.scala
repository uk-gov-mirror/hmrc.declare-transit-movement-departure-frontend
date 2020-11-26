package controllers

import controllers.actions._
import forms.AddSafetyAndSecurityConsignorEoriFormProvider
import javax.inject.Inject
import models.{Mode, LocalReferenceNumber}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.AddSafetyAndSecurityConsignorEoriPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class AddSafetyAndSecurityConsignorEoriController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    @SafetyAndSecurity navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalActionProvider,
    requireData: DataRequiredAction,
    formProvider: AddSafetyAndSecurityConsignorEoriFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "addSafetyAndSecurityConsignorEori.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddSafetyAndSecurityConsignorEoriPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "lrn"    -> lrn,
        "radios" -> Radios.yesNo(preparedForm("value"))
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "lrn"    -> lrn,
            "radios" -> Radios.yesNo(formWithErrors("value"))
          )

          renderer.render(template, json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddSafetyAndSecurityConsignorEoriPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddSafetyAndSecurityConsignorEoriPage, mode, updatedAnswers))
      )
  }
}
