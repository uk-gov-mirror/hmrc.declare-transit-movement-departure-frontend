package pages

import pages.behaviours.PageBehaviours

class TotalPackagesPageSpec extends PageBehaviours {

  "TotalPackagesPage" - {

    beRetrievable[Int](TotalPackagesPage)

    beSettable[Int](TotalPackagesPage)

    beRemovable[Int](TotalPackagesPage)
  }
}
