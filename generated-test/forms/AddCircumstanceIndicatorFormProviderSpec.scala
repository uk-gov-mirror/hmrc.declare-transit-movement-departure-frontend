package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddCircumstanceIndicatorFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addCircumstanceIndicator.error.required"
  val invalidKey = "error.boolean"

  val form = new AddCircumstanceIndicatorFormProvider()()

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
