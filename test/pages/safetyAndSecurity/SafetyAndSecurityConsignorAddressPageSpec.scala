package pages

import pages.behaviours.PageBehaviours
import pages.safetyAndSecurity.SafetyAndSecurityConsignorAddressPage


class SafetyAndSecurityConsignorAddressPageSpec extends PageBehaviours {

  "SafetyAndSecurityConsignorAddressPage" - {

    beRetrievable[String](SafetyAndSecurityConsignorAddressPage)

    beSettable[String](SafetyAndSecurityConsignorAddressPage)

    beRemovable[String](SafetyAndSecurityConsignorAddressPage)
  }
}
