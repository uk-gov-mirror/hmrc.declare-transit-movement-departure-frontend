package pages.addItems

import models.Index
import pages.QuestionPage
import play.api.libs.json.JsPath

case class CommodityCodePage(index: Index) extends QuestionPage[String] {

  override def path: JsPath = JsPath \ Items \ index.position \ toString

  override def toString: String = "commodityCode"
}
