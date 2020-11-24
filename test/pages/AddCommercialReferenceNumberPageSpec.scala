package pages

import pages.behaviours.PageBehaviours

class AddCommercialReferenceNumberPageSpec extends PageBehaviours {

  "AddCommercialReferenceNumberPage" - {

    beRetrievable[Boolean](AddCommercialReferenceNumberPage)

    beSettable[Boolean](AddCommercialReferenceNumberPage)

    beRemovable[Boolean](AddCommercialReferenceNumberPage)
  }
}
