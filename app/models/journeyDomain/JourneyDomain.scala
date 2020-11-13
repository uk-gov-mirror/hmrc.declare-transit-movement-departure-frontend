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

import cats.data._
import cats.implicits._
import models.UserAnswers
import models.journeyDomain.TransportDetails.InlandMode
import models.messages.customsoffice.{CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.{BulkPackage, GoodsItem, RegularPackage, UnpackedPackage}
import models.messages.guarantee.{Guarantee, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.header.{Header, Transport}
import models.messages.trader.TraderPrincipalWithEori
import models.messages.{DeclarationRequest, Meta, booleanToInt}
import utils.Format

case class JourneyDomain(
  preTaskList: PreTaskListDetails,
  movementDetails: MovementDetails,
  routeDetails: RouteDetails,
  transportDetails: TransportDetails,
  traderDetails: TraderDetails,
  itemDetails: NonEmptyList[ItemSection],
  goodsSummary: GoodsSummary,
  guarantee: GuaranteeDetails
)

object JourneyDomain {

  implicit def userAnswersReader: UserAnswersReader[JourneyDomain] = {
    // TOOD: This is a workaround till we remove UserAnswersParser
    implicit def fromUserAnswersParser[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersReader[A] =
      ReaderT[Option, UserAnswers, A](parser.run _)

    for {
      preTaskList      <- UserAnswersReader[PreTaskListDetails]
      movementDetails  <- UserAnswersReader[MovementDetails]
      routeDetails     <- UserAnswersReader[RouteDetails]
      transportDetails <- UserAnswersReader[TransportDetails]
      traderDetails    <- UserAnswersReader[TraderDetails]
      itemDetails      <- UserAnswersReader[NonEmptyList[ItemSection]]
      goodsSummary     <- UserAnswersReader[GoodsSummary]
      guarantee        <- UserAnswersReader[GuaranteeDetails]
    } yield
      JourneyDomain(
        preTaskList,
        movementDetails,
        routeDetails,
        transportDetails,
        traderDetails,
        itemDetails,
        goodsSummary,
        guarantee
      )
  }

  def convert(journeyDomain: JourneyDomain, decDate: LocalDate): DeclarationRequest = {

    val JourneyDomain(preTaskList, movementDetails, routeDetails, transportDetails, traderDetails, itemDetails, goodsSummary, guarantee) = journeyDomain

    def guaranteeDetails(guaranteeDetails: GuaranteeDetails): Guarantee =
      guaranteeDetails match {
        case GuaranteeDetails.GuaranteeReference(guaranteeType, guaranteeReferenceNumber, _, accessCode) =>
          val guaranteeReferenceWithGrn = GuaranteeReferenceWithGrn(guaranteeReferenceNumber, accessCode)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceWithGrn))
        case GuaranteeDetails.GuaranteeOther(guaranteeType, otherReference, _) =>
          val guaranteeReferenceOther = GuaranteeReferenceWithOther(otherReference, None)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceOther))
      }

    def packages(packages: NonEmptyList[Packages]): NonEmptyList[models.messages.goodsitem.Package] =
      packages.map {
        case Packages.UnpackedPackages(packageType, _, totalPieces, markOrNumber) =>
          UnpackedPackage(packageType.toString, totalPieces, markOrNumber)
        case Packages.BulkPackages(packageType, _, markOrNumber) =>
          BulkPackage(packageType.toString, markOrNumber)
        case Packages.OtherPackages(packageType, howManyPackagesPage, markOrNumber) =>
          RegularPackage(packageType.toString, howManyPackagesPage, markOrNumber)
      }

    def goodsItems(goodsItems: NonEmptyList[ItemSection]): NonEmptyList[GoodsItem] = {
      goodsItems.zipWithIndex.map {
        case(itemSection, index) =>
          GoodsItem(
            itemNumber = index + 1,
            commodityCode = itemSection.itemDetails.commodityCode,
            declarationType = None,
            description = itemSection.itemDetails.itemDescription,
            grossMass = Some(BigDecimal(itemSection.itemDetails.totalGrossMass)), //TODO Pass this as a string rather than BigDecimal
            netMass = itemSection.itemDetails.totalNetMass.map(BigDecimal(_)), //TODO same here
            countryOfDispatch = None,
            countryOfDestination = None,
            methodOfPayment = None,
            commercialReferenceNumber = None,
            dangerousGoodsCode = None,
            previousAdministrativeReferences = Seq.empty,
            producedDocuments = Seq.empty,
            specialMention = Seq.empty,
            traderConsignorGoodsItem = None,
            traderConsigneeGoodsItem = None,
            containers = Seq.empty,
            packages = packages(itemSection.packages).toList,
            sensitiveGoodsInformation = Seq.empty
          )
      }
    }

    DeclarationRequest(
      Meta(
        interchangeControlReference = ???, //TODO this service isnt called anywhere
        dateOfPreparation = ???,
        timeOfPreparation = ???
      ),
      Header(
        refNumHEA4 = preTaskList.lrn.toString,
        typOfDecHEA24 = movementDetails.declarationType.code,
        couOfDesCodHEA30 = Some(routeDetails.destinationCountry.code),
        agrLocOfGooCodHEA38 = None,
        agrLocOfGooHEA39 = None,
        autLocOfGooCodHEA41 = None,
        plaOfLoaCodHEA46 = None,
        couOfDisCodHEA55 = Some(routeDetails.countryOfDispatch.code),
        cusSubPlaHEA66 = None,
        transportDetails = Transport(
          inlTraModHEA75 = Some(InlandMode.Constants.code.head),
          traModAtBorHEA76 = Some(transportDetails.detailsAtBorder.mode.toInt),
          ideOfMeaOfTraAtDHEA78 = Some(transportDetails.detailsAtBorder.idCrossing),
          natOfMeaOfTraAtDHEA80 = None,
          ideOfMeaOfTraCroHEA85 = None,
          natOfMeaOfTraCroHEA87 = None,
          typOfMeaOfTraCroHEA88 = None
        ),
        conIndHEA96 = booleanToInt(movementDetails.containersUsed),
        totNumOfIteHEA305 = itemDetails.size,
        totNumOfPacHEA306 = None,
        totGroMasHEA307 = goodsSummary.totalMass,
        decDatHEA383 = decDate,
        decPlaHEA394 = movementDetails.declarationPlacePage,
        speCirIndHEA1 = None,
        traChaMetOfPayHEA1 = None,
        comRefNumHEA = None,
        secHEA358 = None,
        conRefNumHEA = None,
        codPlUnHEA357 = None
      ),
      TraderPrincipalWithEori(
        eori = ???,
        name = ???,
        streetAndNumber = ???,
        postCode = ???,
        city = ???,
        countryCode = ???
      ),
      None,
      None,
      None,
      CustomsOfficeDeparture(
        referenceNumber = journeyDomain.routeDetails.officeOfDeparture
      ),
      Seq.empty[CustomsOfficeTransit],
      CustomsOfficeDestination(
        referenceNumber = journeyDomain.routeDetails.destinationOffice
      ),
      None,
      None,
      None,
      guaranteeDetails(journeyDomain.guarantee),
      goodsItems(journeyDomain.itemDetails)
      )
  }

  // TODO: remove. We will use UserAnswersReader[JourneyDomain].run instead
  def parse(userAnswers: UserAnswers): Option[(MovementDetails, RouteDetails, TraderDetails, TransportDetails)] =
    for {
      movementDetails  <- UserAnswersOptionalParser[MovementDetails].run(userAnswers)
      routeDetails     <- UserAnswersOptionalParser[RouteDetails].run(userAnswers)
      traderDetails    <- UserAnswersOptionalParser[TraderDetails].run(userAnswers)
      transportDetails <- UserAnswersOptionalParser[TransportDetails].run(userAnswers)
    } yield (movementDetails, routeDetails, traderDetails, transportDetails)

}
