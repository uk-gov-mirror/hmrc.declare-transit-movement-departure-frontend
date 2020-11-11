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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.{JourneyModelGenerators, ModelGenerators}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.addItems.{AddMarkPage, DeclareMarkPage, DeclareNumberOfPackagesPage, HowManyPackagesPage, TotalPiecesPage}
import pages.{PackageTypePage, QuestionPage}

class PackagesSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with ModelGenerators {

  import PackagesSpec._

  val mandatoryPagesOther: Gen[QuestionPage[_]] = Gen.oneOf(
    PackageTypePage(index, index),
    HowManyPackagesPage(index, index),
    DeclareMarkPage(index, index)
  )

  val mandatoryPagesBulkPackages: Gen[QuestionPage[_]] = PackageTypePage(index, index)

  "PackagesSpec" - {

    "any Packages can be parsed from UserAnswer" in {
      forAll(arbitrary[Packages], arbitrary[UserAnswers]) {
        (packages, userAnswers) =>
          val updatedUserAnswers = setPackageUserAnswers(packages, index)(userAnswers)
          val result             = UserAnswersReader[Packages](Packages.packagesReader(index, index)).run(updatedUserAnswers)

          result.value mustEqual packages
      }
    }

    "OtherPackage" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[OtherPackages], arbitrary[UserAnswers]) {
          (otherPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index)(userAnswers)
            val result             = UserAnswersReader[OtherPackages](OtherPackages.otherPackageReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual otherPackage
        }
      }

      "can not be parsed when a mandatory answer is missing" in {

        forAll(arbitrary[OtherPackages], arbitrary[UserAnswers], mandatoryPagesOther) {
          case (otherPackage, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index)(userAnswers)

            val userAnswersIncomplete = updatedUserAnswers.remove(mandatoryPage).success.value
            val result                = UserAnswersReader[OtherPackages](OtherPackages.otherPackageReader(index, index)).run(userAnswersIncomplete)

            result mustEqual None
        }
      }

    }

    "BulkPackage" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[BulkPackages], arbitrary[UserAnswers]) {
          (bulkPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(bulkPackage, index)(userAnswers)
            val result             = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual bulkPackage
        }
      }

      "can not be parsed when a mandatory answer is missing" in {

        forAll(arbitrary[BulkPackages], arbitrary[UserAnswers], mandatoryPagesBulkPackages) {
          case (otherPackage, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index)(userAnswers)

            val userAnswersIncomplete = updatedUserAnswers.remove(mandatoryPage).success.value
            val result                = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(userAnswersIncomplete)

            result mustEqual None
        }
      }

    }

    "UnpackedPackages" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[UnpackedPackages], arbitrary[UserAnswers]) {
          (bulkPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(bulkPackage, index)(userAnswers)
            val result             = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual bulkPackage
        }
      }

      "can not be parsed when a mandatory answer is missing" in {

        forAll(arbitrary[UnpackedPackages], arbitrary[UserAnswers], mandatoryPagesBulkPackages) {
          case (otherPackage, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index)(userAnswers)

            val userAnswersIncomplete = updatedUserAnswers.remove(mandatoryPage).success.value
            val result                = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(userAnswersIncomplete)

            result mustEqual None
        }
      }

    }

  }

}

object PackagesSpec extends UserAnswersSpecHelper {

  def setPackageUserAnswers(packages: Packages, index: Index)(userAnswers: UserAnswers): UserAnswers =
    packages match {
      case otherPackage: OtherPackages =>
        userAnswers
          .unsafeSetVal(PackageTypePage(index, index))(otherPackage.packageType)
          .unsafeSetVal(HowManyPackagesPage(index, index))(otherPackage.howManyPackagesPage)
          .unsafeSetVal(DeclareMarkPage(index, index))(otherPackage.markOrNumber)
      case bulkPackage: BulkPackages =>
        userAnswers
          .unsafeSetVal(PackageTypePage(index, index))(bulkPackage.packageType)
          .unsafeSetVal(DeclareNumberOfPackagesPage(index, index))(bulkPackage.howManyPackagesPage.isDefined)
          .unsafeSetOpt(HowManyPackagesPage(index, index))(bulkPackage.howManyPackagesPage)
          .unsafeSetVal(AddMarkPage(index, index))(bulkPackage.markOrNumber.isDefined)
          .unsafeSetOpt(DeclareMarkPage(index, index))(bulkPackage.markOrNumber)
      case unpackedPackages: UnpackedPackages =>
        userAnswers
          .unsafeSetVal(PackageTypePage(index, index))(unpackedPackages.packageType)
          .unsafeSetVal(DeclareNumberOfPackagesPage(index, index))(unpackedPackages.howManyPackagesPage.isDefined)
          .unsafeSetOpt(HowManyPackagesPage(index, index))(unpackedPackages.howManyPackagesPage)
          .unsafeSetVal(TotalPiecesPage(index, index))(unpackedPackages.totalPieces)
          .unsafeSetVal(AddMarkPage(index, index))(unpackedPackages.markOrNumber.isDefined)
          .unsafeSetOpt(DeclareMarkPage(index, index))(unpackedPackages.markOrNumber)
    }

}
