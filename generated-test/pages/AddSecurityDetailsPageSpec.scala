package pages

import pages.behaviours.PageBehaviours

class AddSecurityDetailsPageSpec extends PageBehaviours {

  "AddSecurityDetailsPage" - {

    beRetrievable[Boolean](AddSecurityDetailsPage)

    beSettable[Boolean](AddSecurityDetailsPage)

    beRemovable[Boolean](AddSecurityDetailsPage)
  }
}
