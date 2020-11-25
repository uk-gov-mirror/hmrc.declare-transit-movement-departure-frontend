package pages

import pages.behaviours.PageBehaviours

class AddConveyancerReferenceNumberPageSpec extends PageBehaviours {

  "AddConveyancerReferenceNumberPage" - {

    beRetrievable[Boolean](AddConveyancerReferenceNumberPage)

    beSettable[Boolean](AddConveyancerReferenceNumberPage)

    beRemovable[Boolean](AddConveyancerReferenceNumberPage)
  }
}
