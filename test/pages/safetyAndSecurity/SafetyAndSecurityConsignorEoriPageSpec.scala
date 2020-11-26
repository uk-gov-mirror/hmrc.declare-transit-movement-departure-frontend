package pages

import pages.behaviours.PageBehaviours
import pages.safetyAndSecurity.SafetyAndSecurityConsignorEoriPage


class SafetyAndSecurityConsignorEoriPageSpec extends PageBehaviours {

  "SafetyAndSecurityConsignorEoriPage" - {

    beRetrievable[String](SafetyAndSecurityConsignorEoriPage)

    beSettable[String](SafetyAndSecurityConsignorEoriPage)

    beRemovable[String](SafetyAndSecurityConsignorEoriPage)
  }
}
