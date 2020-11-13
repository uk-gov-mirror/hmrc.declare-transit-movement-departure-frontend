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
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, Rail}
import models.journeyDomain.TransportDetails._
import models.reference.CountryCode
import pages.{
  AddIdAtDeparturePage,
  ChangeAtBorderPage,
  IdAtDeparturePage,
  IdCrossingBorderPage,
  InlandModePage,
  ModeAtBorderPage,
  ModeCrossingBorderPage,
  NationalityAtDeparturePage,
  NationalityCrossingBorderPage
}

case class TransportDetails(
  inlandMode: InlandMode,
  detailsAtBorder: DetailsAtBorder
)

object TransportDetails {

  implicit val userAnswersParser: UserAnswersParser[Option, TransportDetails] =
    UserAnswersOptionalParser(
      (
        UserAnswersReader[InlandMode],
        UserAnswersReader[DetailsAtBorder]
      ).tupled
    )((TransportDetails.apply _).tupled)

  sealed trait InlandMode

  object InlandMode {

    object Constants {
      val codesSingleDigit: Seq[String] = Rail.Constants.codesSingleDigit ++ Mode5or7.Constants.codesSingleDigit
      val codesDoubleDigit: Seq[String] = Rail.Constants.codesDoubleDigit ++ Mode5or7.Constants.codesDoubleDigit
      val codes: Seq[String]            = codesSingleDigit ++ codesDoubleDigit
    }

    implicit val userAnswersReader: UserAnswersReader[InlandMode] =
      UserAnswersReader[Rail.type].widen[InlandMode] orElse
        UserAnswersReader[Mode5or7].widen[InlandMode] orElse
        UserAnswersReader[NonSpecialMode].widen[InlandMode]

    case object Rail extends InlandMode {

      object Constants {
        val codesSingleDigit: Seq[String] = Seq("2")
        val codesDoubleDigit: Seq[String] = Seq("20")
        val codes: Seq[String]            = codesSingleDigit ++ codesDoubleDigit
      }

      implicit val userAnswersReaderRail: UserAnswersReader[Rail.type] =
        InlandModePage.reader
          .andThen(
            code =>
              Rail.Constants.codes
                .find(_ == code)
                .productR(Rail.some)
          )

    }

    final case class Mode5or7(nationalityAtDeparture: CountryCode) extends InlandMode

    case object Mode5or7 {

      object Constants {
        val codesSingleDigit: Seq[String] = Seq("5", "7")
        val codesDoubleDigit: Seq[String] = Seq("50", "70")
        val codes: Seq[String]            = codesSingleDigit ++ codesDoubleDigit
      }

      implicit val userAnswersReaderMode5or7: UserAnswersReader[Mode5or7] =
        InlandModePage.reader
          .filter(Mode5or7.Constants.codes.contains(_))
          .productR(
            NationalityAtDeparturePage.reader
              .map(Mode5or7(_))
          )

    }

    final case class NonSpecialMode(nationalityAtDeparture: CountryCode, departureId: Option[String]) extends InlandMode

    object NonSpecialMode {

      implicit val userAnswersReaderNonSpecialMode: UserAnswersReader[NonSpecialMode] =
        InlandModePage.reader
          .filterNot(InlandMode.Constants.codes.contains(_))
          .productR(
            AddIdAtDeparturePage.reader
              .flatMap {
                bool =>
                  if (bool) {
                    (
                      NationalityAtDeparturePage.reader,
                      IdAtDeparturePage.reader.map(Some(_))
                    ).tupled.map((NonSpecialMode.apply _).tupled)
                  } else {
                    NationalityAtDeparturePage.reader.map(NonSpecialMode(_, None))
                  }
              }
          )

    }

  }

  sealed trait DetailsAtBorder

  object DetailsAtBorder {

    implicit val reader: UserAnswersReader[DetailsAtBorder] =
      UserAnswersReader[SameDetailsAtBorder.type].widen[DetailsAtBorder] orElse
        UserAnswersReader[NewDetailsAtBorder].widen[DetailsAtBorder]

    object SameDetailsAtBorder extends DetailsAtBorder {

      implicit val userAnswersReader: UserAnswersReader[SameDetailsAtBorder.type] =
        ChangeAtBorderPage.reader
          .filterNot(identity)
          .productR(SameDetailsAtBorder.pure[UserAnswersReader])

    }

    final case class NewDetailsAtBorder(
      mode: String,
      idCrossing: String,
      modeCrossingBorder: ModeCrossingBorder
    ) extends DetailsAtBorder

    object NewDetailsAtBorder {

      implicit val userAnswersReader: UserAnswersReader[NewDetailsAtBorder] =
        ChangeAtBorderPage.reader
          .filter(identity)
          .productR(
            (
              ModeAtBorderPage.reader,
              IdCrossingBorderPage.reader,
              UserAnswersReader[ModeCrossingBorder]
            ).tupled.map((NewDetailsAtBorder.apply _).tupled)
          )

    }

  }

  sealed trait ModeCrossingBorder

  object ModeCrossingBorder {

    implicit val reader: UserAnswersReader[ModeCrossingBorder] =
      ModeCrossingBorderPage.reader.flatMap(
        modeCode =>
          if ((Mode5or7.Constants.codes ++ Rail.Constants.codes).contains(modeCode))
            ModeExemptNationality.pure[UserAnswersReader].widen[ModeCrossingBorder]
          else
            NationalityCrossingBorderPage.reader.map(ModeWithNationality(_))
      )

    object ModeExemptNationality extends ModeCrossingBorder // 2, 20, 5, 50, 7, 70
    final case class ModeWithNationality(nationalityCrossingBorder: CountryCode) extends ModeCrossingBorder
  }

}
