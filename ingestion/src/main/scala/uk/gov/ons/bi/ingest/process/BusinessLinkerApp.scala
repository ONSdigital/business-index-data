package uk.gov.ons.bi.ingest.process

import java.io.File

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.parsers.CsvProcessor._
import uk.gov.ons.bi.ingest.parsers.LinkedFileParser
import uk.gov.ons.bi.ingest.{BiConfigManager, ElasticClientBuilder, ElasticImporter}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  *
  * Temporary application for testing. Eventually code from this class should be migrated to Spark
  *
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
object BusinessLinkerApp extends App {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  // elastic search part

  val config = BiConfigManager.envConf(ConfigFactory.load())

  val elasticClient = ElasticClientBuilder.build(config)

  val elasticImporter = new ElasticImporter(elasticClient)

  def getProp(name: String) = config.getString(name)

  def readFile(filename: String) = Source.fromFile(filename).getLines.toSeq

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

  val chPath = getProp("ch.path")
  val payePath = getProp("paye.path")
  val vatPath = getProp("vat.path")
  val linkingPath = getProp("linking.path")

  val outPath = getProp("out.path")

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

  val biName = getProp("elasticsearch.bi.name")
//  val resFutures = elasticImporter.initBiIndex(biName) flatMap { x =>
//    println(s"Index created with results $x")
//    elasticImporter.loadBusinessIndex(biName, busObjs)
//  }
  val resFutures = elasticImporter.loadBusinessIndex(biName, busObjs)
  // blocking, for test purposes only
  val loadTimeout = Option(System.getProperty("indexing.timeout")).getOrElse("100").toInt
  Try(Await.result(resFutures, loadTimeout.seconds)) match {
    case Success(ress) =>
//      ress.foreach(r => {
//        logger.debug(r.original)
//        logger.debug(s"${r.id} -> created: ${r.isCreated}")
//      })
      logger.info(s"Successfully imported data")
    case Failure(err) => logger.error("Unable to import data", err)
  }
}
