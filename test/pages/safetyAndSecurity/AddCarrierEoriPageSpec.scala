package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours

class AddCarrierEoriPageSpec extends PageBehaviours {

  "AddCarrierEoriPage" - {

    beRetrievable[Boolean](AddCarrierEoriPage)

    beSettable[Boolean](AddCarrierEoriPage)

    beRemovable[Boolean](AddCarrierEoriPage)
  }
}
