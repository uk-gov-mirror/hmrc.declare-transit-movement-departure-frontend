package pages.safetyAndSecurity

import pages.QuestionPage
import play.api.libs.json.JsPath

case object CarrierNamePage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "carrierName"
}
