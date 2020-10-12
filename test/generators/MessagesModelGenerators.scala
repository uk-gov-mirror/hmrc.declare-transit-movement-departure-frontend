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

package generators

import java.time.{LocalDate, LocalTime}

import models.LocalReferenceNumber
import models.messages._
import models.messages.customsoffice.{CustomsOffice, CustomsOfficeDeparture, CustomsOfficeDestination, CustomsOfficeTransit}
import models.messages.goodsitem.GoodsItem
import models.messages.guarantee.{Guarantee, GuaranteeReference, GuaranteeReferenceWithGrn, GuaranteeReferenceWithOther}
import models.messages.trader._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{alphaNumChar, choose}
import org.scalacheck.{Arbitrary, Gen}
import utils.Format.dateFormatted

trait MessagesModelGenerators extends Generators {

  implicit lazy val arbitraryInterchangeControlReference: Arbitrary[InterchangeControlReference] = {
    Arbitrary {
      for {
        date  <- localDateGen
        index <- Gen.posNum[Int]
      } yield InterchangeControlReference(dateFormatted(date), index)
    }
  }

  implicit lazy val arbitraryMeta: Arbitrary[Meta] = {
    Arbitrary {
      for {
        interchangeControlReference <- arbitrary[InterchangeControlReference]
        date                        <- arbitrary[LocalDate]
        time                        <- arbitrary[LocalTime]
      } yield
        Meta(
          interchangeControlReference,
          date,
          LocalTime.of(time.getHour, time.getMinute),
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
    }
  }

  implicit lazy val arbitraryCustomsOfficeTransit: Arbitrary[CustomsOfficeTransit] =
    Arbitrary {
      for {
        customsOffice   <- Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')
        arrivalDateTime <- Gen.option(arbitrary(arbitraryLocalDateTime))
      } yield CustomsOfficeTransit(customsOffice.mkString, arrivalDateTime)
    }

  implicit lazy val arbitraryDeclarationRequest: Arbitrary[DeclarationRequest] = {
    Arbitrary {
      for {
        meta                      <- arbitrary[Meta]
        header                    <- arbitrary[Header]
        traderPrinciple           <- Gen.oneOf(arbitrary[TraderPrincipalWithEori], arbitrary[TraderPrincipalWithoutEori])
        traderConsignor           <- Gen.option(arbitrary[TraderConsignor])
        traderConsignee           <- Gen.option(arbitrary[TraderConsignee])
        traderAuthorisedConsignee <- arbitrary[TraderAuthorisedConsignee]
        customsOfficeDeparture    <- Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')
        customsOfficeTransit      <- listWithMaxLength[CustomsOfficeTransit](CustomsOffice.Constants.transitOfficeCount)
        customsOfficeDestination  <- Gen.pick(CustomsOffice.Constants.length, 'A' to 'Z')
        controlResult             <- Gen.option(arbitrary[ControlResult])
        representative            <- Gen.option(arbitrary[Representative])
        seals                     <- Gen.option(arbitrary[Seals])
        guarantee                 <- arbitrary[Guarantee]
        goodsItems                <- nonEmptyListWithMaxSize(2, arbitrary[GoodsItem])
        //TODO: This needs more xml nodes adding as models become available
      } yield
        DeclarationRequest(
          meta,
          header,
          traderPrinciple,
          traderConsignor,
          traderConsignee,
          traderAuthorisedConsignee,
          CustomsOfficeDeparture(customsOfficeDeparture.mkString),
          customsOfficeTransit,
          CustomsOfficeDestination(customsOfficeDestination.mkString),
          controlResult,
          representative,
          seals,
          guarantee,
          goodsItems
        )
    }
  }

  implicit lazy val arbitraryHeader: Arbitrary[Header] = {
    Arbitrary {
      for {
        refNumHEA4            <- arbitrary[LocalReferenceNumber].map(_.toString())
        typOfDecHEA24         <- Gen.pick(Header.Constants.typeOfDeclarationLength, 'A' to 'Z')
        couOfDesCodHEA30      <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        agrLocOfGooCodHEA38   <- Gen.option(stringsWithMaxLength(Header.Constants.agreedLocationOfGoodsCodeLength, alphaNumChar))
        agrLocOfGooHEA39      <- Gen.option(stringsWithMaxLength(Header.Constants.agreedLocationOfGoodsLength, alphaNumChar))
        autLocOfGooCodHEA41   <- Gen.option(stringsWithMaxLength(Header.Constants.authorisedLocationOfGoodsCodeLength, alphaNumChar))
        plaOfLoaCodHEA46      <- Gen.option(stringsWithMaxLength(Header.Constants.placeOfLoadingGoodsCodeLength, alphaNumChar))
        couOfDisCodHEA55      <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        cusSubPlaHEA66        <- Gen.option(stringsWithMaxLength(Header.Constants.customsSubPlaceLength, alphaNumChar))
        inlTraModHEA75        <- Gen.option(choose(min = 1: Int, 99: Int))
        traModAtBorHEA76      <- Gen.option(choose(min = 1: Int, 99: Int))
        ideOfMeaOfTraAtDHEA78 <- Gen.option(stringsWithMaxLength(Header.Constants.identityMeansOfTransport, alphaNumChar))
        natOfMeaOfTraAtDHEA80 <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        ideOfMeaOfTraCroHEA85 <- Gen.option(stringsWithMaxLength(Header.Constants.identityMeansOfTransport, alphaNumChar))
        natOfMeaOfTraCroHEA87 <- Gen.option(stringsWithMaxLength(Header.Constants.countryLength, alphaNumChar))
        typOfMeaOfTraCroHEA88 <- Gen.option(choose(min = 1: Int, 99: Int))
        conIndHEA96           <- choose(min = 0: Int, 1: Int)
        totNumOfIteHEA305     <- choose(min = 1: Int, 100: Int)
        totNumOfPacHEA306     <- Gen.option(choose(min = 1: Int, 100: Int))
        grossMass             <- Gen.choose(0.0, 99999999.999).map(BigDecimal(_).bigDecimal.setScale(3, BigDecimal.RoundingMode.DOWN))
        decDatHEA383          <- arbitrary[LocalDate]
        decPlaHEA394          <- stringsWithMaxLength(Header.Constants.declarationPlace, alphaNumChar)
      } yield
        Header(
          refNumHEA4,
          typOfDecHEA24.mkString,
          couOfDesCodHEA30.map(_.mkString),
          agrLocOfGooCodHEA38,
          agrLocOfGooHEA39,
          autLocOfGooCodHEA41,
          plaOfLoaCodHEA46,
          couOfDisCodHEA55.map(_.mkString),
          cusSubPlaHEA66,
          inlTraModHEA75,
          traModAtBorHEA76,
          ideOfMeaOfTraAtDHEA78,
          natOfMeaOfTraAtDHEA80.map(_.mkString),
          ideOfMeaOfTraCroHEA85,
          natOfMeaOfTraCroHEA87.map(_.mkString),
          typOfMeaOfTraCroHEA88,
          conIndHEA96,
          totNumOfIteHEA305,
          totNumOfPacHEA306,
          grossMass.toString,
          decDatHEA383,
          decPlaHEA394
        )
    }
  }

  implicit lazy val arbitraryTraderPrincipalWithEori: Arbitrary[TraderPrincipalWithEori] =
    Arbitrary {
      for {
        eori            <- stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar)
        name            <- Gen.option(stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar))
        streetAndNumber <- Gen.option(stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar))
        postCode        <- Gen.option(stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar))
        city            <- Gen.option(stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar))
        countryCode     <- Gen.option(Gen.pick(2, 'A' to 'Z'))
      } yield TraderPrincipalWithEori(eori, name, streetAndNumber, postCode, city, countryCode.map(_.mkString))
    }

  implicit lazy val arbitraryTraderPrincipalWithoutEori: Arbitrary[TraderPrincipalWithoutEori] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
      } yield TraderPrincipalWithoutEori(name, streetAndNumber, postCode, city, countryCode.mkString)
    }

  implicit lazy val arbitraryTraderConsignor: Arbitrary[TraderConsignor] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
        eori            <- Gen.option(stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar))
      } yield TraderConsignor(name, streetAndNumber, postCode, city, countryCode.mkString, eori)
    }

  implicit lazy val arbitraryTraderConsignee: Arbitrary[TraderConsignee] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength, alphaNumChar)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength, alphaNumChar)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength, alphaNumChar)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength, alphaNumChar)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
        eori            <- Gen.option(stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar))
      } yield TraderConsignee(name, streetAndNumber, postCode, city, countryCode.mkString, eori)
    }

  implicit lazy val arbitraryAuthorisedConsigneeTrader: Arbitrary[TraderAuthorisedConsignee] =
    Arbitrary {
      for {
        eori <- stringsWithMaxLength(Trader.Constants.eoriLength, alphaNumChar)
      } yield TraderAuthorisedConsignee(eori)
    }

  implicit lazy val arbitraryRepresentative: Arbitrary[Representative] =
    Arbitrary {
      for {
        name     <- stringsWithMaxLength(Representative.Constants.nameLength, alphaNumChar)
        capacity <- Gen.option(stringsWithMaxLength(Representative.Constants.capacityLength, alphaNumChar))
      } yield Representative(name, capacity)
    }

  implicit lazy val arbitraryControlResult: Arbitrary[ControlResult] =
    Arbitrary {
      for {
        controlResultCode <- Gen.pick(2, 'A' to 'Z')
        dateLimit         <- localDateGen
      } yield ControlResult(controlResultCode.mkString, dateLimit)
    }

  implicit lazy val arbitrarySeals: Arbitrary[Seals] =
    Arbitrary {
      for {
        numberOfSeals <- choose(min = 1: Int, 10: Int)
        sealId        <- listWithMaxLength(numberOfSeals, stringsWithMaxLength(Seals.Constants.sealIdLength, alphaNumChar))
      } yield Seals(numberOfSeals, sealId)
    }

  implicit lazy val arbitraryGuaranteeReferenceWithGrn: Arbitrary[GuaranteeReferenceWithGrn] =
    Arbitrary {
      for {
        guaranteeReferenceNumber <- stringsWithMaxLength(GuaranteeReferenceWithGrn.Constants.guaranteeReferenceNumberLength, alphaNumChar)
        accessCode               <- stringsWithMaxLength(GuaranteeReference.Constants.accessCodeLength, alphaNumChar)
      } yield GuaranteeReferenceWithGrn(guaranteeReferenceNumber, accessCode)
    }

  implicit lazy val arbitraryGuaranteeReferenceWithOther: Arbitrary[GuaranteeReferenceWithOther] =
    Arbitrary {
      for {
        guaranteeReferenceNumber <- stringsWithMaxLength(GuaranteeReferenceWithOther.Constants.otherReferenceNumberLength, alphaNumChar)
        accessCode               <- Gen.option(stringsWithMaxLength(GuaranteeReference.Constants.accessCodeLength, alphaNumChar))
      } yield GuaranteeReferenceWithOther(guaranteeReferenceNumber, accessCode)
    }

  implicit lazy val arbitraryGuaranteeReference: Arbitrary[GuaranteeReference] =
    Arbitrary {
      Gen.oneOf[GuaranteeReference](arbitrary[GuaranteeReferenceWithGrn], arbitrary[GuaranteeReferenceWithOther])
    }

  implicit lazy val arbitraryGuarantee: Arbitrary[Guarantee] =
    Arbitrary {
      for {
        guaranteeType     <- stringsWithMaxLength(GuaranteeReferenceWithOther.Constants.otherReferenceNumberLength, alphaNumChar)
        guarnteeReference <- listWithMaxLength(Guarantee.Constants.guaranteeReferenceCount, arbitrary[GuaranteeReference])
      } yield Guarantee(guaranteeType, guarnteeReference)
    }

  implicit lazy val arbitraryGoodsItem: Arbitrary[GoodsItem] =
    Arbitrary {
      for {
        itemNumber           <- Gen.choose(1, 99999)
        commodityCode        <- Gen.option(stringsWithMaxLength(GoodsItem.Constants.commodityCodeLength, alphaNumChar))
        declarationType      <- Gen.option(Gen.pick(GoodsItem.Constants.typeOfDeclarationLength, 'A' to 'Z'))
        description          <- stringsWithMaxLength(GoodsItem.Constants.descriptionLength, alphaNumChar)
        grossMass            <- Gen.option(Gen.choose(0.0, 99999999.999).map(BigDecimal(_)))
        netMass              <- Gen.option(Gen.choose(0.0, 99999999.999).map(BigDecimal(_)))
        countryOfDispatch    <- Gen.option(stringsWithMaxLength(GoodsItem.Constants.countryLength, alphaNumChar))
        countryOfDestination <- Gen.option(stringsWithMaxLength(GoodsItem.Constants.countryLength, alphaNumChar))
      } yield
        GoodsItem(
          itemNumber,
          commodityCode,
          declarationType.map(_.mkString),
          description,
          grossMass,
          netMass,
          countryOfDispatch,
          countryOfDestination
        )
    }

}
