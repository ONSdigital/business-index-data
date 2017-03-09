package uk.gov.ons.bi.ingest.helper

import java.io.File
import java.nio.file.Paths

import com.typesafe.config.Config
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Try
import scala.util.control.NonFatal

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */
object Utils {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  val CurrentMonth: Int = new DateTime().getMonthOfYear

  def readFile(filename: String): Iterator[String] = {
    logger.info(s"Reading $filename")
    try {
      Source.fromFile(filename).getLines
    } catch {
      case NonFatal(e) => throw new RuntimeException(s"Can't read file $filename", e)
    }
  }

  def writeToFile(name: String, content: String): Unit = {
    logger.info(s"Write data to file $name")
    printToFile(name) { x =>
      x.println(content)
    }
  }

  def printToFile(name: String)(op: java.io.PrintWriter => Unit) {
    val file = new File(name)
    if (!file.getParentFile.exists()) {
      file.getParentFile.mkdirs()
    }
    val p = new java.io.PrintWriter(file)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

  def getResource(file: String): Iterator[String] = try {
    Source.fromInputStream(getClass.getResourceAsStream(file)).getLines()
  } catch {
    case NonFatal(e) => throw new RuntimeException(s"Can't get resource $file", e)
  }


  def getPropOrElse(name: String, default: => String)(implicit config: Config): String =
    Try(config.getString(name)).getOrElse(default)

  def getProp(name: String)(implicit config: Config): String =
    getPropOrElse(name, sys.error(s"Config $name was not found."))

}
