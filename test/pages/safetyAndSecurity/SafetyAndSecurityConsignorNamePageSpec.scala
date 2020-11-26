package pages

import pages.behaviours.PageBehaviours
import pages.safetyAndSecurity.SafetyAndSecurityConsignorNamePage


class SafetyAndSecurityConsignorNamePageSpec extends PageBehaviours {

  "SafetyAndSecurityConsignorNamePage" - {

    beRetrievable[String](SafetyAndSecurityConsignorNamePage)

    beSettable[String](SafetyAndSecurityConsignorNamePage)

    beRemovable[String](SafetyAndSecurityConsignorNamePage)
  }
}
