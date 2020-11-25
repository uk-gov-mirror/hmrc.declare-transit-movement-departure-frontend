package pages

import pages.behaviours.PageBehaviours

class AddCommercialReferenceNumberAllItemsPageSpec extends PageBehaviours {

  "AddCommercialReferenceNumberAllItemsPage" - {

    beRetrievable[Boolean](AddCommercialReferenceNumberAllItemsPage)

    beSettable[Boolean](AddCommercialReferenceNumberAllItemsPage)

    beRemovable[Boolean](AddCommercialReferenceNumberAllItemsPage)
  }
}
