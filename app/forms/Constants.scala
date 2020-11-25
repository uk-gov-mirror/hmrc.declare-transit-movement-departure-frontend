/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

object Constants {
  lazy val addressRegex: String                 = "^[a-zA-Z0-9/@?%,.\\- ]*$"
  lazy val validPostcodeCharactersRegex: String = "^[a-zA-Z\\s*0-9]*$"
  lazy val postCodeRegex: String                = "^[a-zA-Z]{1,2}([0-9]{1,2}|[0-9][a-zA-Z])\\s*[0-9][a-zA-Z]{2}$"
  lazy val eoriNumberRegex: String              = "^[a-zA-Z]{2}[0-9a-zA-Z]{1,15}"
  lazy val maxLengthEoriNumber: Int             = 17
  lazy val validEoriCharactersRegex: String     = "^[a-zA-Z0-9]*$"
  lazy val vehicleIdRegex: String               = "^[a-zA-Z0-9 ]*$"
  lazy val vehicleIdMaxLength                   = 27
  lazy val consignorNameRegex                   = s"^[a-zA-Z0-9&'@\\/.\\-%?<> ]{1,35}"
  lazy val consigneeNameMaxLength: Int          = 35

}
