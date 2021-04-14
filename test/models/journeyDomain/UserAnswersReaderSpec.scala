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

package models.journeyDomain

import base.SpecBase
import models.UserAnswers
import play.api.libs.json._
import queries.Gettable

class UserAnswersReaderSpec extends SpecBase {
  final case class TestData(field1: Int, field2: String)

  implicit val jsonReads: Reads[TestData] = Json.reads[TestData]

  val passingGettable1: Gettable[Int] = new Gettable[Int] {
    override def path: JsPath = __ \ "passingGettable1Path"
  }

  val passingGettable2: Gettable[TestData] = new Gettable[TestData] {
    override def path: JsPath = __ \ "passingGettable2Path"
  }

  val failingGettable: Gettable[Int] = new Gettable[Int] {
    override def path: JsPath = __ \ "failingGettablePath"
  }

  val testData = UserAnswers(
    lrn,
    eoriNumber,
    Json.obj(
      "passingGettable1Path" -> 1,
      "passingGettable2Path" -> Json.obj(
        "field1" -> 1,
        "field2" -> "asdf"
      )
    )
  )

  "reader" - {
    "when a reader for a gettable is run" - {
      "passes and reads data when present" in {
        passingGettable1.reader.run(testData).right.value mustEqual 1
      }

      "fails when not present" in {
        failingGettable.reader.run(testData).isLeft mustBe true
      }

    }
  }

  "optionalReader" - {
    "when a reader for a gettable is run" - {
      "passes and reads data when present" in {
        passingGettable1.optionalReader.run(testData).right.value mustEqual Some(1)
      }

      "passes and return a None when not present" in {
        failingGettable.optionalReader.run(testData).isLeft mustBe true
      }

    }
  }

  "filterDependent" - {
    "when the first reader passes" - {
      "and the second reader has data that is defined, then the full reader passes" in {
        val testReaders = passingGettable1.filterDependent(_ == 1) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData).right.value

        result.value mustEqual TestData(1, "asdf")
      }

      "and the second reader has data that is missing, then the full reader fails" in {
        val testReaders = passingGettable1.filterDependent(_ == 0) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData).isLeft

        result mustBe true
      }
    }

    "when the first reader fails" - {
      "then the full reader fails" in {
        val testReaders = failingGettable.filterDependent(_ == 1) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData).isLeft

        result mustBe true
      }
    }
  }

}
