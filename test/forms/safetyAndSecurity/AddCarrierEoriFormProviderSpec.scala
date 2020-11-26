package forms.safetyAndSecurity

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddCarrierEoriFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addCarrierEori.error.required"
  val invalidKey = "error.boolean"

  val form = new AddCarrierEoriFormProvider()()

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
