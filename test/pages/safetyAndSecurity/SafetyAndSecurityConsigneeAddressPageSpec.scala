package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class SafetyAndSecurityConsigneeAddressPageSpec extends PageBehaviours {

  "SafetyAndSecurityConsigneeAddressPage" - {

    beRetrievable[String](SafetyAndSecurityConsigneeAddressPage)

    beSettable[String](SafetyAndSecurityConsigneeAddressPage)

    beRemovable[String](SafetyAndSecurityConsigneeAddressPage)
  }
}
