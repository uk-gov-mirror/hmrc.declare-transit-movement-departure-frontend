package pages

import pages.behaviours.PageBehaviours

class ConsignorForAllItemsPageSpec extends PageBehaviours {

  "ConsignorForAllItemsPage" - {

    beRetrievable[Boolean](ConsignorForAllItemsPage)

    beSettable[Boolean](ConsignorForAllItemsPage)

    beRemovable[Boolean](ConsignorForAllItemsPage)
  }
}
