package uk.gov.ons.bi.ingest.helper

import java.io.File
import java.nio.file.Paths

import com.typesafe.config.Config
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */
object Utils {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  val CurrentMonth: Int = new DateTime().getMonthOfYear

  def readFile(filename: String): Iterator[String] = {
    logger.info(s"Reading $filename")
    Try(Source.fromFile(filename).getLines) match {
      case Success(x) => x
      case Failure(e) => throw new RuntimeException(s"Can't read file $filename", e)
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
    Try(op(p)).foreach(x => p.close()) // like finally, ignore errors
  }

  def getResource(file: String): Iterator[String] =
    Try(Source.fromInputStream(getClass.getResourceAsStream(file)).getLines()) match {
      case Success(s) => s
      case Failure(e) => throw new RuntimeException(s"Can't get resource $file", e)
    }


  def getPropOrElse(name: String, default: => String)(implicit config: Config): String =
    Try(config.getString(name)).getOrElse(default)

  def getProp(name: String)(implicit config: Config): String =
    getPropOrElse(name, sys.error(s"Config $name was not found."))

}
