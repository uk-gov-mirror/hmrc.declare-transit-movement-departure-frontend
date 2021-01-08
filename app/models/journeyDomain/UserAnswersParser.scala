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

import cats._
import cats.data._
import cats.implicits._
import models.UserAnswers

trait UserAnswersParser[F[_], +A] {
  def run[AA >: A](ua: UserAnswers): F[AA]
}

object UserAnswersParser {

  def apply[F[_], A](implicit parser: UserAnswersParser[F, A]): UserAnswersParser[F, A] = parser

  implicit def monoid[A]: Monoid[UserAnswersParser[Option, A]] =
    Monoid.instance(
      UserAnswersOptionalParser.empty[A],
      (l, r) =>
        new UserAnswersOptionalParser[A] {

          override def run[AA >: A](ua: UserAnswers): Option[AA] =
            l.run(ua) orElse r.run(ua)
      }
    )
}

abstract class UserAnswersOptionalParser[A] extends UserAnswersParser[Option, A]

object UserAnswersOptionalParser {

  def apply[A](implicit parser: UserAnswersParser[Option, A]): UserAnswersParser[Option, A] = parser

  def apply[A, B](reader: ReaderT[Option, UserAnswers, A])(f: A => B): UserAnswersOptionalParser[B] =
    new UserAnswersOptionalParser[B] {

      override def run[AA >: B](ua: UserAnswers): Option[AA] =
        reader.map(f).run(ua).widen[AA]
    }

  def empty[A]: UserAnswersOptionalParser[A] =
    new UserAnswersOptionalParser[A] {
      override def run[AA >: A](ua: UserAnswers): Option[AA] = none[A]
    }
}
