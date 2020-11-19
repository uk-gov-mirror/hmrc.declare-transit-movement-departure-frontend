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

import java.time.LocalDate

import cats.implicits._
import derivable.DeriveNumberOfSeals
import models.ProcedureType
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.GoodSummaryDetails
import pages._
import pages.goodsSummary.{
  AddCustomsApprovedLocationPage,
  AuthorisedLocationCodePage,
  ControlResultDateLimitPage,
  CustomsApprovedLocationPage,
  DeclarePackagesPage,
  TotalGrossMassPage,
  TotalPackagesPage
}

case class GoodsSummary(
  numberOfPackages: Option[Int],
  totalMass: String,
  loadingPlace: Option[String],
  goodSummaryDetails: GoodSummaryDetails,
  sealNumbers: Seq[SealDomain]
)

object GoodsSummary {

  implicit val parser: UserAnswersParser[Option, GoodsSummary] =
    UserAnswersOptionalParser(
      (
        DeclarePackagesPage.reader
          .flatMap(
            bool =>
              if (bool)
                TotalPackagesPage.optionalReader
              else
                none[Int].pure[UserAnswersReader]
          ),
        TotalGrossMassPage.reader,
        none[String].pure[UserAnswersReader],
        UserAnswersReader[GoodSummaryDetails],
        DeriveNumberOfSeals.reader orElse List.empty[SealDomain].pure[UserAnswersReader]
      ).tupled
    )((GoodsSummary.apply _).tupled)

  sealed trait GoodSummaryDetails

  final case class GoodSummaryNormalDetails(customsApprovedLocation: Option[String]) extends GoodSummaryDetails

  object GoodSummaryNormalDetails {

    implicit val goodSummaryNormalDetailsReader: UserAnswersReader[GoodSummaryNormalDetails] =
      ProcedureTypePage.reader
        .filter(_ == ProcedureType.Normal)
        .productR(
          AddCustomsApprovedLocationPage.reader
            .flatMap {
              locationNeeded =>
                if (locationNeeded)
                  CustomsApprovedLocationPage.reader.map(
                    location => GoodSummaryNormalDetails(Some(location))
                  )
                else
                  GoodSummaryNormalDetails(None).pure[UserAnswersReader]
            }
        )
  }

  final case class GoodSummarySimplifiedDetails(authorisedLocationCode: String, controlResultDateLimit: LocalDate) extends GoodSummaryDetails

  object GoodSummarySimplifiedDetails {

    implicit val goodSummarySimplifiedDetailsReader: UserAnswersReader[GoodSummarySimplifiedDetails] =
      ProcedureTypePage.reader
        .filter(_ == ProcedureType.Simplified)
        .productR(
          (
            AuthorisedLocationCodePage.reader,
            ControlResultDateLimitPage.reader
          ).tupled
            .map((GoodSummarySimplifiedDetails.apply _).tupled)
        )

  }

  object GoodSummaryDetails {

    implicit val goodSummaryDetailsReader: UserAnswersReader[GoodSummaryDetails] =
      UserAnswersReader[GoodSummaryNormalDetails].widen[GoodSummaryDetails] orElse
        UserAnswersReader[GoodSummarySimplifiedDetails].widen[GoodSummaryDetails]
  }
}
