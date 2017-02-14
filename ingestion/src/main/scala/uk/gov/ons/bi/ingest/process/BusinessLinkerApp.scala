package uk.gov.ons.bi.ingest.process

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.ingest.parsers.CsvProcessor._
import uk.gov.ons.bi.ingest.parsers.LinkedFileParser
import uk.gov.ons.bi.ingest.{BiConfigManager, ElasticClientBuilder, ElasticImporter}

import scala.concurrent.Await
import scala.concurrent.duration._
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

  val chPath = getProp("ch.path")
  val payePath = getProp("paye.path")
  val vatPath = getProp("vat.path")
  val linkingPath = getProp("linking.path")

  val outPath = getProp("out.path")

  // read all input data
  // we need all InputData to be represented as DataSource

  // invoke linker class
  // and pass CSV as links
  val busObjs = new BusinessLinker().transformAndLink(
    readFile(vatPath),
    readFile(payePath),
    readFile(chPath),
    readFile(linkingPath).mkString("\n")
  )

  val header = """"ID","BusinessName","UPRN","IndustryCode","LegalStatus","TradingStatus","Turnover","EmploymentBands""""

  printToFile(outPath) { writer =>
    writer.println(header)
    busObjs.foreach(x => writer.println(x.toCsv))
  }

  logger.info("File saved. Output data to elasticsearch ... ")

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
