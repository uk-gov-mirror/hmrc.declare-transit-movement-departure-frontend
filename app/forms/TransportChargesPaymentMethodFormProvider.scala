package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class TransportChargesPaymentMethodFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("transportChargesPaymentMethod.error.required")
        .verifying(maxLength(2, "transportChargesPaymentMethod.error.length"))
    )
}
