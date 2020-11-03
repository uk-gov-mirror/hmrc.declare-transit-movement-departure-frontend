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

import base.{GeneratorSpec, SpecBase}
import generators.JourneyModelGenerators
import models.journeyDomain.RouteDetails.TransitInformation
import models.{Index, LocalDateTimeWithAMPM, UserAnswers}
import pages._

class RouteDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "RouteDetails" - {
    "can be constructed when all the answers have been answered" in {
      forAll(arbitrary[RouteDetails], arbitrary[UserAnswers]) {
        (expected, baseUserAnswers) =>
          val interstitialUserAnswers = baseUserAnswers
            .set(CountryOfDispatchPage, expected.countryOfDispatch)
            .toOption
            .value
            .set(OfficeOfDeparturePage, expected.officeOfDeparture)
            .toOption
            .value
            .set(DestinationCountryPage, expected.destinationCountry)
            .toOption
            .value
            .set(DestinationOfficePage, expected.destinationOffice)
            .toOption
            .value

          val userAnswers = expected.transitInformation.zipWithIndex.foldLeft(interstitialUserAnswers) {
            case (ua, (TransitInformation(transitOffice, arrivalTime), index)) =>
              ua.set(AddAnotherTransitOfficePage(Index(index)), transitOffice)
                .toOption
                .value
                .set(ArrivalTimesAtOfficePage(Index(index)), LocalDateTimeWithAMPM(arrivalTime, "PM"))
                .toOption
                .value
          }

          val result = UserAnswersParser[Option, RouteDetails].run(userAnswers).value

          result mustEqual expected

      }

    }
  }

}
