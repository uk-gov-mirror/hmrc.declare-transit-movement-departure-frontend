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

import cats.data._
import cats.implicits._
import models.journeyDomain.TransportDetails.DetailsAtBorder.SameDetailsAtBorder
import models.journeyDomain.TransportDetails._
import models.reference.CountryCode
import pages.{AddIdAtDeparturePage, IdAtDeparturePage, InlandModePage, NationalityAtDeparturePage}

case class TransportDetails(
  inlandMode: InlandMode,
  detailsAtBorder: DetailsAtBorder
)

object TransportDetails {

  implicit val userAnswersParser: UserAnswersParser[Option, TransportDetails] =
    UserAnswersOptionalParser(
      (
        UserAnswersReader[InlandMode],
        UserAnswersReader[SameDetailsAtBorder.type]
      ).tupled
    )((TransportDetails.apply _).tupled)

  sealed trait InlandMode

  object InlandMode {

    implicit val userAnswersReader: UserAnswersReader[InlandMode] =
      UserAnswersReader[Rail.type].widen[InlandMode] orElse
        UserAnswersReader[Mode5or7].widen[InlandMode] orElse
        UserAnswersReader[NonSpecialMode].widen[InlandMode]

    case object Rail extends InlandMode {

      object Constants {
        val codes: Seq[String] = Seq("2", "20")
      }

      implicit val userAnswersReaderRail: UserAnswersReader[Rail.type] =
        InlandModePage.reader.andThen(
          code =>
            Rail.Constants.codes
              .find(_ == code)
              .productR(Rail.some)
        )

    }

    final case class Mode5or7(nationalityAtDeparture: CountryCode) extends InlandMode

    case object Mode5or7 {

      object Constants {
        val codes: Seq[String] = Seq("5", "50", "7", "70")
      }

      implicit val userAnswersReaderMode5or7: UserAnswersReader[Mode5or7] = {

        val mode5or7 = ReaderT[Option, (String, CountryCode), Mode5or7] {
          x =>
            val (code, nationalityAtDeparture) = x
            Mode5or7.Constants.codes
              .find(_ == code)
              .productR(Mode5or7(nationalityAtDeparture).some)
        }

        (
          InlandModePage.reader,
          NationalityAtDeparturePage.reader
        ).tupled
          .andThen(mode5or7)

      }
    }

    final case class NonSpecialMode(nationalityAtDeparture: CountryCode, departureId: Option[String]) extends InlandMode

    object NonSpecialMode {

      implicit val userAnswersReaderNonSpecialMode: UserAnswersReader[NonSpecialMode] =
        (
          InlandModePage.reader,
          AddIdAtDeparturePage.reader,
          IdAtDeparturePage.optionalReader,
          NationalityAtDeparturePage.reader
        ).tupled
          .map {
            case (_, _, optionalIdAtDeparture, nationalityAtDeparture) =>
              NonSpecialMode(nationalityAtDeparture, optionalIdAtDeparture)
          }

    }

  }

  sealed trait DetailsAtBorder

  object DetailsAtBorder {

    object SameDetailsAtBorder extends DetailsAtBorder {

      implicit val userAnswersReader: UserAnswersReader[SameDetailsAtBorder.type] =
        SameDetailsAtBorder.pure[UserAnswersReader]

    }

    final case class NewDetailsAtBorder(
      mode: String,
      idCrossing: String,
      modeCrossingBorder: ModeCrossingBorder
    ) extends DetailsAtBorder

  }

  sealed trait ModeCrossingBorder

  object ModeCrossingBorder {
    object ModeExemptNationality extends ModeCrossingBorder // 2, 20, 5, 50, 7, 70
    final case class ModeWithNationality(nationalityCrossingBorder: CountryCode) extends ModeCrossingBorder
  }

}
