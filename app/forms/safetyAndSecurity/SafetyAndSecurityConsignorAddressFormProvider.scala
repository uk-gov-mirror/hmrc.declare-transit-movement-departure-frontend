package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class SafetyAndSecurityConsignorAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("safetyAndSecurityConsignorAddress.error.required")
        .verifying(maxLength(10, "safetyAndSecurityConsignorAddress.error.length"))
    )
}
