package pages

import pages.behaviours.PageBehaviours


class TransportChargesPaymentMethodPageSpec extends PageBehaviours {

  "TransportChargesPaymentMethodPage" - {

    beRetrievable[String](TransportChargesPaymentMethodPage)

    beSettable[String](TransportChargesPaymentMethodPage)

    beRemovable[String](TransportChargesPaymentMethodPage)
  }
}
