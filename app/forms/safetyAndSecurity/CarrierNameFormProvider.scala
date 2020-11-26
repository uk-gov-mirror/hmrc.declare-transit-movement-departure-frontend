package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class CarrierNameFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("carrierName.error.required")
        .verifying(maxLength(10, "carrierName.error.length"))
    )
}
