package pages

import pages.behaviours.PageBehaviours

class RemoveItemPageSpec extends PageBehaviours {

  "RemoveItemPage" - {

    beRetrievable[Boolean](RemoveItemPage)

    beSettable[Boolean](RemoveItemPage)

    beRemovable[Boolean](RemoveItemPage)
  }
}
