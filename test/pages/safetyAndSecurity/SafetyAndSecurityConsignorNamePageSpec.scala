package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class SafetyAndSecurityConsignorNamePageSpec extends PageBehaviours {

  "SafetyAndSecurityConsignorNamePage" - {

    beRetrievable[String](SafetyAndSecurityConsignorNamePage)

    beSettable[String](SafetyAndSecurityConsignorNamePage)

    beRemovable[String](SafetyAndSecurityConsignorNamePage)
  }
}
