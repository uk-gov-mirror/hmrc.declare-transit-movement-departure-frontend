package pages

import pages.behaviours.PageBehaviours


class DeclareMarkPageSpec extends PageBehaviours {

  "DeclareMarkPage" - {

    beRetrievable[String](DeclareMarkPage)

    beSettable[String](DeclareMarkPage)

    beRemovable[String](DeclareMarkPage)
  }
}
