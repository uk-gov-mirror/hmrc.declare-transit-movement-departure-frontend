package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddTransportChargesPaymentMethodFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addTransportChargesPaymentMethod.error.required"
  val invalidKey = "error.boolean"

  val form = new AddTransportChargesPaymentMethodFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
