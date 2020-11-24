package pages

import pages.behaviours.PageBehaviours

class AddCircumstanceIndicatorPageSpec extends PageBehaviours {

  "AddCircumstanceIndicatorPage" - {

    beRetrievable[Boolean](AddCircumstanceIndicatorPage)

    beSettable[Boolean](AddCircumstanceIndicatorPage)

    beRemovable[Boolean](AddCircumstanceIndicatorPage)
  }
}
