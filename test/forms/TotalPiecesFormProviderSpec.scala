package forms

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

class TotalPiecesFormProviderSpec extends IntFieldBehaviours {

  val form = new TotalPiecesFormProvider()()

  ".value" - {

    val fieldName = "value"

    val minimum = 1
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
      nonNumericError  = FormError(fieldName, "totalPieces.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "totalPieces.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, "totalPieces.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "totalPieces.error.required")
    )
  }
}
