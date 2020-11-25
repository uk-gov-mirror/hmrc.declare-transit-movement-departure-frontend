package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class CountryOfRoutingFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("countryOfRouting.error.required")
        .verifying(maxLength(2, "countryOfRouting.error.length"))
    )
}
