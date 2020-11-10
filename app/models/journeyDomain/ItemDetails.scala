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

package models.journeyDomain

import pages.addItems.CommodityCodePage
import pages._

final case class ItemDetails(
  itemDescription: String,
  totalGrossMass: String,
  addNetMass: Boolean,
  totalNetMass: Option[String],
  isCommodityCodeKnow: Boolean,
  commodityCode: Option[String]
)

object ItemDetails {

  implicit val itemDetailsReader: UserAnswersReader[ItemDetails] = ???
//
//  (
//    ItemDescriptionPage.reader,
//    ItemTotalGrossMassPage.reader,
//    AddTotalNetMassPage.reader,
//    TotalNetMassPage.reader,
//    IsCommodityCodeKnownPage.reader,
//    CommodityCodePage.reader,
//    ItemTotalGrossMassPage.reader
//    ).tupled.map((ItemDetails.apply _).tupled)
}
