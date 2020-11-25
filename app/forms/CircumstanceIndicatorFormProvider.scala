package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class CircumstanceIndicatorFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("circumstanceIndicator.error.required")
        .verifying(maxLength(2, "circumstanceIndicator.error.length"))
    )
}
