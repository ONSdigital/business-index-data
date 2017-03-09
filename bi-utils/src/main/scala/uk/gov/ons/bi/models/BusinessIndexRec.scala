package uk.gov.ons.bi.models

import java.util

import scala.collection.JavaConverters._
import uk.gov.ons.bi.models.BIndexConsts.{toString, _}

case class BusinessIndexRec(
                             id: Long, // the same as uprn ?
                             businessName: String,
                             uprn: Long,
                             postCode: Option[String],
                             industryCode: Option[Long],
                             legalStatus: Option[String],
                             tradingStatus: Option[String],
                             turnover: Option[String],
                             employmentBands: Option[String],
                             vatRefs: Option[Seq[Long]],
                             payeRefs: Option[Seq[String]]
                           ) {

  // method that used as output on UI (some fields are hidden)
  def secured: BusinessIndexRec = this.copy(vatRefs = None, payeRefs = None, postCode = None)


  def toCsvSecured: String = BusinessIndexRec.toString(List(id, businessName, uprn, industryCode, legalStatus,
    tradingStatus, turnover, employmentBands))

  def toCsv: String = BusinessIndexRec.toString(List(id, businessName, uprn, industryCode, legalStatus,
    tradingStatus, turnover, employmentBands, vatRefs, payeRefs))

}

object BusinessIndexRec {

  val Delim = ","

  def toString(fields: List[Any]): String = fields.map {
    case Some(a) => s"$a"
    case None => ""
    case z => s"$z"
  }.mkString(Delim)

  // build business index from elastic search map of fields
  def fromMap(id: Long, map: Map[String, Any]) = BusinessIndexRec(
    id = id,
    businessName = map.getOrElse(BiName, EmptyStr).toString,
    uprn = java.lang.Long.parseLong(map.getOrElse(BiUprn, 0L).toString),
    postCode = map.get(BiPostCode).map(_.toString),
    industryCode = map.get(BiIndustryCode).map(_.toString.toLong),
    legalStatus = map.get(BiLegalStatus).map(_.toString),
    tradingStatus = map.get(BiTradingStatus).map(_.toString),
    turnover = map.get(BiTurnover).map(_.toString),
    employmentBands = map.get(BiEmploymentBand).map(_.toString),
    vatRefs = map.get(BiVatRefs).map {
      case e: util.ArrayList[Long] => e.asScala
      case e: Seq[Long] => e
      case e: String => e.split(",").map(_.toLong)
    },
    payeRefs = map.get(BiPayeRefs).map {
      case e: util.ArrayList[String] => e.asScala
      case ps: Seq[String] => ps
      case e: String => e.split(",").toSeq
    }
  )

  def toMap(bi: BusinessIndexRec): Map[String, Any] = Map(
    BiName -> bi.businessName.toUpperCase,
    BiUprn -> bi.uprn,
    BiPostCode -> bi.postCode.orNull,
    BiIndustryCode -> bi.industryCode.orNull,
    BiLegalStatus -> bi.legalStatus.orNull,
    BiTradingStatus -> bi.tradingStatus.orNull,
    BiTurnover -> bi.turnover.orNull,
    BiEmploymentBand -> bi.employmentBands.orNull,
    BiVatRefs -> bi.vatRefs.orNull,
    BiPayeRefs -> bi.payeRefs.orNull
  )

  val BiSecuredHeader: String = toString(List("ID", BiName, BiUprn, BiIndustryCode, BiLegalStatus,
    BiTradingStatus, BiTurnover, BiEmploymentBand))

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
  val BiVatRefs = "VatRefs"
  val BiPayeRefs = "PayeRefs"

  val EmptyStr = ""

}