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

package base

import models.UserAnswers
import org.scalatest.TestSuite
import pages.QuestionPage
import play.api.libs.json.Writes

trait UserAnswersSpecHelper {

  implicit class UserAnswersNoErrorSet(userAnswers: UserAnswers) {

    def unsafeSetVal[A: Writes](page: QuestionPage[A])(value: A): UserAnswers =
      userAnswers.set(page, value).getOrElse(throw new Exception(s"`set` on UserAnswers failed in test for userAnswers: $userAnswers"))

    def unsafeSetOpt[A: Writes](page: QuestionPage[A])(value: Option[A]): UserAnswers =
      value.fold(userAnswers)(unsafeSetVal(page))

    def unsafeSetSeq[A: Writes](pageFn: Int => QuestionPage[A])(value: Seq[A]): UserAnswers =
      value.zipWithIndex
        .map {
          case (value, index) => (pageFn(index), value)
        }
        .foldLeft(userAnswers) {
          case (ua, (page, value)) =>
            unsafeSetVal(page)(value)
        }

    def unsafeSetFun[A, B: Writes](page: QuestionPage[B])(value: A)(pf: A => B): UserAnswers =
      unsafeSetVal(page)(pf(value))

  }

}
