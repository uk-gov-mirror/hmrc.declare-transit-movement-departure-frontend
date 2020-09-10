package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class AddAnotherTransitOfficeFormProviderSpec extends StringFieldBehaviours {

  val form = new AddAnotherTransitOfficeFormProvider()()

  ".Which transit office do you want to add?" - {

    val fieldName = "Which transit office do you want to add?"
    val requiredKey = "addAnotherTransitOffice.error.required"
    val lengthKey = "addAnotherTransitOffice.error.Which transit office do you want to add?.length"
    val maxLength = 100

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

  ".field2" - {

    val fieldName = "field2"
    val requiredKey = "addAnotherTransitOffice.error.field2.required"
    val lengthKey = "addAnotherTransitOffice.error.field2.length"
    val maxLength = 100

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
