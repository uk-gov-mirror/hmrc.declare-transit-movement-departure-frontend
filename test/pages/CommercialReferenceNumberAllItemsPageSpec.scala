package pages

import pages.behaviours.PageBehaviours


class CommercialReferenceNumberAllItemsPageSpec extends PageBehaviours {

  "CommercialReferenceNumberAllItemsPage" - {

    beRetrievable[String](CommercialReferenceNumberAllItemsPage)

    beSettable[String](CommercialReferenceNumberAllItemsPage)

    beRemovable[String](CommercialReferenceNumberAllItemsPage)
  }
}
