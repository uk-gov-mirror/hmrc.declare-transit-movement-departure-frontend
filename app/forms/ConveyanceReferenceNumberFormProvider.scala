package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class ConveyanceReferenceNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("conveyanceReferenceNumber.error.required")
        .verifying(maxLength(10, "conveyanceReferenceNumber.error.length"))
    )
}
