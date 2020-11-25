package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddPlaceOfUnloadingCodeFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addPlaceOfUnloadingCode.error.required"
  val invalidKey = "error.boolean"

  val form = new AddPlaceOfUnloadingCodeFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
