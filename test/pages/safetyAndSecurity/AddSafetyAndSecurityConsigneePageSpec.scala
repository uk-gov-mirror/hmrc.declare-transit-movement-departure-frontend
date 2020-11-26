package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours

class AddSafetyAndSecurityConsigneePageSpec extends PageBehaviours {

  "AddSafetyAndSecurityConsigneePage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsigneePage)

    beSettable[Boolean](AddSafetyAndSecurityConsigneePage)

    beRemovable[Boolean](AddSafetyAndSecurityConsigneePage)
  }
}
