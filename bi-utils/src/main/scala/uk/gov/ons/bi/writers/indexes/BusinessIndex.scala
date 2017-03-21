package uk.gov.ons.bi.writers.indexes

import com.sksamuel.elastic4s.ElasticDsl.{edgeNGramTokenFilter, field, mapping}
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings.MappingDefinition
import uk.gov.ons.bi.writers.Initializer
import uk.gov.ons.bi.models.BIndexConsts._

/**
  * Created by Volodymyr.Glushak on 16/02/2017.
  */
class BusinessIndex(val indexName: String) extends Initializer {

  override def recordName: String = BiType

  override def analyzer: Option[AnalyzerDefinition] = Some(
    CustomAnalyzerDefinition(analyzerName,
      StandardTokenizer,
      LowercaseTokenFilter
      )
  )

  /**
    * Uses the Elastic4S client DSL to build a specification for a given index.
    * This will basically use a generic index construction mechanism to pre-build
    * the indexes that already exist at the time when the Spark application is executed.
    *
    * @return A mapping definition.
    */
  override def indexDefinition: MappingDefinition = mapping(recordName).fields(
    field(BiName, StringType) boost 4 analyzer analyzerName,
    field(BiNameSuggest, CompletionType),

    field(BiUprn, LongType) analyzer KeywordAnalyzer,

    field(BiPostCode, StringType) analyzer analyzerName,

    field(BiIndustryCode, LongType) analyzer KeywordAnalyzer,

    field(BiLegalStatus, StringType) index "not_analyzed" includeInAll false,
    field(BiTradingStatus, StringType) index "not_analyzed" includeInAll false,

    field(BiTurnover, StringType) index "not_analyzed" includeInAll false,

    field(BiEmploymentBand, StringType) index "not_analyzed" includeInAll false,

    field(BiPayeRefs, StringType) analyzer KeywordAnalyzer,
    field(BiVatRefs, LongType) analyzer KeywordAnalyzer

  )
}


object BusinessIndex {
  def apply(name: String): BusinessIndex = new BusinessIndex(name)
}
