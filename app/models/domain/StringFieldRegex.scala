/*
 * Copyright 2021 HM Revenue & Customs
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

package models.domain

object StringFieldRegex {

  lazy val stringFieldRegex                     = "[\\sa-zA-Z0-9&'@/.\\-? ]*".r
  lazy val alphaNumericUpperCaseRegex           = "^[A-Z0-9]*$".r
  lazy val alphaNumericRegex                    = "^[a-zA-Z0-9]*$".r
  lazy val alphaNumericWithSpaceRegex           = "^[a-zA-Z0-9 ]*$".r
  lazy val commodityCodeCharactersRegex         = "^[0-9]*$"
  lazy val commodityCodeFormatRegex             = "^([0-9]{6}|[0-9]{8}|[0-9]{10})$"
  lazy val liabilityAmountCharactersRegex       = "^$|^[0-9.]*$"
  lazy val liabilityAmountFormatRegex           = "^$|([0-9]*(?:\\.[0-9]{1,2})?)$"
  lazy val greaterThanZeroRegex                 = "^$|([1-9]{1}[0-9.]*)$"
  lazy val validPostcodeCharactersRegex: String = "^[a-zA-Z\\s*0-9]*$"
  lazy val postCodeRegex: String                = "^[a-zA-Z]{1,2}([0-9]{1,2}|[0-9][a-zA-Z])\\s*[0-9][a-zA-Z]{2}$"
  lazy val eoriNumberRegex: String              = "^[a-zA-Z]{2}[0-9a-zA-Z]{1,15}"
  lazy val consignorNameRegex                   = s"^[a-zA-Z0-9&'@\\/.\\-%? ]{1,35}"
  lazy val addressRegex: String                 = "^[a-zA-Z0-9/@?%,.\\- ]*$"

}
