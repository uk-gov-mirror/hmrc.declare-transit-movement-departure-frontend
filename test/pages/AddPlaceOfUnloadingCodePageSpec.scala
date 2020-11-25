package pages

import pages.behaviours.PageBehaviours

class AddPlaceOfUnloadingCodePageSpec extends PageBehaviours {

  "AddPlaceOfUnloadingCodePage" - {

    beRetrievable[Boolean](AddPlaceOfUnloadingCodePage)

    beSettable[Boolean](AddPlaceOfUnloadingCodePage)

    beRemovable[Boolean](AddPlaceOfUnloadingCodePage)
  }
}
