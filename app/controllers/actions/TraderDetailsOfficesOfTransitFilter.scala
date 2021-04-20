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
import models.requests.DataRequest
import models.{Index, NormalMode}
import pages.{AddAnotherTransitOfficePage, OfficeOfTransitCountryPage}
import play.api.mvc.Results._
import play.api.mvc.{ActionFilter, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TraderDetailsOfficesOfTransitProvider @Inject()()(implicit ec: ExecutionContext) {

  def apply(index: Index, pageId: Int): ActionFilter[DataRequest] = new TraderDetailsOfficesOfTransitFilter(index, pageId)

}

class TraderDetailsOfficesOfTransitFilter(index: Index, pageId: Int)(implicit protected val executionContext: ExecutionContext)
    extends ActionFilter[DataRequest] {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    val numberOfOffices = request.userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)
    Future(
      if (index.position == 0) {
        None
      } else {
        if (numberOfOffices > 0) {
          request.userAnswers.get(AddAnotherTransitOfficePage(Index(numberOfOffices - 1))) match {
            case Some(_) =>
              (index.position == numberOfOffices, request.userAnswers.get(OfficeOfTransitCountryPage(Index(numberOfOffices - 1))), pageId == 0) match {
                case (true, None, true) =>
                  Option(Redirect(controllers.routeDetails.routes.OfficeOfTransitCountryController.onPageLoad(request.userAnswers.id, index, NormalMode).url))
                case (false, Some(_), true) =>
                  Option(Redirect(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(request.userAnswers.id, NormalMode).url))
                case (_) => None
              }
            case None =>
              if (index.position == numberOfOffices - 1) {
                None
              } else {
                Option(
                  Redirect(
                    controllers.routeDetails.routes.OfficeOfTransitCountryController
                      .onPageLoad(request.userAnswers.id, Index(numberOfOffices - 1), NormalMode)
                      .url))
              }
          }
        } else {
          Option(Redirect(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(request.userAnswers.id, NormalMode).url))
        }
      }
    )
  }

}
