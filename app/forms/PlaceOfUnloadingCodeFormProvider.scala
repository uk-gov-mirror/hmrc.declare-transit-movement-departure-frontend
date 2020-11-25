package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class PlaceOfUnloadingCodeFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("placeOfUnloadingCode.error.required")
        .verifying(maxLength(10, "placeOfUnloadingCode.error.length"))
    )
}
