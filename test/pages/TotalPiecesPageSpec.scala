package pages

import pages.behaviours.PageBehaviours

class TotalPiecesPageSpec extends PageBehaviours {

  "TotalPiecesPage" - {

    beRetrievable[Int](TotalPiecesPage)

    beSettable[Int](TotalPiecesPage)

    beRemovable[Int](TotalPiecesPage)
  }
}
