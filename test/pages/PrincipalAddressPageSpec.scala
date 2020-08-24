package pages

import models.PrincipalAddress
import pages.behaviours.PageBehaviours

class PrincipalAddressPageSpec extends PageBehaviours {

  "PrincipalAddressPage" - {

    beRetrievable[PrincipalAddress](PrincipalAddressPage)

    beSettable[PrincipalAddress](PrincipalAddressPage)

    beRemovable[PrincipalAddress](PrincipalAddressPage)
  }
}
