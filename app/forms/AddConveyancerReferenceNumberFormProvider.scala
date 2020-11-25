package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class AddConveyancerReferenceNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("addConveyancerReferenceNumber.error.required")
    )
}
