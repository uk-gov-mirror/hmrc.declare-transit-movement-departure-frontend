package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IsPrincipalEoriKnownFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("isPrincipalEoriKnown.error.required")
    )
}
