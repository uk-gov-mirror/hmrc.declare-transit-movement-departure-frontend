package pages.addItems

import pages.QuestionPage
import play.api.libs.json.JsPath

case object DeclareMarkPage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "declareMark"
}
