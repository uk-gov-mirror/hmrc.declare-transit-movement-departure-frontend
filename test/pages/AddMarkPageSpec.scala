package pages

import pages.behaviours.PageBehaviours

class AddMarkPageSpec extends PageBehaviours {

  "AddMarkPage" - {

    beRetrievable[Boolean](AddMarkPage)

    beSettable[Boolean](AddMarkPage)

    beRemovable[Boolean](AddMarkPage)
  }
}
