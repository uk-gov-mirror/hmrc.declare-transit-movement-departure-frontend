package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class CommercialReferenceNumberAllItemsFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("commercialReferenceNumberAllItems.error.required")
        .verifying(maxLength(10, "commercialReferenceNumberAllItems.error.length"))
    )
}
