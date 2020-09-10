package models

import play.api.libs.json._

case class AddAnotherTransitOffice (Which transit office do you want to add?: String, field2: String)

object AddAnotherTransitOffice {
  implicit val format = Json.format[AddAnotherTransitOffice]
}
