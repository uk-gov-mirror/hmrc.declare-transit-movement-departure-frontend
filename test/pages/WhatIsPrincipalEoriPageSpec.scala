package pages

import pages.behaviours.PageBehaviours


class WhatIsPrincipalEoriPageSpec extends PageBehaviours {

  "WhatIsPrincipalEoriPage" - {

    beRetrievable[String](WhatIsPrincipalEoriPage)

    beSettable[String](WhatIsPrincipalEoriPage)

    beRemovable[String](WhatIsPrincipalEoriPage)
  }
}
