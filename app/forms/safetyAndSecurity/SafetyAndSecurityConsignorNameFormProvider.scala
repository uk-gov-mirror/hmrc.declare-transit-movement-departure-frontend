package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class SafetyAndSecurityConsignorNameFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("safetyAndSecurityConsignorName.error.required")
        .verifying(maxLength(10, "safetyAndSecurityConsignorName.error.length"))
    )
}
