package forms

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

class TotalPackagesFormProviderSpec extends IntFieldBehaviours {

  val form = new TotalPackagesFormProvider()()

  ".value" - {

    val fieldName = "value"

    val minimum = 0
    val maximum = 99999

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, "totalPackages.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "totalPackages.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, "totalPackages.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "totalPackages.error.required")
    )
  }
}
