package pages.safetyAndSecurity

import pages.QuestionPage
import play.api.libs.json.JsPath

case object SafetyAndSecurityConsignorAddressPage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "safetyAndSecurityConsignorAddress"
}
