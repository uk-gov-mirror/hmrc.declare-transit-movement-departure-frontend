package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class SafetyAndSecurityConsignorEoriFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("safetyAndSecurityConsignorEori.error.required")
        .verifying(maxLength(10, "safetyAndSecurityConsignorEori.error.length"))
    )
}
