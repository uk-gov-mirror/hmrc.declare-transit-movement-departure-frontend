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

import cats.data._
import cats.implicits._
import play.api.libs.json.Reads
import queries.Gettable

package object journeyDomain {

  type EitherType[A]        = Either[String, A] //TODO make this a query
  type UserAnswersReader[A] = ReaderT[EitherType, UserAnswers, A]

  object UserAnswersReader {
    def apply[A: UserAnswersReader]: UserAnswersReader[A] = implicitly[UserAnswersReader[A]]

    def apply[A](fn: UserAnswers => EitherType[A]): UserAnswersReader[A] =
      ReaderT[EitherType, UserAnswers, A](fn)

    def failed[A]: UserAnswersReader[A] = ReaderT[EitherType, UserAnswers, A](
      _ => Left("woa") // TODO pass a helpful error
    )
  }

  implicit class GettableAsFilterForNextReaderOps[A: Reads](a: Gettable[A]) {

    /**
      * Returns UserAnswersReader[Option[B]], where UserAnswersReader[B] which is run only if UserAnswerReader[A]
      * is defined and satisfies the predicate, if it defined and does not satisfy the predicate overall reader will
      * will return None. If the result of UserAnswerReader[A] is not defined then the overall reader will fail and
      * `next` will not be run
      */
    def filterDependent[B](predicate: A => Boolean)(next: UserAnswersReader[B]): UserAnswersReader[Option[B]] =
      a.reader
        .flatMap {
          x =>
            if (predicate(x)) {
              next.map(Option(_))
            } else {
              ReaderT[EitherType, UserAnswers, Option[B]](
                _ => Left("Failed filter ops") // TODO pass a helpful error
              )
            }
        }
  }

  implicit class GettableAsOptionalReaderOps[A](a: Gettable[A]) {

    /**
      * Returns a reader for [[gettable]], which will succeed with Some[A] if the value is defined
      * and will succeed with a None if it is not defined
      */

    def optionalReader(implicit reads: Reads[A]): UserAnswersReader[Option[A]] =
      ReaderT[EitherType, UserAnswers, Option[A]](
        x => Right(x.get(a))
      )
  }

  implicit class GettableAsReaderOps[A](a: Gettable[A]) {

    /**
      * Returns a reader for [[gettable]], which will succeed with an [[A]]  if the value is defined
      * and will fail if it is not defined
      */

    def reader(implicit reads: Reads[A]): UserAnswersReader[A] =
      ReaderT[EitherType, UserAnswers, A](
        x =>
          x.get(a) match {
            case Some(value) => Right(value)
            case None        => Left(a.path.toString())
        }
      )
  }

}
