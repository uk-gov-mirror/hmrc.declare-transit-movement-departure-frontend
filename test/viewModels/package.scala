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

import models.Status
import play.api.libs.json.{JsArray, JsObject, Json, Reads}

package object viewModels {

  implicit class SectionSpecHelper(vm: TaskListViewModel) {

    def getSection(sectionName: String): Option[JsObject] = {
      val json     = Json.toJsObject(vm)
      val sections = (json \ TaskListViewModel.Constants.sections).as[JsArray]
      sections.value
        .find(
          section => (section \ "name").as[String] == sectionName
        )
        .map(_.as[JsObject])
    }

    def getStatus(sectionName: String): Option[Status] =
      getSection(sectionName: String).map(
        section => (section \ "status").as[Status]
      )

    def getHref(sectionName: String): Option[String] =
      getSection(sectionName: String).map(
        section => (section \ "href").as[String]
      )

  }

  implicit val readsStatus: Reads[Status] = implicitly[Reads[String]].map {
    case "notStarted" => Status.NotStarted
    case "inProgress" => Status.InProgress
    case "completed"  => Status.Completed
    case _            => throw new Exception(s"Invalid string value for ${Status.getClass}")
  }
}
