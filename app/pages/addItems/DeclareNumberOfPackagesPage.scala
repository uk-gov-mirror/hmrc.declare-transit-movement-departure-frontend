package pages.addItems

import pages.QuestionPage
import play.api.libs.json.JsPath

case object DeclareNumberOfPackagesPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "declareNumberOfPackages"
}
