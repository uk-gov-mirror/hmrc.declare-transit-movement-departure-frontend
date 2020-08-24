package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class PrincipalAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new PrincipalAddressFormProvider()()

  ".Number and street" - {

    val fieldName = "Number and street"
    val requiredKey = "principalAddress.error.Number and street.required"
    val lengthKey = "principalAddress.error.Number and street.length"
    val maxLength = 35

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

  ".Town" - {

    val fieldName = "Town"
    val requiredKey = "principalAddress.error.Town.required"
    val lengthKey = "principalAddress.error.Town.length"
    val maxLength = 35

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
