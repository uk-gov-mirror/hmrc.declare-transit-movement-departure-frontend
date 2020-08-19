package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class PrincipalNameFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("principalName.error.required")
        .verifying(maxLength(35, "principalName.error.length"))
    )
}
