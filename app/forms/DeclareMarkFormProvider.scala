package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class DeclareMarkFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("declareMark.error.required")
        .verifying(maxLength(42, "declareMark.error.length"))
    )
}
