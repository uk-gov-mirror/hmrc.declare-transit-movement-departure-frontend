package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours


class CarrierNamePageSpec extends PageBehaviours {

  "CarrierNamePage" - {

    beRetrievable[String](CarrierNamePage)

    beSettable[String](CarrierNamePage)

    beRemovable[String](CarrierNamePage)
  }
}
