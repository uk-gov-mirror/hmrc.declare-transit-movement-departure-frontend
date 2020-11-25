package pages

import play.api.libs.json.JsPath

case object PlaceOfUnloadingCodePage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "placeOfUnloadingCode"
}
