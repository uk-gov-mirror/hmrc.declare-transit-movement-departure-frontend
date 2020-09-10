package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.AddAnotherTransitOffice

class AddAnotherTransitOfficeFormProvider @Inject() extends Mappings {

   def apply(): Form[AddAnotherTransitOffice] = Form(
     mapping(
      "Which transit office do you want to add?" -> text("addAnotherTransitOffice.error.Which transit office do you want to add?.required")
        .verifying(maxLength(100, "addAnotherTransitOffice.error.Which transit office do you want to add?.length")),
      "field2" -> text("addAnotherTransitOffice.error.field2.required")
        .verifying(maxLength(100, "addAnotherTransitOffice.error.field2.length"))
    )(AddAnotherTransitOffice.apply)(AddAnotherTransitOffice.unapply)
   )
 }
