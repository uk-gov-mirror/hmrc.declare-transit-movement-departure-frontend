package controllers

import controllers.actions._
import forms.ConveyanceReferenceNumberFormProvider
import javax.inject.Inject
import models.{Mode, LocalReferenceNumber}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.ConveyanceReferenceNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class ConveyanceReferenceNumberController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       @SafetyAndSecurity navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: ConveyanceReferenceNumberFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "conveyanceReferenceNumber.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(ConveyanceReferenceNumberPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ConveyanceReferenceNumberPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ConveyanceReferenceNumberPage, mode, updatedAnswers))
      )
  }
}
