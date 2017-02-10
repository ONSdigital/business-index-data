package uk.gov.ons.bi.ingest

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import com.sksamuel.elastic4s.ElasticDsl._
import com.typesafe.config.Config
import org.elasticsearch.common.settings.Settings
import uk.gov.ons.bi.ingest.models.BusinessIndex
import uk.gov.ons.bi.ingest.process.DataSource

/**
  * Created by Volodymyr.Glushak on 10/02/2017.
  */
class ElasticImporter(elastic: ElasticClient) {

  // TODO: include proper logger

  def loadBusinessIndex(indexName: String, d: DataSource[String, BusinessIndex]) = {
    d.foreach { bi =>
      elastic.execute {
        println(s"Indexing entry in ElasticSearch $bi")
        index into indexName / "business" id bi.id fields(
          "BusinessName" -> bi.name,
          "UPRN" -> bi.uprn,
          "IndustryCode" -> bi.industryCode,
          "LegalStatus" -> bi.legalStatus,
          "TradingStatus" -> bi.tradingStatus,
          "Turnover" -> bi.turnover,
          "EmploymentBands" -> bi.turnover)
      }
    }
  }
}

case class ElasticConfiguration(localMode: Boolean, clusterName: String, uri: String, sniffEnabled: Boolean)


object BiConfigManager {

  def envConf(conf: Config) = {
    val env = sys.props.get("environment").getOrElse("default")
    println(s"Load config for [$env] env")
    val envConf = conf.getConfig(s"env.$env")
    println(envConf)
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
    val settings = Settings.settingsBuilder()
      .put("client.transport.sniff", config.sniffEnabled)
      .put("cluster.name", config.clusterName)

    config.localMode match {
      case true =>
        ElasticClient.local(settings.put("path.home", System.getProperty("java.io.tmpdir")).build())
      case _ =>
        ElasticClient.transport(settings.build(),
          ElasticsearchClientUri(config.uri)
        )
    }
  }
}