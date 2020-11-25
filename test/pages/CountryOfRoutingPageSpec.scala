package pages

import pages.behaviours.PageBehaviours


class CountryOfRoutingPageSpec extends PageBehaviours {

  "CountryOfRoutingPage" - {

    beRetrievable[String](CountryOfRoutingPage)

    beSettable[String](CountryOfRoutingPage)

    beRemovable[String](CountryOfRoutingPage)
  }
}
