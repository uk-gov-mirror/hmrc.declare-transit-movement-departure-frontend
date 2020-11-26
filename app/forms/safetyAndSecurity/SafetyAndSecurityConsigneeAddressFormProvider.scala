package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class SafetyAndSecurityConsigneeAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("safetyAndSecurityConsigneeAddress.error.required")
        .verifying(maxLength(10, "safetyAndSecurityConsigneeAddress.error.length"))
    )
}
