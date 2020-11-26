package forms.safetyAndSecurity

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class SafetyAndSecurityConsignorEoriFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "safetyAndSecurityConsignorEori.error.required"
  val lengthKey = "safetyAndSecurityConsignorEori.error.length"
  val maxLength = 10

  val form = new SafetyAndSecurityConsignorEoriFormProvider()()

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
