package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class ExtraInformationFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("extraInformation.error.required")
        .verifying(maxLength(20, "extraInformation.error.length"))
    )
}
