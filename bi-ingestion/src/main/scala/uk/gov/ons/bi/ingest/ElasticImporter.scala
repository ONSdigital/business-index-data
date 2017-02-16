package uk.gov.ons.bi.ingest

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.analyzers.{CustomAnalyzerDefinition, KeywordAnalyzer, LowercaseTokenFilter, StandardTokenizer}
import com.sksamuel.elastic4s.mappings.FieldType.{CompletionType, LongType, StringType}
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import com.typesafe.config.Config
import org.elasticsearch.common.settings.Settings
import org.slf4j.LoggerFactory
import uk.gov.ons.bi.ingest.models.BusinessIndex
import uk.gov.ons.bi.ingest.process.{DataSource, MapDataSource}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.ons.bi.ingest.helper.Utils._

import scala.util.Try

/**
  * Created by Volodymyr.Glushak on 10/02/2017.
  */
class ElasticImporter(elastic: ElasticClient)(implicit val config: Config) {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  def initBiIndex(indexName: String) = {
    elastic.execute {
      delete index indexName
    } flatMap { x =>
      logger.info(s"Cleanup completed: $x")
      elastic.execute {
        // define the ElasticSearch index
        create
          .index(indexName)
          .mappings(
            mapping("business").fields(
              field("BusinessName", StringType) boost 4 analyzer "BusinessNameAnalyzer",
              field("BusinessName_suggest", CompletionType),
              field("UPRN", LongType) analyzer KeywordAnalyzer,
              field("IndustryCode", LongType) analyzer KeywordAnalyzer,
              field("LegalStatus", StringType) index "not_analyzed" includeInAll false,
              field("TradingStatus", StringType) index "not_analyzed" includeInAll false,
              field("Turnover", StringType) index "not_analyzed" includeInAll false,
              field("EmploymentBands", StringType) index "not_analyzed" includeInAll false
            )
          )
          .analysis(
            CustomAnalyzerDefinition(
              "BusinessNameAnalyzer",
              StandardTokenizer,
              LowercaseTokenFilter,
              edgeNGramTokenFilter("BusinessNameNGramFilter") minGram 2 maxGram 24))
      }
    }
  }

  val BatchSize = getPropOrElse("elastic.importer.batch.size", "1000").toInt
  val TheadDelays = getPropOrElse("elastic.importer.delay.ms", "5").toInt

  def initializeIndex(businessIndex: String) = {
    Try(elastic.execute {
      delete index businessIndex
    }) // ignore if index doesn't exists
    elastic.execute {
      create.index(businessIndex).mappings(
        mapping("business").fields(
          field("BusinessName", StringType) boost 4 analyzer "BusinessNameAnalyzer",
          field("BusinessName_suggest", CompletionType),
          field("UPRN", LongType) analyzer KeywordAnalyzer,
          field("PostCode", StringType) analyzer KeywordAnalyzer,
          field("IndustryCode", LongType) analyzer KeywordAnalyzer,
          field("LegalStatus", StringType) index "not_analyzed" includeInAll false,
          field("TradingStatus", StringType) index "not_analyzed" includeInAll false,
          field("Turnover", StringType) index "not_analyzed" includeInAll false,
          field("EmploymentBands", StringType) index "not_analyzed" includeInAll false
        )
      ).analysis(CustomAnalyzerDefinition("BusinessNameAnalyzer",
        StandardTokenizer,
        LowercaseTokenFilter,
        edgeNGramTokenFilter("BusinessNameNGramFilter") minGram 2 maxGram 24)
      )
    }
  }

  def loadBusinessIndex(indexName: String,
                        d: DataSource[String, BusinessIndex]) = {
    val r = d match {
      case data: MapDataSource[String, BusinessIndex] => data.data.grouped(BatchSize).map { biMap =>
        if (TheadDelays > 0) Thread.sleep(TheadDelays)
        logger.debug(s"Bulk of size ${biMap.size} is about to be processed...")
        elastic.execute {
          bulk(
            biMap.map { case (i, bi) =>
              logger.trace(s"Indexing entry in ElasticSearch $bi")
              index into indexName / "business" id bi.id fields("BusinessName" -> bi.name.toUpperCase,
                "UPRN" -> bi.uprn,
                "PostCode" -> bi.postCode,
                "IndustryCode" -> bi.industryCode,
                "LegalStatus" -> bi.legalStatus,
                "TradingStatus" -> bi.tradingStatus,
                "Turnover" -> bi.turnover,
                "EmploymentBands" -> bi.employmentBand)
            }.toSeq)
        }
      }
    }
    Future.sequence(r)
  }
}

case class ElasticConfiguration(localMode: Boolean, clusterName: String, uri: String, sniffEnabled: Boolean)

object BiConfigManager {

  private[this] val logger = LoggerFactory.getLogger(BiConfigManager.getClass)

  def envConf(conf: Config) = {
    val env = sys.props.get("environment").getOrElse("default")
    logger.info(s"Load config for [$env] env")
    val envConf = conf.getConfig(s"env.$env")
    logger.debug(envConf.toString)
    envConf
  }
}

object ElasticClientBuilder {

  def build(envConf: Config) = {
    val cfg = ElasticConfiguration(
      localMode = envConf.getBoolean("elasticsearch.local"),
      clusterName = envConf.getString("elasticsearch.cluster.name"),
      uri = envConf.getString("elasticsearch.uri"),
      sniffEnabled = envConf.getBoolean("elasticsearch.client.transport.sniff")
    )
    buildWithConfig(cfg)
  }

  def buildWithConfig(config: ElasticConfiguration) = {
    val settings = Settings.settingsBuilder().put("client.transport.sniff", config.sniffEnabled)

    config.localMode match {
      case true =>
        ElasticClient.local(
          settings.put("path.home", System.getProperty("java.io.tmpdir")).build())
      case _ =>
        ElasticClient.transport(
          settings.put("cluster.name", config.clusterName).build(), ElasticsearchClientUri(config.uri))
    }
  }
}
