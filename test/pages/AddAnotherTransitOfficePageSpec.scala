package pages

import models.AddAnotherTransitOffice
import pages.behaviours.PageBehaviours

class AddAnotherTransitOfficePageSpec extends PageBehaviours {

  "AddAnotherTransitOfficePage" - {

    beRetrievable[AddAnotherTransitOffice](AddAnotherTransitOfficePage)

    beSettable[AddAnotherTransitOffice](AddAnotherTransitOfficePage)

    beRemovable[AddAnotherTransitOffice](AddAnotherTransitOfficePage)
  }
}
