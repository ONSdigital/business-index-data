package uk.gov.ons.bi.ingest.parsers

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
object ImplicitHelpers {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  private[this] val dtfm = DateTimeFormat.forPattern("dd/MM/yy")

  val Date = new DateTime()

  import StringHelpers._

  implicit class AsOption[T](x: T) {
    def ? = Option(x)
  }

  implicit class TypedString(s: String) {

    def asDateTime: DateTime = parseDate(s)

    def asIntOpt: Option[Int] = strOption(s).map(_.toDouble.toInt)

    def asDateTimeOpt: Some[DateTime] = {
      // FIXME: strOption(s).map(x => parseDate(x))
      Some(Date)
    }
  }

  def parseDate(s: String): DateTime = {
     dtfm.parseDateTime(s)
  }
}

object StringHelpers {

  def strOption(s: String): Option[String] = s match {
    case "" | "\"\"" => None
    case _ => Option(s)
  }

  def unquote(s: String): String = s.replaceAll("\"", "")

}

