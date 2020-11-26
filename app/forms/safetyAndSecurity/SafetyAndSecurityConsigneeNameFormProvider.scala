package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class SafetyAndSecurityConsigneeNameFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("safetyAndSecurityConsigneeName.error.required")
        .verifying(maxLength(10, "safetyAndSecurityConsigneeName.error.length"))
    )
}
