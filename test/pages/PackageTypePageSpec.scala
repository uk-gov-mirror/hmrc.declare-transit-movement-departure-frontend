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

package pages

import base.SpecBase
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.addItems._
import pages.behaviours.PageBehaviours

class PackageTypePageSpec extends PageBehaviours with SpecBase {

  "PackageTypePage" - {

    beRetrievable[String](PackageTypePage(index, index))

    beSettable[String](PackageTypePage(index, index))

    beRemovable[String](PackageTypePage(index, index))

    "cleanup" - {

      "must cleanup pages when there is a change of answer" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result =
              userAnswers
                .set(DeclareNumberOfPackagesPage(index, index), true)
                .success
                .value
                .set(HowManyPackagesPage(index, index), 123)
                .success
                .value
                .set(TotalPiecesPage(index, index), 123)
                .success
                .value
                .set(AddMarkPage(index, index), true)
                .success
                .value
                .set(DeclareMarkPage(index, index), "mark")
                .success
                .value
                .set(PackageTypePage(index, index), "AB")
                .success
                .value

            result.get(DeclareNumberOfPackagesPage(index, index)) must not be defined
            result.get(HowManyPackagesPage(index, index)) must not be defined
            result.get(TotalPiecesPage(index, index)) must not be defined
            result.get(AddMarkPage(index, index)) must not be defined
            result.get(DeclareMarkPage(index, index)) must not be defined
        }
      }
    }
  }

}
