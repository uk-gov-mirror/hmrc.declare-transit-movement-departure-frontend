package pages.addItems

import pages.QuestionPage
import play.api.libs.json.JsPath

case object HowManyPackagesPage extends QuestionPage[Int] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "howManyPackages"
}
