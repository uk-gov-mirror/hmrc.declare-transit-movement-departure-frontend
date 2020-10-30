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

import cats._
import cats.data._
import cats.implicits._
import models.reference.CountryCode
import TransportDetails._
import models.journeyDomain.TransportDetails.DetailsAtBorder.SameDetailsAtBorder
import pages.{ChangeAtBorderPage, InlandModePage}

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
      ???

    object Rail extends InlandMode {

      object Constants {
        val codes: Seq[String] = Seq("2", "20")
      }
    }

    final case class Mode5or7(nationalityAtDeparture: CountryCode) extends InlandMode

    object Mode5or7 {

      object Constants {
        val codes: Seq[String] = Seq("5", "50", "7", "70")
      }
    }

    final case class NonSpecialMode(nationalityAtDeparture: CountryCode, departureId: Option[String]) extends InlandMode
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
