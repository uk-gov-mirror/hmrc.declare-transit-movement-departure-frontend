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
import models.journeyDomain.Packages.{BulkPackage, OtherPackage}
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.addItems.{DeclareMarkPage, HowManyPackagesPage}
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

    "OtherPackage" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[OtherPackage], arbitrary[UserAnswers]) {
          (otherPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index)(userAnswers)
            val result             = UserAnswersReader[OtherPackage](OtherPackage.otherPackageReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual otherPackage
        }
      }

      "can not be parsed when a mandatory answer is missing" in {

        forAll(arbitrary[OtherPackage], arbitrary[UserAnswers], mandatoryPagesOther) {
          case (otherPackage, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index)(userAnswers)

            val userAnswersIncomplete = updatedUserAnswers.remove(mandatoryPage).success.value
            val result                = UserAnswersReader[OtherPackage](OtherPackage.otherPackageReader(index, index)).run(userAnswersIncomplete)

            result mustEqual None
        }
      }

    }

    "BulkPackage" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[BulkPackage], arbitrary[UserAnswers]) {
          (bulkPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(bulkPackage, index)(userAnswers)
            val result             = UserAnswersReader[BulkPackage](BulkPackage.bulkPackageReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual bulkPackage
        }
      }

      "can not be parsed when a mandatory answer is missing" in {

        forAll(arbitrary[BulkPackage], arbitrary[UserAnswers], mandatoryPagesBulkPackages) {
          case (otherPackage, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index)(userAnswers)

            val userAnswersIncomplete = updatedUserAnswers.remove(mandatoryPage).success.value
            val result                = UserAnswersReader[BulkPackage](BulkPackage.bulkPackageReader(index, index)).run(userAnswersIncomplete)

            result mustEqual None
        }
      }

    }

  }

}

object PackagesSpec extends UserAnswersSpecHelper {

  def setPackageUserAnswers(otherPackage: Packages, index: Index)(userAnswers: UserAnswers): UserAnswers = otherPackage match {
    case otherPackage: OtherPackage => {
      userAnswers
        .unsafeSetVal(PackageTypePage(index, index))(otherPackage.packageType)
        .unsafeSetVal(HowManyPackagesPage(index, index))(otherPackage.howManyPackagesPage)
        .unsafeSetVal(DeclareMarkPage(index, index))(otherPackage.markOrNumber)
    }
    case bulkPackage: BulkPackage => {
      userAnswers
        .unsafeSetVal(PackageTypePage(index, index))(bulkPackage.packageType)
        .unsafeSetOpt(HowManyPackagesPage(index, index))(bulkPackage.howManyPackagesPage)
        .unsafeSetOpt(DeclareMarkPage(index, index))(bulkPackage.markOrNumber)
    }

  }

}
