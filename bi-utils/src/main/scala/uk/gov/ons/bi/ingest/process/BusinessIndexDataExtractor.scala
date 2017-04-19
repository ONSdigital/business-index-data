package uk.gov.ons.bi.ingest.process

import uk.gov.ons.bi.ingest.process.BandMappings._
import uk.gov.ons.bi.ingest.process.ExtractorHelper._
import uk.gov.ons.bi.models.SICCode

import scala.util.Try

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */

object ExtractorHelper {

  def firstNonEmpty[T](col: Seq[T])(f: T => String): Option[String] = col.find(obj => f(obj).nonEmpty).map(f)

}

class BusinessIndexDataExtractor(val cvp: BusinessData) {


  def companyName: String = cvp match {
    case BusinessData(_, ch :: tl, _, _) => firstNonEmpty(ch :: tl)(_.companyName).getOrElse("") // example of how we can iterate throw all records to find first non empty
    case BusinessData(_, Nil, vt :: tl, _) => vt.name.nameline1
    case BusinessData(_, Nil, Nil, py :: tl) => py.name.nameline1
    case _ => ""
  }

  def uprn: Long = cvp.ubrn.toLong

  def extractCode(pc: String): String = if (pc.length > 1) pc.substring(0, 2) else ""

  def postCode: String = cvp match {
    case BusinessData(_, ch :: tl, _, _) if ch.postCode.length > 1 => extractCode(ch.postCode)
    case BusinessData(_, Nil, vt :: tl, _) => extractCode(vt.address.postcode)
    case BusinessData(_, Nil, Nil, py :: tl) => extractCode(py.address.postcode)
    case _ => ""
  }

  def industryCode: Option[String] = cvp match {
    case BusinessData(_, ch1 :: tl, _, _) if ch1.sicCode.nonEmpty => ch1.sicCode.flatMap(_.sicCodeNum)
    case BusinessData(_, _, vt :: tl, _) => SICCode.code(vt.sic92)
    case _ => None
  }

  def legalStatus: String = {
    val r = cvp match {
      // case BusinessData(_, c1 :: tl, _, _) => c1.company_status is it trading status ???
      case BusinessData(_, _, vt :: tl, _) => vt.legalstatus
      case BusinessData(_, _, Nil, py :: tl) => py.legalstatus
      case _ => "0"
    }
    // we need to cut decimal part: 1.0, 2.0 => "1", "2"
    Try(r.toDouble.toInt).getOrElse(0).toString
  }

  def tradingStatus: String = tradingStatusBand(cvp.c.headOption.map(_.companyStatus).getOrElse("")) // Unknown yet

  def turnover: String = {
    val turnover = cvp match {
      case BusinessData(_, _, vt :: tl, _) => vt.turnover
      case _ => "0"
    }
    turnoverBand(turnover.toLong)
  }

  def employment: String = {
    val emplNum = cvp match {
      case BusinessData(_, _, _, py :: tl) => py.month_jobs.recentJobs
      case _ => 0
    }
    employmentBand(emplNum)
  }

  def vatRefs: Seq[Long] = {
    cvp match {
      case BusinessData(_, _, vt, _) => vt.map(_.vatref.toLong)
      case _ => Seq()
    }
  }

  def payeRefs: Seq[String] = {
    cvp match {
      case BusinessData(_, _, _, py) => py.map(_.payeref)
      case _ => Seq()
    }
  }

  def companyNo: Option[String] = {
    cvp match {
      case BusinessData(_, ch :: tail, _, _) => Option(ch.companyNumber)
      case _ => None
    }
  }

}