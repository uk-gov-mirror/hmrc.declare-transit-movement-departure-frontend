package pages.safetyAndSecurity

import pages.QuestionPage
import play.api.libs.json.JsPath

case object AddCarrierPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addCarrier"
}
