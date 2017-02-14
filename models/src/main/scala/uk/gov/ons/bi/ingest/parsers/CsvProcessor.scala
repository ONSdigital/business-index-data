package uk.gov.ons.bi.ingest.parsers


import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.LoggerFactory
import uk.gov.ons.bi.ingest.parsers.StringHelpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
object CsvProcessor {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  val Delimiter = ",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)" // coma, ignore quoted comas
  val Eol = System.lineSeparator

  def csvToMapToObj[T](csvString: Iterator[String], f: Map[String, String] => T) = {
    val counter = new AtomicInteger(0)

    def splt(s: String) = s.split(Delimiter).map(v => unquote(v.trim))

    val header = splt(csvString.next)

    val res = csvString.map(dataLine => Future {
      val c = counter.incrementAndGet()
      if (c % 10000 == 0) logger.debug(s"Processed $c records")
      f(header zip splt(dataLine) toMap)
    })
    Await.result(Future.sequence(res), 60 minutes)
  }

  def csvToMap[T](csvString: Iterator[String]) = csvToMapToObj(csvString, identity)
}


