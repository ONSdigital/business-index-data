package uk.gov.ons.bi.ingest.process

import java.io.File

import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.parsers.CsvProcessor._
import uk.gov.ons.bi.ingest.parsers.LinkedFileParser

import scala.io.Source

/**
  *
  * Temporary application for testing. Eventually code from this class should be migrated to Spark
  *
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
object BusinessLinkerApp extends App {

  // -Dout.path=/Users/Volodymyr.Glushak/git/business-index-data/models/src/test/resources/OUT.json -Dlinking.path=/Users/Volodymyr.Glushak/git/business-index-data/models/src/test/resources/linking.json -Dch.path=/Users/Volodymyr.Glushak/git/business-index-data/models/src/test/resources/CH_Output.csv -Dpaye.path=/Users/Volodymyr.Glushak/git/business-index-data/models/src/test/resources/PAYE_Output.csv -Dvat.path=/Users/Volodymyr.Glushak/git/business-index-data/models/src/test/resources/VAT_Output.csv    models/run

  def getSys(name: String) = sys.props.getOrElse(name, sys.error(s"System var $name missed"))

  def readFile(filename: String) = Source.fromFile(filename).getLines.toSeq

  def writeToFile(name: String, content: String) = {
    printToFile(name) { x =>
      x.println(content)
    }
  }

  def printToFile(name: String)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(new File(name))
    try { op(p) } finally { p.close() }
  }

  val chPath = getSys("ch.path")
  val payePath = getSys("paye.path")
  val vatPath = getSys("vat.path")
  val linkingPath = getSys("linking.path")

  val outPath = getSys("out.path")

  // read all input data
  // we need all InputData to be represented as DataSource

  val chMapList = csvToMap(readFile(chPath)).map(chCmp =>
    CHBuilder.companyHouseFromMap(chCmp)
  ).map(ch => ch.company_number -> ch).toMap

  val payeMapList = csvToMap(readFile(payePath)).map(payeRec =>
    PayeBuilder.payeFromMap(payeRec)
  ).map(py => py.entref -> py).toMap

  val vatMapList = csvToMap(readFile(vatPath)).map(vatRec =>
    VATBuilder.vatFromMap(vatRec)
  ).map(vt => vt.entref -> vt).toMap

  val links = LinkedFileParser.parse(readFile(linkingPath).mkString("\n")).head.map { lk =>
    lk.id -> lk
  }.toMap

  def asMapDS[T](map: Map[String, T]) = new MapDataSource(map)

  // invoke linker class
  // and pass CSV as DataSources
  val busObjs = new BusinessLinker().buildLink(
    asMapDS(links),
    asMapDS(vatMapList),
    asMapDS(payeMapList),
    asMapDS(chMapList)
  )

  val header = """"ID","BusinessName","UPRN","IndustryCode","LegalStatus","TradingStatus","Turnover","EmploymentBands""""

  printToFile(outPath) { writer =>

    writer.println(header)
    busObjs.foreach(x => writer.println(x.toCsv))

  }

}
