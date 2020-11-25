package pages

import pages.behaviours.PageBehaviours


class PlaceOfUnloadingCodePageSpec extends PageBehaviours {

  "PlaceOfUnloadingCodePage" - {

    beRetrievable[String](PlaceOfUnloadingCodePage)

    beSettable[String](PlaceOfUnloadingCodePage)

    beRemovable[String](PlaceOfUnloadingCodePage)
  }
}
