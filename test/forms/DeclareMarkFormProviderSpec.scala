package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class DeclareMarkFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "declareMark.error.required"
  val lengthKey = "declareMark.error.length"
  val maxLength = 42

  val form = new DeclareMarkFormProvider()()

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
