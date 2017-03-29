package uk.gov.ons.bi.ingest.process

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.models.BIndexConsts._
import uk.gov.ons.bi.writers.{BiConfigManager, ElasticClientBuilder, ElasticImporter}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
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

  implicit val config = BiConfigManager.envConf(ConfigFactory.load())

  implicit val elasticClient = ElasticClientBuilder.build(config)

  val elasticImporter = new ElasticImporter

  def getProp(name: String): String = config.getString(name)

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

  val header = s""""ID","$cBiName","$cBiUprn","$cBiIndustryCode","$cBiLegalStatus","$cBiTradingStatus","$cBiTurnover","$cBiEmploymentBand", "$cBiVatRefs", "$cBiPayeRefs" """

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

  val initialization = getProp("elastic.recreate.index").toBoolean
  val initFuture = if (initialization) elasticImporter.initializeIndex(biName) else Future.successful {}
  val resFutures = initFuture.flatMap(x => elasticImporter.loadBusinessIndexFromMapDS(biName, busObjs))
  // blocking, for test purposes only
  val loadTimeout = Option(System.getProperty("indexing.timeout")).getOrElse("100").toInt // TODO: ???

  Try(Await.result(resFutures, loadTimeout.seconds)) match {
    case Success(ress) =>

      ress.foreach { r =>
        if (r.hasFailures) {
          logger.error(s"Failed while importing data: ${r.failureMessage}")
        } else {
          val cr = r.items
          // val cr2 = cr.map(ra => ra.indexResult.get.isCreated)
          logger.info(s"Successfully imported data. Total: ${cr.size}") // , created: ${cr.count(_ == (true))}")
        }
      }

    case Failure(err) => logger.error("Unable to import data", err)
  }
}
