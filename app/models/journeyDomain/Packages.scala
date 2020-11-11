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

import cats.implicits._
import models.Index
import models.reference.PackageType
import pages.PackageTypePage
import pages.addItems.{DeclareMarkPage, HowManyPackagesPage}

sealed trait Packages

object Packages {

  final case class UnpackedPackaged(
    packageType: PackageType,
    // declareNumberOfPackage: Boolean,
    howManyPackagesPage: Option[Int],
    totalPieces: Int,
    // addMark: Boolean,
    markOrNumber: Option[String]
  ) extends Packages

  final case class BulkPackage(
    packageType: PackageType,
    // declareNumberOfPackage: Boolean,
    howManyPackagesPage: Option[Int],
    // addMark: Boolean,
    markOrNumber: Option[String]
  ) extends Packages

  object BulkPackage {

    def bulkPackageReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[BulkPackage] =
      (
        PackageTypePage(itemIndex, referenceIndex).reader,
        HowManyPackagesPage(itemIndex, referenceIndex).optionalReader,
        DeclareMarkPage(itemIndex, referenceIndex).optionalReader
      ).tupled.map((BulkPackage.apply _).tupled)
  }

  final case class OtherPackage(packageType: PackageType, howManyPackagesPage: Int, markOrNumber: String) extends Packages

  object OtherPackage {

    def otherPackageReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[OtherPackage] =
      (
        PackageTypePage(itemIndex, referenceIndex).reader,
        HowManyPackagesPage(itemIndex, referenceIndex).reader,
        DeclareMarkPage(itemIndex, referenceIndex).reader
      ).tupled.map((OtherPackage.apply _).tupled)

  }
}
