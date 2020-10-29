package pages

import pages.behaviours.PageBehaviours

class AddExtraInformationPageSpec extends PageBehaviours {

  "AddExtraInformationPage" - {

    beRetrievable[Boolean](AddExtraInformationPage)

    beSettable[Boolean](AddExtraInformationPage)

    beRemovable[Boolean](AddExtraInformationPage)
  }
}
