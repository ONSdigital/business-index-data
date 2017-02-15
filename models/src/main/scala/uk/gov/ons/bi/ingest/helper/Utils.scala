package uk.gov.ons.bi.ingest.helper

import java.io.File

import com.typesafe.config.Config
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Try

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */
object Utils {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  val Month = new DateTime().getMonthOfYear


  def readFile(filename: String) = {
    logger.info(s"Reading $filename")
    val res = Source.fromFile(filename).getLines
    // res.length go to the end of iterator ... logger.info(s"File $filename contains ${res.length} lines.")
    res
  }

  def writeToFile(name: String, content: String) = {
    printToFile(name) { x =>
      x.println(content)
    }
  }

  def printToFile(name: String)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(new File(name))
    try {
      op(p)
    } finally {
      p.close()
    }
  }

  def getResource(file: String) = Source.fromInputStream(getClass.getResourceAsStream(file)).getLines()


  def getPropOrElse(name: String, default: => String)(implicit config: Config) = Try(config.getString(name)).getOrElse(default)

  def getProp(name: String)(implicit config: Config) = getPropOrElse(name, sys.error(s"Config $name was not found."))

}
