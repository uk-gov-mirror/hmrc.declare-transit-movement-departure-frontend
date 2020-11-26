package pages

import pages.behaviours.PageBehaviours
import pages.safetyAndSecurity.AddSafetyAndSecurityConsignorEoriPage

class AddSafetyAndSecurityConsignorEoriPageSpec extends PageBehaviours {

  "AddSafetyAndSecurityConsignorEoriPage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsignorEoriPage)

    beSettable[Boolean](AddSafetyAndSecurityConsignorEoriPage)

    beRemovable[Boolean](AddSafetyAndSecurityConsignorEoriPage)
  }
}
