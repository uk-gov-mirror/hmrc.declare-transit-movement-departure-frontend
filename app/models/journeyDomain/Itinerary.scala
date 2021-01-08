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

package models.journeyDomain

import cats.data.{NonEmptyList, ReaderT}
import cats.implicits.{catsStdInstancesForOption, _}
import derivable.DeriveNumberOfCountryOfRouting
import models.reference.CountryCode
import models.{Index, UserAnswers}
import pages.safetyAndSecurity.CountryOfRoutingPage

case class Itinerary(countryCode: CountryCode)

object Itinerary {

  def itineraryReader(index: Index): UserAnswersReader[Itinerary] =
    CountryOfRoutingPage(index).reader.map(Itinerary.apply)

  def readItineraries: ReaderT[Option, UserAnswers, NonEmptyList[Itinerary]] =
    DeriveNumberOfCountryOfRouting.reader
      .filter(_.nonEmpty)
      .flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, Itinerary]({
            case (_, index) =>
              itineraryReader(Index(index))
          })
          .map(NonEmptyList.fromListUnsafe)
      }
}
