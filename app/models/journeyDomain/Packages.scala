package models.journeyDomain

import models.reference.PackageType

sealed trait Packages

object Packages {

  final case class UnpackedPackaged(
                                    packageType: PackageType,
                                    // declareNumberOfPackage: Boolean,
                                    howManyPackagesPage: Option[Int],
                                    totalPieces: Int,
                                    // addMark: Boolean,
                                    markOrNumber: Option[String]
                                   )

  final case class BulkPackage(
                                packageType: PackageType,
                                // declareNumberOfPackage: Boolean,
                                howManyPackagesPage: Option[Int],
                                // addMark: Boolean,
                                markOrNumber: Option[String]
                              )

  final case class OtherPackage(packageType: PackageType, howManyPackagesPage: Int, markOrNumber: String)

}
