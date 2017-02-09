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
      val compHouseRec = x.ch.flatMap(ch.getById).headOption // might be empty & ignore all records except first
      val payeRec = x.paye.flatMap(paye.getById).headOption
      val vatRec = x.vat.flatMap(vat.getById).headOption
      implicit val cvp = (compHouseRec, vatRec, payeRec)

      BusinessIndex(
        id = x.id.toInt,
        name = extractCompanyName,
        uprn = extractUprn,
        industryCode = extractIndustryCode,
        legalStatus = extractLegalStatus,
        tradingStatus = extractTradingStatus,
        turnover = extractTurnover,
        employmentBand = extractExploymentBand
      )
    }
    }
  }

  // TODO: cleanup this triple tuple
  def extractCompanyName()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord], Option[PayeRecord2])) = {
    cvp match {
      case (Some(ch), _, _) => ch.company_name
      case (None, Some(vt), _) => vt.name.toString
      case (None, None, Some(py)) => py.name.toString
      case _ => ""
    }
  }

  def extractUprn()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord], Option[PayeRecord2])): Long = {
    -1L // TODO: suppose to come from address index
  }


  def extractIndustryCode()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord], Option[PayeRecord2])): Long = {
    cvp match {
      case (_, Some(vt), _) => vt.inqcode.toLong
      case (_, None, Some(py)) => py.inqcode.toLong
      case _ => -1
    }
  }

  def extractLegalStatus()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord], Option[PayeRecord2])): String = {
    cvp match {
      case (Some(c1), _, _) => c1.company_status // TODO: company status is legal status ?
      case (None, Some(vt), _) => vt.legalstatus
      case (None, None, Some(py)) => py.legalstatus
      case _ => ""
    }
  }

  def extractTradingStatus()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord], Option[PayeRecord2])): String = {
    cvp match {
      case (Some(c1), _, _) => c1.company_status
      case _ => ""
    }
  }

  def extractTurnover()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord], Option[PayeRecord2])): String = {
    cvp match {
      case (_, Some(vt), _) => vt.turnover
      case _ => ""
    }
  }

  def extractExploymentBand()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord], Option[PayeRecord2])): String = {
    cvp match {
      case (_, _, Some(py)) => py.employer_cat
      case _ => ""
    }
  }
}
