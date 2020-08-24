package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class PrincipalAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new PrincipalAddressFormProvider()()

  ".Number and street" - {

    val fieldName = "Number and street"
    val requiredKey = "principalAddress.error.numberAndStreet"
    val lengthKey = "principalAddress.error.numberAndStreet.length"
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
    val requiredKey = "principalAddress.town.required"
    val lengthKey = "principalAddress.error.town.length"
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

  ".Postcode" - {

    val fieldName = "Town"
    val requiredKey = "principalAddress.postcode.required"
    val lengthKey = "principalAddress.error.postcode.length"
    val maxLength = 9

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
