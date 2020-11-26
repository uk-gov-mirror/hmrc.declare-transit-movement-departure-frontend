package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class AddSafetyAndSecurityConsigneeEoriFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("addSafetyAndSecurityConsigneeEori.error.required")
    )
}
