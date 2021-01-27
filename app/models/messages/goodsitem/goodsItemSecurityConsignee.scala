package models.messages.goodsitem

import models.LanguageCodeEnglish
import xml.XMLWrites

trait goodsItemSecurityConsignee

object goodsItemSecurityConsignee

final case class ItemsSecurityConsigneeWithEori(eori: String) extends goodsItemSecurityConsignee
//format off

object ItemsSecurityConsigneeWithEori {

  implicit def writes: XMLWrites[ItemsSecurityConsigneeWithEori] = XMLWrites[ItemsSecurityConsigneeWithEori] {
    consignee =>
      <TRACONSECGOO013>
        <TINTRACONSECGOO020>{consignee.eori}</TINTRACONSECGOO020>
      </TRACONSECGOO013>
  }
}
final case class ItemsSecurityConsigneeWithoutEori(
                                                    name: String,
                                                    streetAndNumber: String,
                                                    postCode: String,
                                                    city: String,
                                                    countryCode: String
                                                  )  extends goodsItemSecurityConsignee

object ItemsSecurityConsigneeWithoutEori {

  implicit def writes: XMLWrites[ItemsSecurityConsigneeWithoutEori] = XMLWrites[ItemsSecurityConsigneeWithoutEori] {
    consignee =>
      <TRACONSECGOO013>
        <NamTRACONSECGOO017>{consignee.name}</NamTRACONSECGOO017>
        <StrNumTRACONSECGOO019>{consignee.streetAndNumber}</StrNumTRACONSECGOO019>
        <PosCodTRACONSECGOO018>{consignee.postCode}</PosCodTRACONSECGOO018>
        <CityTRACONSECGOO014>{consignee.city}</CityTRACONSECGOO014>
        <CouCodTRACONSECGOO015>{consignee.countryCode}</CouCodTRACONSECGOO015>
        <TRACONSECGOO013LNG>{LanguageCodeEnglish.code}</TRACONSECGOO013LNG>
      </TRACONSECGOO013>
  }

}



//format on
