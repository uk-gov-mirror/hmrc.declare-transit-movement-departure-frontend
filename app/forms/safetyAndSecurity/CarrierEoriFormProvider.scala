package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class CarrierEoriFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("carrierEori.error.required")
        .verifying(maxLength(10, "carrierEori.error.length"))
    )
}
