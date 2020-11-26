package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours

class AddSafetyAndSecurityConsigneeEoriPageSpec extends PageBehaviours {

  "AddSafetyAndSecurityConsigneeEoriPage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsigneeEoriPage)

    beSettable[Boolean](AddSafetyAndSecurityConsigneeEoriPage)

    beRemovable[Boolean](AddSafetyAndSecurityConsigneeEoriPage)
  }
}
