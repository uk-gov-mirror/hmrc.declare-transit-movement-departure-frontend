package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CircumstanceIndicatorFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "circumstanceIndicator.error.required"
  val lengthKey = "circumstanceIndicator.error.length"
  val maxLength = 2

  val form = new CircumstanceIndicatorFormProvider()()

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
