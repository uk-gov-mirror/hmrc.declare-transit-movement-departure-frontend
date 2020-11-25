package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CountryOfRoutingFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "countryOfRouting.error.required"
  val lengthKey = "countryOfRouting.error.length"
  val maxLength = 2

  val form = new CountryOfRoutingFormProvider()()

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
