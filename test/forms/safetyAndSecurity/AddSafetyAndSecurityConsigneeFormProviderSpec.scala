package forms.safetyAndSecurity

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddSafetyAndSecurityConsigneeFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addSafetyAndSecurityConsignee.error.required"
  val invalidKey = "error.boolean"

  val form = new AddSafetyAndSecurityConsigneeFormProvider()()

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
