package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class AddSafetyAndSecurityConsignorEoriFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("addSafetyAndSecurityConsignorEori.error.required")
    )
}
