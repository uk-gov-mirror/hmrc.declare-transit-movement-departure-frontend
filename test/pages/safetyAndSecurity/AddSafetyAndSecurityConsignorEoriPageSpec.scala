package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours

class AddSafetyAndSecurityConsignorEoriPageSpec extends PageBehaviours {

  "AddSafetyAndSecurityConsignorEoriPage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsignorEoriPage)

    beSettable[Boolean](AddSafetyAndSecurityConsignorEoriPage)

    beRemovable[Boolean](AddSafetyAndSecurityConsignorEoriPage)
  }
}
