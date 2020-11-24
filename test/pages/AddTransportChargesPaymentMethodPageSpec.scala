package pages

import pages.behaviours.PageBehaviours

class AddTransportChargesPaymentMethodPageSpec extends PageBehaviours {

  "AddTransportChargesPaymentMethodPage" - {

    beRetrievable[Boolean](AddTransportChargesPaymentMethodPage)

    beSettable[Boolean](AddTransportChargesPaymentMethodPage)

    beRemovable[Boolean](AddTransportChargesPaymentMethodPage)
  }
}
