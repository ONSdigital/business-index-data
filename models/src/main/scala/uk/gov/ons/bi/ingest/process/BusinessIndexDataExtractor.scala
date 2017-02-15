package uk.gov.ons.bi.ingest.process

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
    case BusinessData(_, Nil, vt :: tl, _) => vt.name.toString
    case BusinessData(_, Nil, Nil, py :: tl) => py.name.toString
    case _ => ""
  }

  def uprn: Long = cvp.ubrn.toLong

  def industryCode: Long = cvp match {
    case BusinessData(_, ch1 :: tl, _, _) if ch1.sic_code.nonEmpty => ch1.sic_code.map(_.sicCodeNum).getOrElse(0L)
    case BusinessData(_, _, vt :: tl, _) => vt.inqcode.toLong
    case BusinessData(_, _, Nil, py :: tl) => py.inqcode.toLong
    case _ => -1
  }

  def legalStatus: String = cvp match {
    case BusinessData(_, c1 :: tl, _, _) => c1.company_status // TODO: company status is legal status ?
    case BusinessData(_, Nil, vt :: tl, _) => vt.legalstatus
    case BusinessData(_, Nil, Nil, py :: tl) => py.legalstatus
    case _ => ""
  }

  def tradingStatus: String = cvp match {
    case BusinessData(_, c1 :: tl, _, _) => c1.company_status
    case _ => ""
  }

  def turnover: String = cvp match {
    case BusinessData(_, _, vt :: tl, _) => vt.turnover
    case _ => ""
  }

  def exploymentBand: String = cvp match {
    case BusinessData(_, _, _, py :: tl) => py.employer_cat
    case _ => ""
  }

}