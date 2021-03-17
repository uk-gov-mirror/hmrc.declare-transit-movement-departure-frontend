/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import models.reference.OfficeOfTransit

class OfficeOfTransitList(officesOfTransit: Seq[OfficeOfTransit]) {

  def getAll: Seq[OfficeOfTransit] =
    officesOfTransit

  def getById(id: String): Option[OfficeOfTransit] =
    officesOfTransit.find(_.id == id)

  def filterNot(customOfficeIds: Seq[String]): Seq[OfficeOfTransit] =
    officesOfTransit.filterNot(
      office => customOfficeIds.contains(office.id)
    )

  override def equals(obj: Any): Boolean = obj match {
    case x: OfficeOfTransitList => x.getAll == getAll
    case _                      => false
  }

}

object OfficeOfTransitList {

  def apply(officesOfTransit: Seq[OfficeOfTransit]): OfficeOfTransitList =
    new OfficeOfTransitList(officesOfTransit)

}
