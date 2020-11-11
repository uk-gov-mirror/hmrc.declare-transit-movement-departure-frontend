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

import cats.data.{Kleisli, ReaderT}
import cats.implicits._
import models.{Index, UserAnswers}
import pages._
import pages.addItems.CommodityCodePage

final case class ItemDetails(
  itemDescription: String,
  totalGrossMass: String,
  totalNetMass: Option[String],
  commodityCode: Option[String]
)

object ItemDetails {

  private def readTotalNetMassPage(index: Index): UserAnswersReader[Option[String]] =
    AddTotalNetMassPage(index).reader
      .flatMap {
        bool =>
          if (bool) TotalNetMassPage(index).reader.map(Some(_))
          else none[String].pure[UserAnswersReader]
      }

  def itemDetailsReader(index: Index): UserAnswersReader[ItemDetails] =
    (
      ItemDescriptionPage(index).reader,
      ItemTotalGrossMassPage(index).reader,
      readTotalNetMassPage(index),
      CommodityCodePage(index).optionalReader
    ).tupled.map((ItemDetails.apply _).tupled)

}
