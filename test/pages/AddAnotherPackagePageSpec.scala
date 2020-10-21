package pages

import pages.behaviours.PageBehaviours

class AddAnotherPackagePageSpec extends PageBehaviours {

  "AddAnotherPackagePage" - {

    beRetrievable[Boolean](AddAnotherPackagePage)

    beSettable[Boolean](AddAnotherPackagePage)

    beRemovable[Boolean](AddAnotherPackagePage)
  }
}
