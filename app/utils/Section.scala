package utils

import play.api.i18n.Messages
import play.api.libs.functional.syntax._
import play.api.libs.json.{__, OWrites}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text

case class Section(sectionTitle: Option[Text], rows: Seq[Row])

object Section {
  def apply(sectionTitle: Text, rows: Seq[Row]): Section = new Section(Some(sectionTitle), rows)

  def apply(rows: Seq[Row]): Section = new Section(None, rows)

  implicit def sectionWrites(implicit messages: Messages): OWrites[Section] =
    (
      (__ \ "sectionTitle").write[Option[Text]] and
        (__ \ "rows").write[Seq[Row]]
      )(unlift(Section.unapply))

}
