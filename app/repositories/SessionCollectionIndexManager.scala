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

package repositories

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

@Singleton
private[repositories] class SessionCollectionIndexManagerImpl @Inject()(
  sessionCollection: SessionCollection,
  config: Configuration
)(implicit ec: ExecutionContext)
    extends SessionCollectionIndexManager {

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private val lastUpdatedIndex = SimpleMongoIndexConfig(
    key     = Seq("lastUpdated" -> IndexType.Ascending),
    name    = Some("user-answers-last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  val started: Future[Unit] =
    sessionCollection()
      .flatMap {
        _.indexesManager.ensure(lastUpdatedIndex)
      }
      .map(
        _ => ()
      )

}

private[repositories] trait SessionCollectionIndexManager {

  def started: Future[Unit]

}
