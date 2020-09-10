package pages

import pages.behaviours.PageBehaviours

class AddAnotherTransitOfficePageSpec extends PageBehaviours {

  "AddAnotherTransitOfficePage" - {

    beRetrievable[String](AddAnotherTransitOfficePage)

    beSettable[String](AddAnotherTransitOfficePage)

    beRemovable[String](AddAnotherTransitOfficePage)
  }
}
