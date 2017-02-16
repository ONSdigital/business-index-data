package uk.gov.ons.bi.models

import uk.gov.ons.bi.models.BIndexConsts._

case class BusinessIndexRec(
   id: Long, // the same as uprn ?
   businessName: String,
   uprn: Long,
   postCode: String,
   industryCode: Long,
   legalStatus: String,
   tradingStatus: String,
   turnover: String,
   employmentBands: String
) {

  val Delim = ","

  def toCsv =
    s"""$id$Delim"$businessName"$Delim$uprn$Delim$industryCode$Delim$legalStatus$Delim"$tradingStatus"$Delim"$turnover"$Delim"$employmentBands""""

}

object BusinessIndexRec {

  // build business index from elastic search map of fields
  def fromMap(id: Long, map: Map[String, Any]) = BusinessIndexRec(
    id = id,
    businessName = map.getOrElse(BiName, EmptyStr).toString,
    uprn = java.lang.Long.parseLong(map.getOrElse(BiUprn, 0L).toString),
    postCode = map.getOrElse(BiPostCode, EmptyStr).toString,
    industryCode = map.getOrElse(BiIndustryCode, EmptyStr).toString.toLong,
    legalStatus = map.getOrElse(BiLegalStatus, EmptyStr).toString,
    tradingStatus = map.getOrElse(BiTradingStatus, EmptyStr).toString,
    turnover = map.getOrElse(BiTurnover, EmptyStr).toString,
    employmentBands = map.getOrElse(BiEmploymentBand, EmptyStr).toString
  )

  def toMap(bi: BusinessIndexRec): Map[String, Any] = Map(
    BiName -> bi.businessName.toUpperCase,
    BiUprn -> bi.uprn,
    BiPostCode -> bi.postCode,
    BiIndustryCode -> bi.industryCode,
    BiLegalStatus -> bi.legalStatus,
    BiTradingStatus -> bi.tradingStatus,
    BiTurnover -> bi.turnover,
    BiEmploymentBand -> bi.employmentBands
  )

}

object BIndexConsts {
  val BiType = "business"
  val BiName = "BusinessName"
  val BiNameSuggest = "BusinessName_suggest"
  val BiUprn = "UPRN"
  val BiPostCode = "PostCode"
  val BiIndustryCode = "IndustryCode"
  val BiLegalStatus = "LegalStatus"
  val BiTradingStatus = "TradingStatus"
  val BiTurnover = "Turnover"
  val BiEmploymentBand = "EmploymentBands"

  val EmptyStr = ""
}