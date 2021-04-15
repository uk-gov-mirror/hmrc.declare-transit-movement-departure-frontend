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

package controllers.actions

import derivable.DeriveNumberOfOfficeOfTransits
import models.{Index, NormalMode}
import models.requests.DataRequest
import pages.{AddAnotherTransitOfficePage, AddTransitOfficePage, OfficeOfTransitCountryPage}
import play.api.mvc.{ActionFilter, Result}
import play.api.mvc.Results._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TraderDetailsOfficesOfTransitProvider @Inject()(ec: ExecutionContext) {

  def apply(index: Index): ActionFilter[DataRequest] = new TraderDetailsOfficesOfTransitFilter(index, ec)

}

class TraderDetailsOfficesOfTransitFilter(index: Index, ec: ExecutionContext) extends ActionFilter[DataRequest] {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] =
    if (index.position <= 8) {
      val numberOfOffices = request.userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)
      if (numberOfOffices > 0) {
        request.userAnswers.get(AddAnotherTransitOfficePage(Index(numberOfOffices - 1))) match {
          case Some(_) =>
            Future.successful(None)
          case None =>
            Future.successful(Option(Redirect(
              controllers.routeDetails.routes.OfficeOfTransitCountryController.onPageLoad(request.userAnswers.id, Index(numberOfOffices - 1), NormalMode).url)))
        }
      } else {
        Future.successful(None)
      }
    } else {
      Future.successful(Option(Redirect(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(request.userAnswers.id, NormalMode).url)))
    }

  override protected def executionContext: ExecutionContext = ec

}
