package uk.gov.ons.bi.ingest.process

import uk.gov.ons.bi.ingest.models._

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
class BusinessLinker {

  def buildLink(linking: DataSource[String, LinkedRecord],
                vat: DataSource[String, VatRecord],
                paye: DataSource[String, PayeRecord2],
                ch: DataSource[String, CompaniesHouseRecord]): DataSource[String, BusinessIndex] = {
    linking.map { x => {
      val compHouseRec = x.ch.flatMap(ch.getById)
      val payeRec = x.paye.flatMap(paye.getById)
      val vatRec = x.vat.flatMap(vat.getById)
      val extractor = new BusinessIndexDataExtractor(BusinessData(compHouseRec, vatRec, payeRec))

      BusinessIndex(
        id = x.id.toInt,
        name = extractor.companyName,
        uprn = extractor.uprn,
        industryCode = extractor.industryCode,
        legalStatus = extractor.legalStatus,
        tradingStatus = extractor.tradingStatus,
        turnover = extractor.turnover,
        employmentBand = extractor.exploymentBand
      )
    }
    }
  }
}

case class BusinessData(c: List[CompaniesHouseRecord], v: List[VatRecord], p: List[PayeRecord2])


object ExtractorHelper {

  def firstNonEmpty[T](col: Seq[T])(f: T => String) = col.find(obj => f(obj).nonEmpty).map(f)

}

class BusinessIndexDataExtractor(cvp: BusinessData) {

  import ExtractorHelper._

  def companyName = cvp match {
    case BusinessData(ch :: tl, _, _) => firstNonEmpty(ch :: tl)(_.company_name).getOrElse("") // example of how we can iterate throw all records to find first non empty§
    case BusinessData(Nil, vt :: tl, _) => vt.name.toString
    case BusinessData(Nil, Nil, py :: tl) => py.name.toString
    case _ => ""
  }

  def uprn: Long = {
    -1L // TODO: suppose to come from address index
  }

  def industryCode: Long = cvp match {
    case BusinessData(_, vt :: tl, _) => vt.inqcode.toLong
    case BusinessData(_, Nil, py :: tl) => py.inqcode.toLong
    case _ => -1
  }

  def legalStatus: String = cvp match {
    case BusinessData(c1 :: tl, _, _) => c1.company_status // TODO: company status is legal status ?
    case BusinessData(Nil, vt :: tl, _) => vt.legalstatus
    case BusinessData(Nil, Nil, py :: tl) => py.legalstatus
    case _ => ""
  }

  def tradingStatus: String = cvp match {
    case BusinessData(c1 :: tl, _, _) => c1.company_status
    case _ => ""
  }

  def turnover: String = cvp match {
    case BusinessData(_, vt :: tl, _) => vt.turnover
    case _ => ""
  }

  def exploymentBand: String = cvp match {
    case BusinessData(_, _, py :: tl) => py.employer_cat
    case _ => ""
  }

}
