package forms.safetyAndSecurity

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddSafetyAndSecurityConsigneeEoriFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addSafetyAndSecurityConsigneeEori.error.required"
  val invalidKey = "error.boolean"

  val form = new AddSafetyAndSecurityConsigneeEoriFormProvider()()

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
