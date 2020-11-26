package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class CarrierEoriPageSpec extends PageBehaviours {

  "CarrierEoriPage" - {

    beRetrievable[String](CarrierEoriPage)

    beSettable[String](CarrierEoriPage)

    beRemovable[String](CarrierEoriPage)
  }
}
