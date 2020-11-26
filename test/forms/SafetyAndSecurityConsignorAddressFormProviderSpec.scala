package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class SafetyAndSecurityConsignorAddressFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "safetyAndSecurityConsignorAddress.error.required"
  val lengthKey = "safetyAndSecurityConsignorAddress.error.length"
  val maxLength = 10

  val form = new SafetyAndSecurityConsignorAddressFormProvider()()

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
