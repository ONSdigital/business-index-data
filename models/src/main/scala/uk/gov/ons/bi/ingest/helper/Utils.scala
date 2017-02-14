package uk.gov.ons.bi.ingest.helper

import java.io.File

import org.slf4j.LoggerFactory

import scala.io.Source

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */
object Utils {

  private[this] val logger = LoggerFactory.getLogger(getClass)

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

}
