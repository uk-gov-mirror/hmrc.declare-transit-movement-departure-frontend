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

package models

import cats.data.ReaderT
import play.api.libs.json.Reads
import queries.Gettable

package object journeyDomain {

  type UserAnswersReader[A] = ReaderT[Option, UserAnswers, A]

  object UserAnswersReader {
    def apply[A: UserAnswersReader]: UserAnswersReader[A] = implicitly[UserAnswersReader[A]]
  }

  trait GettableAsReader[A] {
    def reader(a: Gettable[A]): UserAnswersReader[A]
  }

  implicit class GettableAsReaderOps[A](a: Gettable[A]) {

    def read(implicit rds: Reads[A]): UserAnswersReader[A] =
      ReaderT[Option, UserAnswers, A](_.get(a))
  }

}
