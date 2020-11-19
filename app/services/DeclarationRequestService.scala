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

package services

import java.time.LocalDateTime

import cats.data.NonEmptyList
import cats.implicits._
import javax.inject.Inject
import models.domain.{Address, SealDomain}
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.JourneyDomain.Constants
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TransportDetails.DetailsAtBorder
import models.journeyDomain.TransportDetails.DetailsAtBorder.SameDetailsAtBorder
import models.journeyDomain._
import models.messages._
import models.messages.customsoffice._
import models.messages.goodsitem._
import models.messages.guarantee._
import models.messages.header._
import models.messages.trader._
import models.{ConsigneeAddress, ConsignorAddress, EoriNumber, UserAnswers}
import models.journeyDomain.{GuaranteeDetails, ItemSection, JourneyDomain, Packages, TraderDetails, UserAnswersReader}
import models.messages.customsoffice.{CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.{BulkPackage, GoodsItem, RegularPackage, UnpackedPackage}
import models.messages.guarantee.{Guarantee, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.header.{Header, Transport}
import models.messages.trader.{TraderConsignor, TraderPrincipal, TraderPrincipalWithEori, TraderPrincipalWithoutEori}
import models.messages._
import models.{ConsignorAddress, EoriNumber, UserAnswers}
import repositories.InterchangeControlReferenceIdRepository

import scala.concurrent.{ExecutionContext, Future}

trait DeclarationRequestServiceInt {
  def convert(userAnswers: UserAnswers): Future[Option[DeclarationRequest]]
}

class DeclarationRequestService @Inject()(
  icrRepository: InterchangeControlReferenceIdRepository,
  dateTimeService: DateTimeService
)(implicit ec: ExecutionContext)
    extends DeclarationRequestServiceInt {

  override def convert(userAnswers: UserAnswers): Future[Option[DeclarationRequest]] =
    icrRepository.nextInterchangeControlReferenceId().map {
      icrId =>
        UserAnswersReader[JourneyDomain]
          .map(journeyModelToSubmissionModel(_, icrId, dateTimeService.currentDateTime))
          .run(userAnswers)
    }

  private def journeyModelToSubmissionModel(journeyDomain: JourneyDomain,
                                            icr: InterchangeControlReference,
                                            dateTimeOfPrep: LocalDateTime): DeclarationRequest = {

    val JourneyDomain(
      preTaskList,
      movementDetails,
      routeDetails,
      transportDetails,
      traderDetails,
      itemDetails,
      goodsSummary,
      guarantee
    ) = journeyDomain

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
          UnpackedPackage(packageType.code, totalPieces, markOrNumber)
        case Packages.BulkPackages(packageType, _, markOrNumber) =>
          BulkPackage(packageType.code, markOrNumber)
        case Packages.OtherPackages(packageType, howManyPackagesPage, markOrNumber) =>
          RegularPackage(packageType.code, howManyPackagesPage, markOrNumber)
      }

    // TODO finish this off
    def goodsItems(goodsItems: NonEmptyList[ItemSection]): NonEmptyList[GoodsItem] =
      goodsItems.zipWithIndex.map {
        case (itemSection, index) =>
          GoodsItem(
            itemNumber                       = index + 1,
            commodityCode                    = itemSection.itemDetails.commodityCode,
            declarationType                  = None,
            description                      = itemSection.itemDetails.itemDescription,
            grossMass                        = Some(BigDecimal(itemSection.itemDetails.totalGrossMass)), //TODO Pass this as a string rather than BigDecimal
            netMass                          = itemSection.itemDetails.totalNetMass.map(BigDecimal(_)), //TODO same here
            countryOfDispatch                = None,
            countryOfDestination             = None,
            methodOfPayment                  = None,
            commercialReferenceNumber        = None,
            dangerousGoodsCode               = None,
            previousAdministrativeReferences = Seq.empty,
            producedDocuments                = Seq.empty,
            specialMention                   = Seq.empty,
            traderConsignorGoodsItem         = traderConsignor(itemSection.consignor),
            traderConsigneeGoodsItem         = traderConsignee(itemSection.consignee),
            containers                       = Seq.empty,
            packages                         = packages(itemSection.packages).toList,
            sensitiveGoodsInformation        = Seq.empty //TODO look up this
          )
      }

    def principalTrader(traderDetails: TraderDetails): TraderPrincipal =
      traderDetails.principalTraderDetails match {
        case TraderDetails.PersonalInformation(name, Address(buildingAndStreet, city, postcode, _)) =>
          TraderPrincipalWithoutEori(
            name            = name,
            streetAndNumber = buildingAndStreet,
            postCode        = postcode,
            city            = city,
            countryCode     = Constants.principalTraderCountryCode.code
          )
        case TraderDetails.TraderEori(traderEori) =>
          TraderPrincipalWithEori(eori = traderEori.value, None, None, None, None, None)
      }

    def headerConsignor(traderDetails: TraderDetails): Option[TraderConsignor] =
      traderDetails.consignor
        .flatMap {
          case TraderDetails.PersonalInformation(name, address) =>
            Address.prismAddressToConsignorAddress.getOption(address).map {
              case ConsignorAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsignor(name, addressLine1, addressLine3, addressLine2, country.code.code, None)
            }

          case TraderDetails.TraderEori(EoriNumber(eori)) =>
            Some(TraderConsignor("???", "???", "???", "???", "???", Some(eori))) //TODO populate this
        }

    def headerConsignee(traderDetails: TraderDetails): Option[TraderConsignee] =
      traderDetails.consignee
        .flatMap {
          case TraderDetails.PersonalInformation(name, address) =>
            Address.prismAddressToConsigneeAddress.getOption(address).map {
              case ConsigneeAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsignee(name, addressLine1, addressLine3, addressLine2, country.code.code, None)
            }
          case TraderDetails.TraderEori(EoriNumber(eori)) =>
            Some(TraderConsignee("???", "???", "???", "???", "???", Some(eori))) //TODO populate this
        }

    def detailsAtBorderMode(detailsAtBorder: DetailsAtBorder): Option[String] =
      detailsAtBorder match {
        case SameDetailsAtBorder                            => None
        case DetailsAtBorder.NewDetailsAtBorder(mode, _, _) => Some(mode)
      }

    def detailsAtBorderIdCrossing(detailsAtBorder: DetailsAtBorder): Option[String] =
      detailsAtBorder match {
        case SameDetailsAtBorder                                  => None
        case DetailsAtBorder.NewDetailsAtBorder(_, idCrossing, _) => Some(idCrossing)
      }

    def customsOfficeTransit(transitInformation: NonEmptyList[TransitInformation]): Seq[CustomsOfficeTransit] =
      transitInformation.map {
        case TransitInformation(office, arrivalTime) => CustomsOfficeTransit(office, arrivalTime)
      }.toList

    // TODO confirm if authorisedLocationCode is the same thing (else last case just returns None)
    def customsSubPlace(goodsSummary: GoodsSummary): Option[String] =
      goodsSummary.goodSummaryDetails match {
        case GoodsSummary.GoodSummaryNormalDetails(customsApprovedLocation) =>
          customsApprovedLocation
        case GoodsSummary.GoodSummarySimplifiedDetails(authorisedLocationCode, _) =>
          Some(authorisedLocationCode)
      }

    def headerSeals(domainSeals: Seq[SealDomain]): Option[Seals] =
      if (domainSeals.nonEmpty) {
        val sealList = domainSeals.map(_.numberOrMark)
        Some(Seals(domainSeals.size, sealList))
      } else None

    def representative(movementDetails: MovementDetails): Option[Representative] =
      movementDetails.declarationForSomeoneElse match {
        case MovementDetails.DeclarationForSelf =>
          None
        case MovementDetails.DeclarationForSomeoneElse(companyName, capacity) =>
          Some(Representative(companyName, Some(capacity.toString)))
      }

    def traderConsignor(requiredDetails: Option[RequiredDetails]): Option[TraderConsignorGoodsItem] =
      requiredDetails
        .flatMap {
          case ItemTraderDetails.PersonalInformation(name, address) =>
            Address.prismAddressToConsignorAddress.getOption(address).map {
              case ConsignorAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsignorGoodsItem(name, addressLine1, addressLine3, addressLine2, country.code.code, None)
            }

          case ItemTraderDetails.TraderEori(EoriNumber(eori)) =>
            Some(TraderConsignorGoodsItem("???", "???", "???", "???", "???", Some(eori))) //TODO populate this
        }

    def traderConsignee(requiredDetails: Option[RequiredDetails]): Option[TraderConsigneeGoodsItem] =
      requiredDetails
        .flatMap {
          case ItemTraderDetails.PersonalInformation(name, address) =>
            Address.prismAddressToConsigneeAddress.getOption(address).map {
              case ConsigneeAddress(addressLine1, addressLine2, addressLine3, country) =>
                TraderConsigneeGoodsItem(name, addressLine1, addressLine3, addressLine2, country.code.code, None)
            }
          case ItemTraderDetails.TraderEori(EoriNumber(eori)) =>
            Some(TraderConsigneeGoodsItem("???", "???", "???", "???", "???", Some(eori))) //TODO populate this
        }

    DeclarationRequest(
      Meta(
        interchangeControlReference = icr,
        dateOfPreparation           = dateTimeOfPrep.toLocalDate,
        timeOfPreparation           = dateTimeOfPrep.toLocalTime
      ),
      Header(
        refNumHEA4          = preTaskList.lrn.value,
        typOfDecHEA24       = movementDetails.declarationType.code,
        couOfDesCodHEA30    = Some(routeDetails.destinationCountry.code),
        agrLocOfGooCodHEA38 = None, // prelodge
        agrLocOfGooHEA39    = None, // prelodge
        autLocOfGooCodHEA41 = None, //TODO look up this
        plaOfLoaCodHEA46    = None, //TODO look up this (routeDetails)
        couOfDisCodHEA55    = Some(routeDetails.countryOfDispatch.code),
        cusSubPlaHEA66      = customsSubPlace(goodsSummary),
        transportDetails = Transport(
          inlTraModHEA75        = Some(transportDetails.inlandMode.code),
          traModAtBorHEA76      = detailsAtBorderMode(transportDetails.detailsAtBorder),
          ideOfMeaOfTraAtDHEA78 = detailsAtBorderIdCrossing(transportDetails.detailsAtBorder),
          natOfMeaOfTraAtDHEA80 = None, // Have a chat with Amaal about this bit
          ideOfMeaOfTraCroHEA85 = None, // Have a chat with Amaal about this bit
          natOfMeaOfTraCroHEA87 = None, // Have a chat with Amaal about this bit
          typOfMeaOfTraCroHEA88 = None // TODO look up this
        ),
        conIndHEA96        = booleanToInt(movementDetails.containersUsed),
        totNumOfIteHEA305  = itemDetails.size,
        totNumOfPacHEA306  = goodsSummary.numberOfPackages,
        totGroMasHEA307    = goodsSummary.totalMass,
        decDatHEA383       = dateTimeOfPrep.toLocalDate,
        decPlaHEA394       = movementDetails.declarationPlacePage,
        speCirIndHEA1      = None, // safety and security
        traChaMetOfPayHEA1 = None, // TODO look up this
        comRefNumHEA       = None, // safety and security
        secHEA358          = None, // local ref number & security
        conRefNumHEA       = None, // safety and security
        codPlUnHEA357      = None // safety and security
      ),
      principalTrader(traderDetails),
      headerConsignor(traderDetails),
      headerConsignee(traderDetails),
      None, // TODO Lookup this
      CustomsOfficeDeparture(
        referenceNumber = routeDetails.officeOfDeparture
      ),
      customsOfficeTransit(routeDetails.transitInformation),
      CustomsOfficeDestination(
        referenceNumber = routeDetails.destinationOffice
      ),
      None, //TODO Lookup this
      representative(movementDetails),
      headerSeals(goodsSummary.sealNumbers),
      guaranteeDetails(guarantee),
      goodsItems(journeyDomain.itemDetails),
      Seq.empty[Itinerary]
    )
  }
}
