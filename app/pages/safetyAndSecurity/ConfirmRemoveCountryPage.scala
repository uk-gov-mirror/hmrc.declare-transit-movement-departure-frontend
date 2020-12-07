package pages.safetyAndSecurity

import models.Index
import pages.QuestionPage
import play.api.libs.json.JsPath

case class ConfirmRemoveCountryPage(index: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "confirmRemoveCountry"
}
