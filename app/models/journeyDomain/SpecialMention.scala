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

import cats.implicits._
import models.Index
import pages.addItems.specialMentions.{SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}

sealed trait SpecialMention

object SpecialMention {

  def parseSpecialMention(index: Index, referenceIndex: Index): UserAnswersReader[SpecialMention] =
    specialMentionWithType(index, referenceIndex).widen[SpecialMention] orElse
      specialMentionWithCAL(index, referenceIndex).widen[SpecialMention]

  final case class SpecialMentionWithCAL(additionalInfo: String) extends SpecialMention

  private def specialMentionWithCAL(index: Index, referenceIndex: Index): UserAnswersReader[SpecialMentionWithCAL] =
    SpecialMentionAdditionalInfoPage(index, referenceIndex).reader.map(SpecialMentionWithCAL.apply)

  final case class SpecialMentionWithType(specialMention: String, additionalInfo: String) extends SpecialMention

  private def specialMentionWithType(index: Index, referenceIndex: Index): UserAnswersReader[SpecialMentionWithType] =
    (
      SpecialMentionTypePage(index, referenceIndex).reader,
      SpecialMentionAdditionalInfoPage(index, referenceIndex).reader,
    ).tupled.map((SpecialMentionWithType.apply _).tupled)
}
