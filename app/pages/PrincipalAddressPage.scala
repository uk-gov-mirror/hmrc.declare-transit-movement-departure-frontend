package pages

import models.PrincipalAddress
import play.api.libs.json.JsPath

case object PrincipalAddressPage extends QuestionPage[PrincipalAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "principalAddress"
}
