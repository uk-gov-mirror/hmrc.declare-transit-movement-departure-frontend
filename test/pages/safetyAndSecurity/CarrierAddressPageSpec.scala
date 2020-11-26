package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class CarrierAddressPageSpec extends PageBehaviours {

  "CarrierAddressPage" - {

    beRetrievable[String](CarrierAddressPage)

    beSettable[String](CarrierAddressPage)

    beRemovable[String](CarrierAddressPage)
  }
}
