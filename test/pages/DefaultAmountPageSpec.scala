package pages

import pages.behaviours.PageBehaviours

class DefaultAmountPageSpec extends PageBehaviours {

  "DefaultAmountPage" - {

    beRetrievable[Boolean](DefaultAmountPage)

    beSettable[Boolean](DefaultAmountPage)

    beRemovable[Boolean](DefaultAmountPage)
  }
}
