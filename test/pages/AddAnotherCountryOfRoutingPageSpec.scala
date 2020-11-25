package pages

import pages.behaviours.PageBehaviours

class AddAnotherCountryOfRoutingPageSpec extends PageBehaviours {

  "AddAnotherCountryOfRoutingPage" - {

    beRetrievable[Boolean](AddAnotherCountryOfRoutingPage)

    beSettable[Boolean](AddAnotherCountryOfRoutingPage)

    beRemovable[Boolean](AddAnotherCountryOfRoutingPage)
  }
}
