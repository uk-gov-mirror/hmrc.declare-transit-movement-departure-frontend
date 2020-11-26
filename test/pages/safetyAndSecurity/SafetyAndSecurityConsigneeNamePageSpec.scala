package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class SafetyAndSecurityConsigneeNamePageSpec extends PageBehaviours {

  "SafetyAndSecurityConsigneeNamePage" - {

    beRetrievable[String](SafetyAndSecurityConsigneeNamePage)

    beSettable[String](SafetyAndSecurityConsigneeNamePage)

    beRemovable[String](SafetyAndSecurityConsigneeNamePage)
  }
}
