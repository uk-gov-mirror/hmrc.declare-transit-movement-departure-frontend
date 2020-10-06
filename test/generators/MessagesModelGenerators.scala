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
import models.messages.trader._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.{alphaNumChar, choose}
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

  implicit lazy val arbitraryDeclarationRequest: Arbitrary[DeclarationRequest] = {
    Arbitrary {
      for {
        meta   <- arbitrary[Meta]
        header <- arbitrary[Header]
        //TODO: This needs more xml nodes adding as models become available
      } yield DeclarationRequest(meta, header)
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
        eori            <- stringsWithMaxLength(Trader.Constants.eoriLength)
        name            <- Gen.option(stringsWithMaxLength(Trader.Constants.nameLength))
        streetAndNumber <- Gen.option(stringsWithMaxLength(Trader.Constants.streetAndNumberLength))
        postCode        <- Gen.option(stringsWithMaxLength(Trader.Constants.postCodeLength))
        city            <- Gen.option(stringsWithMaxLength(Trader.Constants.cityLength))
        countryCode     <- Gen.option(Gen.pick(2, 'A' to 'Z'))
      } yield TraderPrincipalWithEori(eori, name, streetAndNumber, postCode, city, countryCode.map(_.mkString))
    }

  implicit lazy val arbitraryTraderPrincipalWithoutEori: Arbitrary[TraderPrincipalWithoutEori] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
      } yield TraderPrincipalWithoutEori(name, streetAndNumber, postCode, city, countryCode.mkString)
    }

  implicit lazy val arbitraryTraderConsignorWithEori: Arbitrary[TraderConsignorWithEori] =
    Arbitrary {
      for {
        eori            <- stringsWithMaxLength(Trader.Constants.eoriLength)
        name            <- Gen.option(stringsWithMaxLength(Trader.Constants.nameLength))
        streetAndNumber <- Gen.option(stringsWithMaxLength(Trader.Constants.streetAndNumberLength))
        postCode        <- Gen.option(stringsWithMaxLength(Trader.Constants.postCodeLength))
        city            <- Gen.option(stringsWithMaxLength(Trader.Constants.cityLength))
        countryCode     <- Gen.option(Gen.pick(2, 'A' to 'Z'))
      } yield TraderConsignorWithEori(eori, name, streetAndNumber, postCode, city, countryCode.map(_.mkString))
    }

  implicit lazy val arbitraryTraderConsignorWithoutEori: Arbitrary[TraderConsignorWithoutEori] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
      } yield TraderConsignorWithoutEori(name, streetAndNumber, postCode, city, countryCode.mkString)
    }

  implicit lazy val arbitraryTraderConsigneeWithEori: Arbitrary[TraderConsigneeWithEori] =
    Arbitrary {
      for {
        eori            <- stringsWithMaxLength(Trader.Constants.eoriLength)
        name            <- Gen.option(stringsWithMaxLength(Trader.Constants.nameLength))
        streetAndNumber <- Gen.option(stringsWithMaxLength(Trader.Constants.streetAndNumberLength))
        postCode        <- Gen.option(stringsWithMaxLength(Trader.Constants.postCodeLength))
        city            <- Gen.option(stringsWithMaxLength(Trader.Constants.cityLength))
        countryCode     <- Gen.option(Gen.pick(2, 'A' to 'Z'))
      } yield TraderConsigneeWithEori(eori, name, streetAndNumber, postCode, city, countryCode.map(_.mkString))
    }

  implicit lazy val arbitraryTraderConsigneeWithoutEori: Arbitrary[TraderConsigneeWithoutEori] =
    Arbitrary {
      for {
        name            <- stringsWithMaxLength(Trader.Constants.nameLength)
        streetAndNumber <- stringsWithMaxLength(Trader.Constants.streetAndNumberLength)
        postCode        <- stringsWithMaxLength(Trader.Constants.postCodeLength)
        city            <- stringsWithMaxLength(Trader.Constants.cityLength)
        countryCode     <- Gen.pick(2, 'A' to 'Z')
      } yield TraderConsigneeWithoutEori(name, streetAndNumber, postCode, city, countryCode.mkString)
    }
}
