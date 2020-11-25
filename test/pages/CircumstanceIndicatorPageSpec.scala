package pages

import pages.behaviours.PageBehaviours


class CircumstanceIndicatorPageSpec extends PageBehaviours {

  "CircumstanceIndicatorPage" - {

    beRetrievable[String](CircumstanceIndicatorPage)

    beSettable[String](CircumstanceIndicatorPage)

    beRemovable[String](CircumstanceIndicatorPage)
  }
}
