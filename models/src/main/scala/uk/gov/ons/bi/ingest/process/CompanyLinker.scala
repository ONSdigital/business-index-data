package uk.gov.ons.bi.ingest.process

import uk.gov.ons.bi.ingest.models._

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */


class CompanyLinker {

  def buildLink(linking: DataSource[String, LinkedRecord],
                vat: DataSource[String, VatRecord2],
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

  def extractCompanyName()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord2], Option[PayeRecord2])) = {
    cvp match {
      case (Some(ch), _, _) => ch.company_name
      case (None, Some(vt), _) => vt.name.toString
      case (None, None, Some(py)) => py.name.toString
      case _ => ""
    }
  }

  def extractUprn()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord2], Option[PayeRecord2])): String = {
    "" // TODO: suppose to come from address index
  }


  def extractIndustryCode()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord2], Option[PayeRecord2])): String = {
    cvp match {
      case (_, Some(vt), _) => vt.inqcode
      case (_, None, Some(py)) => py.inqcode
      case _ => ""
    }
  }

  def extractLegalStatus()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord2], Option[PayeRecord2])): String = {
    cvp match {
      case (Some(c1), _, _) => c1.company_status // TODO: company status is legal status ?
      case (None, Some(vt), _) => vt.legalstatus
      case (None, None, Some(py)) => py.legalstatus
      case _ => ""
    }
  }

  def extractTradingStatus()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord2], Option[PayeRecord2])): String = {
    cvp match {
      case (Some(c1), _, _) => c1.company_status
      case _ => ""
    }
  }

  def extractTurnover()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord2], Option[PayeRecord2])): String = {
    cvp match {
      case (_, Some(vt), _) => vt.turnover
      case _ => ""
    }
  }

  def extractExploymentBand()(implicit cvp: (Option[CompaniesHouseRecord], Option[VatRecord2], Option[PayeRecord2])): String = {
    cvp match {
      case (_, _, Some(py)) => py.employer_cat
      case _ => ""
    }
  }
}
