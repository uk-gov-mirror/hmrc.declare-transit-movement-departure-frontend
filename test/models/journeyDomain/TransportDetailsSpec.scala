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
import models.UserAnswers
import models.journeyDomain.TransportDetails.DetailsAtBorder._
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, _}
import models.journeyDomain.TransportDetails._
import models.reference.CountryCode
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages._

class TransportDetailsSpec extends SpecBase with GeneratorSpec with TryValues {
  import TransportDetailsSpec._

  "TransportDetail can be parser from UserAnswers" - {
    "when there are no change at the border" - {
      "when inland mode is 'Rail'" in {
        val railModeGen = Gen.oneOf(Rail.Constants.codes)

        forAll(arb[UserAnswers], railModeGen) {
          (baseUserAnswers, railMode) =>
            val userAnswers = setTransportDetailsRail(false, railMode)(baseUserAnswers)

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual TransportDetails(inlandMode = Rail, detailsAtBorder = SameDetailsAtBorder)

        }

      }

      "when inland mode is 'Postal Consignment' or 'Fixed transport installations'" in {
        val modeGen = Gen.oneOf(Mode5or7.Constants.codes)

        forAll(arb[UserAnswers], modeGen, arb[CountryCode]) {
          (baseUserAnswers, mode, countryCode) =>
            val userAnswers = setTransportDetailsMode5or7(false, countryCode, mode)(baseUserAnswers)

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual TransportDetails(inlandMode = Mode5or7(countryCode), detailsAtBorder = SameDetailsAtBorder)

        }
      }

      "when inland mode is anything other than 'Rail', 'Postal Consignment' or 'Fixed transport installations'" in {
        val modeGen = stringsExceptSpecificValues(Rail.Constants.codes ++ Mode5or7.Constants.codes)

        forAll(arb[UserAnswers], modeGen, arb[CountryCode], Gen.option(stringsWithMaxLength(stringMaxLength))) {
          (baseUserAnswers, mode, countryCode, optionalIdAtDeparture) =>
            val userAnswers =
              setTransportDetailsOther(false, countryCode, optionalIdAtDeparture)(baseUserAnswers)

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual TransportDetails(inlandMode = NonSpecialMode(countryCode, optionalIdAtDeparture), detailsAtBorder = SameDetailsAtBorder)

        }
      }
    }
  }

}

object TransportDetailsSpec {

  def setTransportDetail(transportDetails: TransportDetails)(startUserAnswers: UserAnswers): UserAnswers =
    transportDetails.inlandMode match {
      case InlandMode.Rail =>
        setTransportDetailsRail(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type])(startUserAnswers)
      case Mode5or7(nationalityAtDeparture) =>
        setTransportDetailsMode5or7(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type], nationalityAtDeparture)(startUserAnswers)
      case NonSpecialMode(nationalityAtDeparture, departureId) =>
        setTransportDetailsOther(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type], nationalityAtDeparture, departureId)(
          startUserAnswers
        )
    }

  def setTransportDetailsRail(changeAtBorder: Boolean, mode: String = Rail.Constants.codes.head)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
      .set(ChangeAtBorderPage, changeAtBorder)
      .toOption
      .get
      .set(InlandModePage, mode)
      .toOption
      .get

  def setTransportDetailsMode5or7(changeAtBorder: Boolean, countryCode: CountryCode, mode: String = Mode5or7.Constants.codes.head)(
    startUserAnswers: UserAnswers
  ): UserAnswers =
    startUserAnswers
      .set(ChangeAtBorderPage, changeAtBorder)
      .toOption
      .get
      .set(InlandModePage, mode)
      .toOption
      .get
      .set(NationalityAtDeparturePage, countryCode)
      .toOption
      .get

  def setTransportDetailsOther(changeAtBorder: Boolean, countryCode: CountryCode, optionalIdAtDeparture: Option[String], mode: String = "ZZ")(
    startUserAnswers: UserAnswers
  ): UserAnswers = {
    val tmp = startUserAnswers
      .set(ChangeAtBorderPage, changeAtBorder)
      .toOption
      .get
      .set(InlandModePage, Rail.Constants.codes.head)
      .toOption
      .get
      .set(InlandModePage, mode)
      .toOption
      .get
      .set(NationalityAtDeparturePage, countryCode)
      .toOption
      .get
      .set(AddIdAtDeparturePage, optionalIdAtDeparture.isDefined)
      .toOption
      .get

    val userAnswers =
      optionalIdAtDeparture.fold(tmp)(
        idAtDeparture => tmp.set(IdAtDeparturePage, idAtDeparture).toOption.get
      )

    userAnswers
  }

}
