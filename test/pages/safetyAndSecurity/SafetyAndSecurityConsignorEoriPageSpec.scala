package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class SafetyAndSecurityConsignorEoriPageSpec extends PageBehaviours {

  "SafetyAndSecurityConsignorEoriPage" - {

    beRetrievable[String](SafetyAndSecurityConsignorEoriPage)

    beSettable[String](SafetyAndSecurityConsignorEoriPage)

    beRemovable[String](SafetyAndSecurityConsignorEoriPage)
  }
}
