package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class SafetyAndSecurityConsigneeEoriPageSpec extends PageBehaviours {

  "SafetyAndSecurityConsigneeEoriPage" - {

    beRetrievable[String](SafetyAndSecurityConsigneeEoriPage)

    beSettable[String](SafetyAndSecurityConsigneeEoriPage)

    beRemovable[String](SafetyAndSecurityConsigneeEoriPage)
  }
}
