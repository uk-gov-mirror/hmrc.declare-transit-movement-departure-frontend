package pages

import pages.behaviours.PageBehaviours


class ExtraInformationPageSpec extends PageBehaviours {

  "ExtraInformationPage" - {

    beRetrievable[String](ExtraInformationPage)

    beSettable[String](ExtraInformationPage)

    beRemovable[String](ExtraInformationPage)
  }
}
