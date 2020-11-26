package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class SafetyAndSecurityConsignorAddressPageSpec extends PageBehaviours {

  "SafetyAndSecurityConsignorAddressPage" - {

    beRetrievable[String](SafetyAndSecurityConsignorAddressPage)

    beSettable[String](SafetyAndSecurityConsignorAddressPage)

    beRemovable[String](SafetyAndSecurityConsignorAddressPage)
  }
}
