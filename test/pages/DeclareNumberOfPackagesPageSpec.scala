package pages

import pages.behaviours.PageBehaviours

class DeclareNumberOfPackagesPageSpec extends PageBehaviours {

  "DeclareNumberOfPackagesPage" - {

    beRetrievable[Boolean](DeclareNumberOfPackagesPage)

    beSettable[Boolean](DeclareNumberOfPackagesPage)

    beRemovable[Boolean](DeclareNumberOfPackagesPage)
  }
}
