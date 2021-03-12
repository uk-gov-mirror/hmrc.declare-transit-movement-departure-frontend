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

import java.time.LocalDateTime

import cats.data.{NonEmptyList, ReaderT}
import cats.implicits._
import derivable.DeriveNumberOfDocuments
import models.DeclarationType.Option4
import models.GuaranteeType.{GuaranteeNotRequired, IndividualGuarantee}
import models.ProcedureType.Normal
import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.GoodSummaryNormalDetails
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.ItemsSecurityTraderDetails.{SecurityPersonalInformation, SecurityTraderDetails}
import models.journeyDomain.MovementDetails.{DeclarationForSelf, NormalMovementDetails}
import models.journeyDomain.Packages.BulkPackages
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TraderDetails.{PersonalInformation, TraderInformation}
import models.journeyDomain.TransportDetails.DetailsAtBorder.NewDetailsAtBorder
import models.journeyDomain.TransportDetails.InlandMode.Mode5or7
import models.journeyDomain.TransportDetails.ModeCrossingBorder.ModeWithNationality
import models.{EoriNumber, GuaranteeType, Index, LocalReferenceNumber, UserAnswers}
import models.reference.{CircumstanceIndicator, Country, CountryCode, CustomsOffice, PackageType}
import pages.AddSecurityDetailsPage
import pages.addItems._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}

final case class ProducedDocument(documentType: String, documentReference: String, extraInformation: Option[String])

object ProducedDocument {

  private def readDocumentType(itemIndex: Index): ReaderT[Option, UserAnswers, Boolean] =
    (for {
      addSecurity <- {
        println(s"\n\n GOT ADD SECURITY")
        AddSecurityDetailsPage.reader
      }
      addRef <- {
        println(s"GOT ADD COMMERCIAL REFERENCE NUMBER")
        AddCommercialReferenceNumberPage.optionalReader
      } // False
      addCircumstance <- {
        println(s"GOT ADD CIRCUMSTANCE INDICATOR")
        AddCircumstanceIndicatorPage.optionalReader
      } // False
    } yield {

      println(s"GREAT SUCCESS $addSecurity \n\n")
      println(s"  \n\n")
      (addSecurity, addRef, addCircumstance, itemIndex.position == 0) match {
        case (true, Some(false), Some(false), true) => {
          println(s"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ A ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
          true.pure[UserAnswersReader]
        }
        case (true, Some(false), Some(true), true) => {
//          println(s"B")
          CircumstanceIndicatorPage.reader.map(x => CircumstanceIndicator.conditionalIndicators.contains(x))
        }
        case _ => {
//          println(s"C")
          AddDocumentsPage(itemIndex).reader
        }
      }
    }).flatMap(x => {
      println(s"WINNER WINNER DINNER DINNER")
      x
    })

  def deriveProducedDocuments(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    readDocumentType(itemIndex)
      .flatMap {
        isTrue =>
          println(s"////////////////////////////// $isTrue")
          if (isTrue) {
            DeriveNumberOfDocuments(itemIndex).reader
              .filter {
                x =>
                  println(s"22222222222222222222222 $x")
                  x.nonEmpty
              }
              .flatMap {
                x =>
                  println(s"##############################")
                  x.zipWithIndex
                    .traverse[UserAnswersReader, ProducedDocument]({
                      case (_, index) =>
                        ProducedDocument.producedDocumentReader(itemIndex, Index(index))
                    })
                    .map(NonEmptyList.fromList)
              }
          } else {
            println(s"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
            none[NonEmptyList[ProducedDocument]].pure[UserAnswersReader]
          }
      }

  def producedDocumentReader(index: Index, referenceIndex: Index): UserAnswersReader[ProducedDocument] =
    for {
      a <- {
        println("X")
        DocumentTypePage(index, referenceIndex).reader
      }
      b <- {
        println("Y")
        DocumentReferencePage(index, referenceIndex).reader
      }
      c <- {
        println("Z")
        addExtraInformationAnswer(index, referenceIndex)
      }
    } yield {
      println("I LIVE")
      ProducedDocument(a, b, c)
    }

//    (
//      DocumentTypePage(index, referenceIndex).reader,
//      DocumentReferencePage(index, referenceIndex).reader,
//      addExtraInformationAnswer(index, referenceIndex)
//    ).tupled.map((ProducedDocument.apply _).tupled)

  private def addExtraInformationAnswer(index: Index, referenceIndex: Index): UserAnswersReader[Option[String]] =
    AddExtraInformationPage(index, referenceIndex).reader.flatMap(
      isTrue =>
        if (isTrue) {
          DocumentExtraInformationPage(index, referenceIndex).reader.map(Some(_))
        } else {
          none[String].pure[UserAnswersReader]
      }
    )
}
