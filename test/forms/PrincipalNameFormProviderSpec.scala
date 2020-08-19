package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class PrincipalNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "principalName.error.required"
  val lengthKey = "principalName.error.length"
  val maxLength = 35

  val form = new PrincipalNameFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
