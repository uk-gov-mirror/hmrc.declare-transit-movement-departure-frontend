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

    // if the index is valid
    val numberOfOffices = request.userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)
    if (index.position == 0) {
      Future.successful(None)
    } else {

      if (numberOfOffices > 0) {
        request.userAnswers.get(AddAnotherTransitOfficePage(Index(numberOfOffices - 1))) match {
          case Some(_) =>
            Future.successful(
              if (index.position <= 8) {
                if (index.position == numberOfOffices) {
                  if (pageId == 0) {
                    None
                  } else {
                    request.userAnswers.get(OfficeOfTransitCountryPage(Index(numberOfOffices - 1))) match {
                      case Some(_) =>
                        None
                      case None =>
                        Option(
                          Redirect(controllers.routeDetails.routes.OfficeOfTransitCountryController.onPageLoad(request.userAnswers.id, index, NormalMode).url)
                        )
                    }
                  }
                } else {
                  Option(Redirect(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(request.userAnswers.id, NormalMode).url))
                }
              } else {
                Option(Redirect(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(request.userAnswers.id, NormalMode).url))
              }
            )
          case None =>
            println(s"\n\n\n ${index.position} \n\n\n $numberOfOffices \n\n\n")
            if (index.position == numberOfOffices - 1) {
              Future.successful(None)
            } else {
              Future.successful(
                Option(
                  Redirect(
                    controllers.routeDetails.routes.OfficeOfTransitCountryController
                      .onPageLoad(request.userAnswers.id, Index(numberOfOffices - 1), NormalMode)
                      .url))
              )
            }
        }
      } else {
        Future.successful(Option(Redirect(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(request.userAnswers.id, NormalMode).url)))
      }
    }
  }
}
