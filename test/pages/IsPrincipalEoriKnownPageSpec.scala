package pages

import pages.behaviours.PageBehaviours

class IsPrincipalEoriKnownPageSpec extends PageBehaviours {

  "IsPrincipalEoriKnownPage" - {

    beRetrievable[Boolean](IsPrincipalEoriKnownPage)

    beSettable[Boolean](IsPrincipalEoriKnownPage)

    beRemovable[Boolean](IsPrincipalEoriKnownPage)
  }
}
