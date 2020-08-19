package pages

import pages.behaviours.PageBehaviours


class PrincipalNamePageSpec extends PageBehaviours {

  "PrincipalNamePage" - {

    beRetrievable[String](PrincipalNamePage)

    beSettable[String](PrincipalNamePage)

    beRemovable[String](PrincipalNamePage)
  }
}
