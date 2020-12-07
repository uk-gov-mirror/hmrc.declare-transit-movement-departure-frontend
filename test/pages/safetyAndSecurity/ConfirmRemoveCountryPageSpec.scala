package pages.safetyAndSecurity

import pages.behaviours.PageBehaviours

class ConfirmRemoveCountryPageSpec extends PageBehaviours {

  "ConfirmRemoveCountryPage" - {

    beRetrievable[Boolean](ConfirmRemoveCountryPage)

    beSettable[Boolean](ConfirmRemoveCountryPage)

    beRemovable[Boolean](ConfirmRemoveCountryPage)
  }
}
