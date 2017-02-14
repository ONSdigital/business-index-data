package uk.gov.ons.bi.ingest.parsers

import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
object ImplicitHelpers {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  import StringHelpers._

  implicit class AsOption[T](x: T) {
    def ? = Option(x)
  }

  implicit class TypedString(s: String) {
    def asDateTime = parseDate(s)

    def asIntOpt = strOption(s).map(_.toInt)

    def asDateTimeOpt = strOption(s).map(x => parseDate(x))
  }

  def parseDate(s: String) = {
    val dtfm = DateTimeFormat.forPattern("dd/MM/yy")
    dtfm.parseDateTime(s)
  }

}

object StringHelpers {

  def strOption(s: String) = s match {
    case null | "" | "\"\"" => None
    case _ => Option(s)
  }

  def unquote(s: String): String = s.replaceAll("\"", "")

}

