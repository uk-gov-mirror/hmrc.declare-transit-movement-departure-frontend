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

package services

import java.time.LocalDateTime

import cats.data.NonEmptyList
import cats.implicits._
import javax.inject.Inject
import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetails}
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.JourneyDomain.Constants
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.SafetyAndSecurity.SecurityTraderDetails
import models.journeyDomain.TransportDetails.DetailsAtBorder.SameDetailsAtBorder
import models.journeyDomain.TransportDetails.{DetailsAtBorder, InlandMode}
import models.journeyDomain.{GuaranteeDetails, ItemSection, Itinerary, JourneyDomain, Packages, TraderDetails, UserAnswersReader, _}
import models.messages._
import models.messages.customsoffice.{CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.{BulkPackage, GoodsItem, RegularPackage, UnpackedPackage, _}
import models.messages.guarantee.{Guarantee, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.header.{Header, Transport}
import models.messages.safetyAndSecurity._
import models.messages.trader.{TraderConsignor, TraderPrincipal, TraderPrincipalWithEori, TraderPrincipalWithoutEori, _}
import models.{CarrierAddress, ConsigneeAddress, ConsignorAddress, EoriNumber, UserAnswers}
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
      guarantee,
      safetyAndSecurity
    ) = journeyDomain

    def guaranteeDetails(guaranteeDetails: GuaranteeDetails): Guarantee =
      guaranteeDetails match {
        case GuaranteeDetails.GuaranteeReference(guaranteeType, guaranteeReferenceNumber, _, accessCode) =>
          val guaranteeReferenceWithGrn = GuaranteeReferenceWithGrn(guaranteeReferenceNumber, accessCode)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceWithGrn))
        case GuaranteeDetails.GuaranteeOther(guaranteeType, otherReference) =>
          val guaranteeReferenceOther = GuaranteeReferenceWithOther(otherReference, None)
          Guarantee(guaranteeType.toString, Seq(guaranteeReferenceOther))
      }
    def additionalInformationLiabilityAmount(guaranteeDetails: GuaranteeDetails) =
      guaranteeDetails match {
        case GuaranteeDetails.GuaranteeReference(_, guaranteeReferenceNumber, liabilityAmount, _) =>
          Seq(specialMentionLiability(liabilityAmount, guaranteeReferenceNumber))

        case _ => Seq.empty
      }

    def specialMentionLiability(liabilityAmount: String, guaranteeReferenceNumber: String): SpecialMentionGuaranteeLiabilityAmount =
      liabilityAmount match {
        case GuaranteeReference.defaultLiability =>
          val defaultLiabilityAmount = s"${GuaranteeReference.defaultLiability}EUR$guaranteeReferenceNumber"
          SpecialMentionGuaranteeLiabilityAmount("CAL", defaultLiabilityAmount)

        case otherAmount =>
          val notDefaultAmount = s"${otherAmount}GBP$guaranteeReferenceNumber"
          SpecialMentionGuaranteeLiabilityAmount("CAL", notDefaultAmount)
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
    def goodsItems(goodsItems: NonEmptyList[ItemSection], guaranteeDetails: GuaranteeDetails): NonEmptyList[GoodsItem] =
      goodsItems.zipWithIndex.map {
        case (itemSection, index) =>
          GoodsItem(
            itemNumber                       = index + 1,
            commodityCode                    = itemSection.itemDetails.commodityCode,
            declarationType                  = None,
            description                      = itemSection.itemDetails.itemDescription,
            grossMass                        = Some(BigDecimal(itemSection.itemDetails.totalGrossMass)),
            netMass                          = itemSection.itemDetails.totalNetMass.map(BigDecimal(_)),
            countryOfDispatch                = None,
            countryOfDestination             = None,
            methodOfPayment                  = itemSection.itemSecurityTraderDetails.flatMap(_.methodOfPayment),
            commercialReferenceNumber        = itemSection.itemSecurityTraderDetails.flatMap(_.commercialReferenceNumber),
            dangerousGoodsCode               = itemSection.itemSecurityTraderDetails.flatMap(_.dangerousGoodsCode),
            previousAdministrativeReferences = Seq.empty,
            producedDocuments                = Seq.empty,
            specialMention                   = if (index == 0) additionalInformationLiabilityAmount(guaranteeDetails) else Seq.empty,
            traderConsignorGoodsItem         = traderConsignor(itemSection.consignor),
            traderConsigneeGoodsItem         = traderConsignee(itemSection.consignee),
            containers                       = Seq.empty,
            packages                         = packages(itemSection.packages).toList,
            sensitiveGoodsInformation        = Seq.empty, //TODO look up this
            GoodsItemSafetyAndSecurityConsignor(itemSection.itemSecurityTraderDetails),
            GoodsItemSafetyAndSecurityConsignee(itemSection.itemSecurityTraderDetails)
          )
      }

    def GoodsItemSafetyAndSecurityConsignor(itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails]): Option[GoodsItemSecurityConsignor] =
      itemSecurityTraderDetails.flatMap {
        x =>
          x.consignor.map {
            case ItemsSecurityTraderDetails.SecurityPersonalInformation(name, Address(buildingAndStreet, city, postcode, _)) =>
              ItemsSecurityConsignorWithoutEori(name, buildingAndStreet, postcode, city, "GB")
            case ItemsSecurityTraderDetails.SecurityTraderEori(eori) =>
              ItemsSecurityConsignorWithEori(eori.value)
          }
      }

    def GoodsItemSafetyAndSecurityConsignee(itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails]): Option[GoodsItemSecurityConsignee] =
      itemSecurityTraderDetails.flatMap {
        x =>
          x.consignor.map {
            case ItemsSecurityTraderDetails.SecurityPersonalInformation(name, Address(buildingAndStreet, city, postcode, _)) =>
              ItemsSecurityConsigneeWithoutEori(name, buildingAndStreet, postcode, city, "GB")
            case ItemsSecurityTraderDetails.SecurityTraderEori(eori) =>
              ItemsSecurityConsigneeWithEori(eori.value)
          }
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

    def nationalityAtDeparture(inlandMode: InlandMode): Option[String] =
      inlandMode match {
        case InlandMode.Mode5or7(_, nationalityAtDeparture)          => Some(nationalityAtDeparture.code)
        case InlandMode.NonSpecialMode(_, nationalityAtDeparture, _) => Some(nationalityAtDeparture.code)
        case _                                                       => None
      }

    def identityOfTransportAtDeparture(inlandMode: InlandMode): Option[String] =
      inlandMode match {
        case InlandMode.NonSpecialMode(_, _, departureId) => departureId
        case _                                            => None
      }

    def agreedLocationOfGoods(movementDetails: MovementDetails, goodsSummaryDetails: GoodSummaryDetails): Option[String] =
      (movementDetails, goodsSummaryDetails) match {
        case (MovementDetails.NormalMovementDetails(_, prelodge, _, _, _), GoodSummaryNormalDetails(approvedLocation)) =>
          if (prelodge) Some("Pre-lodge") else approvedLocation
        case _ => None
      }

    def safetyAndSecurityFlag(boolFlag: Boolean): Int = if (boolFlag) 1 else 0

    def safetyAndSecurityConsignee(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityConsignee] =
      securityTraderDetails
        .flatMap {
          case SafetyAndSecurity.PersonalInformation(name, address) =>
            Address.prismAddressToConsigneeAddress.getOption(address).map {
              case ConsigneeAddress(addressLine1, addressLine2, addressLine3, country) =>
                SafetyAndSecurityConsigneeWithoutEori(name, addressLine1, addressLine3, addressLine2, country.code.code)
            }

          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            Some(SafetyAndSecurityConsigneeWithEori(eori))
        }

    def safetyAndSecurityConsignor(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityConsignor] =
      securityTraderDetails
        .flatMap {
          case SafetyAndSecurity.PersonalInformation(name, address) =>
            Address.prismAddressToConsignorAddress.getOption(address).map {
              case ConsignorAddress(addressLine1, addressLine2, addressLine3, country) =>
                SafetyAndSecurityConsignorWithoutEori(name, addressLine1, addressLine3, addressLine2, country.code.code)
            }

          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            Some(SafetyAndSecurityConsignorWithEori(eori))
        }

    def carrier(securityTraderDetails: Option[SecurityTraderDetails]): Option[SafetyAndSecurityCarrier] =
      securityTraderDetails
        .flatMap {
          case SafetyAndSecurity.PersonalInformation(name, address) =>
            Address.prismAddressToCarrierAddress.getOption(address).map {
              case CarrierAddress(addressLine1, addressLine2, addressLine3, country) =>
                SafetyAndSecurityCarrierWithoutEori(name, addressLine1, addressLine3, addressLine2, country.code.code)
            }

          case SafetyAndSecurity.TraderEori(EoriNumber(eori)) =>
            Some(SafetyAndSecurityCarrierWithEori(eori))
        }

    def itineraries(itineraries: NonEmptyList[Itinerary]): Seq[models.messages.Itinerary] =
      itineraries.toList.map(countryCode => models.messages.Itinerary(countryCode.countryCode.code))

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
        agrLocOfGooHEA39    = agreedLocationOfGoods(movementDetails, goodsSummary.goodSummaryDetails), // prelodge
        autLocOfGooCodHEA41 = None,
        plaOfLoaCodHEA46    = None,
        couOfDisCodHEA55    = Some(routeDetails.countryOfDispatch.code),
        cusSubPlaHEA66      = customsSubPlace(goodsSummary),
        transportDetails = Transport(
          inlTraModHEA75        = Some(transportDetails.inlandMode.code),
          traModAtBorHEA76      = detailsAtBorderMode(transportDetails.detailsAtBorder),
          ideOfMeaOfTraAtDHEA78 = identityOfTransportAtDeparture(transportDetails.inlandMode),
          natOfMeaOfTraAtDHEA80 = nationalityAtDeparture(transportDetails.inlandMode),
          ideOfMeaOfTraCroHEA85 = None,
          natOfMeaOfTraCroHEA87 = None,
          typOfMeaOfTraCroHEA88 = None
        ),
        conIndHEA96        = booleanToInt(movementDetails.containersUsed),
        totNumOfIteHEA305  = itemDetails.size,
        totNumOfPacHEA306  = goodsSummary.numberOfPackages,
        totGroMasHEA307    = goodsSummary.totalMass,
        decDatHEA383       = dateTimeOfPrep.toLocalDate,
        decPlaHEA394       = movementDetails.declarationPlacePage,
        speCirIndHEA1      = safetyAndSecurity.flatMap(_.circumstanceIndicator),
        traChaMetOfPayHEA1 = safetyAndSecurity.flatMap(_.paymentMethod),
        comRefNumHEA       = safetyAndSecurity.flatMap(_.commercialReferenceNumber),
        secHEA358          = Some(safetyAndSecurityFlag(preTaskList.addSecurityDetails)),
        conRefNumHEA       = safetyAndSecurity.flatMap(_.conveyanceReferenceNumber),
        codPlUnHEA357      = safetyAndSecurity.flatMap(_.placeOfUnloading)
      ),
      principalTrader(traderDetails),
      headerConsignor(traderDetails),
      headerConsignee(traderDetails),
      None,
      CustomsOfficeDeparture(
        referenceNumber = routeDetails.officeOfDeparture.id
      ),
      customsOfficeTransit(routeDetails.transitInformation),
      CustomsOfficeDestination(
        referenceNumber = routeDetails.destinationOffice.id
      ),
      None,
      representative(movementDetails),
      headerSeals(goodsSummary.sealNumbers),
      guaranteeDetails(guarantee),
      goodsItems(journeyDomain.itemDetails, guarantee),
      safetyAndSecurity.map(sas => itineraries(sas.itineraryList)).getOrElse(Seq.empty),
      safetyAndSecurity.flatMap(sas => carrier(sas.carrier)),
      safetyAndSecurity.flatMap(sas => safetyAndSecurityConsignor(sas.consignor)),
      safetyAndSecurity.flatMap(sas => safetyAndSecurityConsignee(sas.consignee))
    )
  }
}
