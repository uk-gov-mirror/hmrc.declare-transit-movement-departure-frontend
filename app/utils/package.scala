/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import models.reference.{Country, CustomsOffice, TransportMode}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.viewmodels.{Content, MessageInterpolators}

package object utils {
  val defaultOption: JsObject = Json.obj("value" -> "", "text" -> "")

   def countryJsonList(value: Option[Country], countries: Seq[Country]): Seq[JsObject] = {
    val countryJsonList = countries.map {
      country =>
        Json.obj("text" -> country.description, "value" -> country.code, "selected" -> value.contains(country))
    }

     defaultOption +: countryJsonList
  }

  def getCustomsOfficesAsJson(value: Option[CustomsOffice], customsOffices: Seq[CustomsOffice]): Seq[JsObject] = {
    val customsOfficeObjects = customsOffices.map {
      office =>
        Json.obj(
          "value" -> office.id,
          "text" -> s"${office.name} (${office.id})",
          "selected" -> value.contains(office)
        )
    }
    defaultOption +: customsOfficeObjects
  }

  def getTransportModesAsJson(value: Option[TransportMode], transportModes: Seq[TransportMode]): Seq[JsObject] = {
    val transportModeObjects = transportModes.map {
      mode =>
        Json.obj(
          "value" -> mode.code,
          "text" -> s"(${mode.code}) ${mode.description}",
          "selected" -> value.contains(mode)
        )
    }
    defaultOption +: transportModeObjects
  }

  def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }


}
