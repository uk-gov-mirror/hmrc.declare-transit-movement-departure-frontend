package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ConveyanceReferenceNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "conveyanceReferenceNumber.error.required"
  val lengthKey = "conveyanceReferenceNumber.error.length"
  val maxLength = 10

  val form = new ConveyanceReferenceNumberFormProvider()()

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
