package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class SafetyAndSecurityConsigneeEoriFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("safetyAndSecurityConsigneeEori.error.required")
        .verifying(maxLength(10, "safetyAndSecurityConsigneeEori.error.length"))
    )
}
