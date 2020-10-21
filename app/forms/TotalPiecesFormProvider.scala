package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class TotalPiecesFormProvider @Inject() extends Mappings {

  def apply(): Form[Int] =
    Form(
      "value" -> int(
        "totalPieces.error.required",
        "totalPieces.error.wholeNumber",
        "totalPieces.error.nonNumeric")
          .verifying(inRange(1, 99999, "totalPieces.error.outOfRange"))
    )
}
