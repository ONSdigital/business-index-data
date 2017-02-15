package uk.gov.ons.bi.ingest.builder

import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.ingest.models.{Address, PayeName, TradStyle}

import scala.util.control.NonFatal

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
trait RecordBuilder[T] {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  def map: Map[String, String]

  def build: Option[T]

  implicit def config: Config

  protected def IgnoreBrokenRecords = getPropOrElse("ignore.csv.errors", "true").toBoolean

  def handled(f: => T): Option[T] = {
    try {
      Some(f)
    } catch {
      case NonFatal(exc) =>
        val msg = s"Exception while building record ${exc.getMessage}. Data map: $map"
        if (IgnoreBrokenRecords) { logger.error(msg); None} else throw new RuntimeException(msg, exc)
    }
  }

  // util methods below that are used in more than one type of records
  protected def multiLineNameFromMap = PayeName(
    nameline1 = map.getOrElse("nameline", map("nameline1")), // PAYE and VAT files has different header
    nameline2 = map("nameline2"),
    nameline3 = map("nameline3")
  )

  protected def tradStyleFromMap = TradStyle(
    tradstyle1 = map("tradstyle1"),
    tradstyle2 = map("tradstyle2"),
    tradstyle3 = map("tradstyle3")
  )

  protected def addressFromMap = Address(
    line_1 = map("address1"),
    line_2 = map("address2"),
    line_3 = map("address3"),
    line_4 = map("address4"),
    line_5 = map("address5"),
    postcode = map("postcode")
  )

}
