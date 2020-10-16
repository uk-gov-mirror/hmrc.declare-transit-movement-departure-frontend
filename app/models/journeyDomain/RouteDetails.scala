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

import java.time.LocalDateTime

import cats.data._
import cats.implicits._
import derivable.DeriveNumberOfOfficeOfTransits
import models.reference.CountryCode
import pages._
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class TransitInformation(
  transitOffice: String,
  arrivalTime: LocalDateTime
)

object TransitInformation {

  implicit val reads: Reads[TransitInformation] =
    ((__ \ AddAnotherTransitOfficePage.key).read[String] and
      (__ \ ArrivalTimesAtOfficePage.key \ "dateTime").read[LocalDateTime])(TransitInformation(_, _))

  implicit val readSeqTransitInformation = {
    val readArrayOfTransitOffices: UserAnswersReader[List[JsObject]] =
      DeriveNumberOfOfficeOfTransits.read

    val readTransitOffices =
      ReaderT[Option, List[JsObject], List[TransitInformation]] {
        _.traverse(_.validate[TransitInformation].asOpt)
      }.flatMapF(NonEmptyList.fromList)

    readArrayOfTransitOffices andThen readTransitOffices
  }
}

final case class RouteDetails(
  countryOfDispatch: CountryCode,
  officeOfDeparture: String,
  destinationCountry: CountryCode,
  destinationOffice: String,
  transitInformation: NonEmptyList[TransitInformation]
)

object RouteDetails {

  implicit val makeSimplifiedMovementDetails: ParseUserAnswers[Option, RouteDetails] =
    ParseUserAnswers(
      (
        CountryOfDispatchPage.read,
        OfficeOfDeparturePage.read,
        DestinationCountryPage.read,
        DestinationOfficePage.read,
        UserAnswersReader[NonEmptyList[TransitInformation]]
      ).tupled
    )(RouteDetails.apply _ tupled)
}
