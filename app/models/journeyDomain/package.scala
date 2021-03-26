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

package models

import cats._
import cats.data._
import cats.implicits._
import play.api.libs.json.Reads
import queries.Gettable

package object journeyDomain {

  type UserAnswersReader[A] = ReaderT[Option, UserAnswers, A]

  object UserAnswersReader {
    def apply[A: UserAnswersReader]: UserAnswersReader[A] = implicitly[UserAnswersReader[A]]

    def apply[A](fn: UserAnswers => Option[A]): UserAnswersReader[A] = ReaderT[Option, UserAnswers, A](fn)

    def empty[A: UserAnswersReader]: UserAnswersReader[A] = unsafeEmpty[A]

    def unsafeEmpty[A]: UserAnswersReader[A] =
      UserAnswersReader[A](
        (_: UserAnswers) => Option.empty[A]
      )
  }

  implicit class GettableAsFilterForNextReaderOps[A: Reads](a: Gettable[A]) {

    /**
      * Returns the UserAnswersReader[Option[B]], where UserAnswersReader[B] which is run only if UserAnswerReader[A]
      * is defined and true, if it defined and false it will return None. If the result of UserAnswerReader[A]
      * is not defined then UserAnswersReader[B] will never run `next`
      */
    def readerWithDependentOptionalReaders[B](predicate: A => Boolean)(next: UserAnswersReader[B]): UserAnswersReader[Option[B]] =
      a.reader
        .flatMap {
          x =>
            if (predicate(x)) {
              next.wrapOption
            } else {
              none[B].pure[UserAnswersReader]
            }
        }
  }

  implicit class GettableAsOptionalReaderOps[A](a: Gettable[A]) {

    def optionalReader(implicit reads: Reads[A]): UserAnswersReader[Option[A]] =
      a.reader.lower
  }

  implicit class GettableAsReaderOps[A](a: Gettable[A]) {

    def reader(implicit reads: Reads[A]): UserAnswersReader[A] =
      ReaderT[Option, UserAnswers, A](_.get(a))
  }

  implicit class ReaderLiftOptionOps[A](reader: UserAnswersReader[A]) {
    def wrapOption: UserAnswersReader[Option[A]] = reader.map(Option(_))
  }

}
