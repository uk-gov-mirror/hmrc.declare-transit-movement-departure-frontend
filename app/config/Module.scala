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

package config

import com.google.inject.AbstractModule
import controllers.actions._
import navigation.{AbstractNavigator, MovementDetailsNavigator, Navigator}
import repositories.{DefaultSessionRepository, SessionRepository}
import utils.annotations.{MainNavigation, MovementDetails}

class Module extends AbstractModule {

  override def configure(): Unit = {

 /*   val navigators = Multibinder.newSetBinder(binder(), classOf[AbstractNavigator])
    navigators.addBinding().to(classOf[Navigator])
    navigators.addBinding().to(classOf[MovementDetailsNavigator])*/


    bind(classOf[AbstractNavigator])
      .annotatedWith(classOf[MainNavigation])
      .to(classOf[Navigator])

    bind(classOf[AbstractNavigator])
      .annotatedWith(classOf[MovementDetails])
      .to(classOf[MovementDetailsNavigator])

    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()

    // For session based storage instead of cred based, change to SessionIdentifierAction
    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction]).asEagerSingleton()

    bind(classOf[SessionRepository]).to(classOf[DefaultSessionRepository]).asEagerSingleton()
  }
}
