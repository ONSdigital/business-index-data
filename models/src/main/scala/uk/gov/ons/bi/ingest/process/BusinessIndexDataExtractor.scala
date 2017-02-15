package uk.gov.ons.bi.ingest.process

import uk.gov.ons.bi.ingest.models.SICCode

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */

object ExtractorHelper {

  def firstNonEmpty[T](col: Seq[T])(f: T => String) = col.find(obj => f(obj).nonEmpty).map(f)

}

class BusinessIndexDataExtractor(val cvp: BusinessData) {

  import ExtractorHelper._

  def companyName = cvp match {
    case BusinessData(_, ch :: tl, _, _) => firstNonEmpty(ch :: tl)(_.company_name).getOrElse("") // example of how we can iterate throw all records to find first non empty§
    case BusinessData(_, Nil, vt :: tl, _) => vt.name.nameline1
    case BusinessData(_, Nil, Nil, py :: tl) => py.name.nameline1
    case _ => ""
  }

  def uprn: Long = cvp.ubrn.toLong
  def extractCode(pc: String) = if (pc.length > 1) pc.substring(0, 2) else ""
  def postCode = cvp match {
    case BusinessData(_, ch :: tl, _, _) if ch.post_code.length > 1 => extractCode(ch.post_code)
    case BusinessData(_, Nil, vt :: tl, _) => extractCode(vt.address.postcode)
    case BusinessData(_, Nil, Nil, py :: tl) => extractCode(py.address.postcode)
    case _ => ""
  }

  def industryCode: Long = cvp match {
    case BusinessData(_, ch1 :: tl, _, _) if ch1.sic_code.nonEmpty => ch1.sic_code.map(_.sicCodeNum).getOrElse(0L)
    case BusinessData(_, _, vt :: tl, _) => SICCode.code(vt.sic92)
    case _ => 0L
  }

  def legalStatus: String = cvp match {
    case BusinessData(_, c1 :: tl, _, _) => c1.company_status
    case BusinessData(_, Nil, vt :: tl, _) => vt.legalstatus
    case BusinessData(_, Nil, Nil, py :: tl) => py.legalstatus
    case _ => ""
  }

  def tradingStatus: String = "" // Unknown yet

  def turnover: String = cvp match {
    case BusinessData(_, _, vt :: tl, _) => vt.turnover
    case _ => ""
  }

  def exploymentBand: Int = cvp match {
    case BusinessData(_, _, _, py :: tl) => py.month_jobs.recent_jobs
    case _ => 0
  }

}