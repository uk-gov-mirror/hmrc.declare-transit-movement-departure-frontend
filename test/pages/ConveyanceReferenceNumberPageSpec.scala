package pages

import pages.behaviours.PageBehaviours


class ConveyanceReferenceNumberPageSpec extends PageBehaviours {

  "ConveyanceReferenceNumberPage" - {

    beRetrievable[String](ConveyanceReferenceNumberPage)

    beSettable[String](ConveyanceReferenceNumberPage)

    beRemovable[String](ConveyanceReferenceNumberPage)
  }
}
