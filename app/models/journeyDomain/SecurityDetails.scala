package models.journeyDomain

import models.Index
import models.reference.{DangerousGoodsCode, MethodOfPayment}

final case class SecurityDetails(
                                  methodOfPayment: Option[MethodOfPayment],
                                  commercialReferenceNumber: Option[String],
                                  dangerousGoodsCode: Option[DangerousGoodsCode]
                                )


object SecurityDetails {

  private def methodOfPaymentPage(index: Index): UserAnswersReader[Option[MethodOfPayment]] = ???

  private def commercialReferenceNumberPage(index: Index): UserAnswersReader[Option[String]] = ???

  private def dangerousGoodsCodePage(index: Index): UserAnswersReader[Option[DangerousGoodsCode]] = ???

  def securityDetailsReader(index: Index): UserAnswersReader[SecurityDetails] = ???
}