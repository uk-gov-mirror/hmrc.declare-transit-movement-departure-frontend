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
import models.messages.{DeclarationRequest, Header, InterchangeControlReference, Meta}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.choose
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
        typOfDecHEA24         <- Gen.pick(Header.typeOfDeclarationLength, 'A' to 'Z')
        couOfDesCodHEA30      <- Gen.option(stringsWithMaxLength(Header.countryLength))
        agrLocOfGooCodHEA38   <- Gen.option(stringsWithMaxLength(Header.agreedLocationOfGoodsCodeLength))
        agrLocOfGooHEA39      <- Gen.option(stringsWithMaxLength(Header.agreedLocationOfGoodsLength))
        autLocOfGooCodHEA41   <- Gen.option(stringsWithMaxLength(Header.authorisedLocationOfGoodsCodeLength))
        plaOfLoaCodHEA46      <- Gen.option(stringsWithMaxLength(Header.placeOfLoadingGoodsCodeLength))
        couOfDisCodHEA55      <- Gen.option(stringsWithMaxLength(Header.countryLength))
        cusSubPlaHEA66        <- Gen.option(stringsWithMaxLength(Header.customsSubPlaceLength))
        inlTraModHEA75        <- Gen.option(choose(min = 1: Int, 99: Int))
        traModAtBorHEA76      <- Gen.option(choose(min = 1: Int, 99: Int))
        ideOfMeaOfTraAtDHEA78 <- Gen.option(stringsWithMaxLength(Header.identityMeansOfTransport))
        natOfMeaOfTraAtDHEA80 <- Gen.option(stringsWithMaxLength(Header.countryLength))
        ideOfMeaOfTraCroHEA85 <- Gen.option(stringsWithMaxLength(Header.identityMeansOfTransport))
        natOfMeaOfTraCroHEA87 <- Gen.option(stringsWithMaxLength(Header.countryLength))
        typOfMeaOfTraCroHEA88 <- Gen.option(choose(min = 1: Int, 99: Int))
        conIndHEA96           <- choose(min = 0: Int, 1: Int)
        totNumOfIteHEA305     <- choose(min = 1: Int, 100: Int)
        totNumOfPacHEA306     <- Gen.option(choose(min = 1: Int, 100: Int))
        grossMass             <- Gen.choose(0.0, 99999999.999).map(BigDecimal(_).bigDecimal.setScale(3, BigDecimal.RoundingMode.DOWN))
        decDatHEA383          <- arbitrary[LocalDate]
        decPlaHEA394          <- stringsWithMaxLength(Header.declarationPlace)
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
}
