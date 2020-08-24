package models

import play.api.libs.json._

case class PrincipalAddress (Number and street: String, Town: String)

object PrincipalAddress {
  implicit val format = Json.format[PrincipalAddress]
}
