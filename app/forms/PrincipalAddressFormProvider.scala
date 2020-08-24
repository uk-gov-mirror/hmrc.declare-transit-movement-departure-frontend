package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.PrincipalAddress

class PrincipalAddressFormProvider @Inject() extends Mappings {

   def apply(): Form[PrincipalAddress] = Form(
     mapping(
      "Number and street" -> text("principalAddress.error.Number and street.required")
        .verifying(maxLength(35, "principalAddress.error.Number and street.length")),
      "Town" -> text("principalAddress.error.Town.required")
        .verifying(maxLength(35, "principalAddress.error.Town.length"))
    )(PrincipalAddress.apply)(PrincipalAddress.unapply)
   )
 }
