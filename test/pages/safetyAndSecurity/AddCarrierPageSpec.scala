package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours

class AddCarrierPageSpec extends PageBehaviours {

  "AddCarrierPage" - {

    beRetrievable[Boolean](AddCarrierPage)

    beSettable[Boolean](AddCarrierPage)

    beRemovable[Boolean](AddCarrierPage)
  }
}
