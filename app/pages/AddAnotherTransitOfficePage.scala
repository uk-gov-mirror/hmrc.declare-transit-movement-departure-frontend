package pages

import models.AddAnotherTransitOffice
import play.api.libs.json.JsPath

case object AddAnotherTransitOfficePage extends QuestionPage[AddAnotherTransitOffice] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addAnotherTransitOffice"
}
