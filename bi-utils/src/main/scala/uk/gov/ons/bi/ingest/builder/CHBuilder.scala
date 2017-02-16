package uk.gov.ons.bi.ingest.builder

import com.typesafe.config.Config
import uk.gov.ons.bi.ingest.models._
import uk.gov.ons.bi.ingest.parsers.ImplicitHelpers._

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
object CHBuilder {

  def companyHouseFromMap(map: Map[String, String])(implicit config: Config) = new CHBuilder(map).build

}

class CHBuilder(val map: Map[String, String])(implicit val config: Config) extends RecordBuilder[CompaniesHouseRecord] {

  def mapFirst(keys: String*) = {
    map(keys.find(k => map.get(k).nonEmpty).getOrElse(sys.error(s"Not found keys $keys in CH map $map")))
  } 
  
  // CompanyName,CompanyNumber,
  // RegAddressCareOf,RegAddressPOBox,
  // RegAddressAddressLine1,RegAddressAddressLine2,RegAddressPostTown,RegAddressCounty,RegAddressCountry,RegAddressPostCode,
  // CompanyCategory,CompanyStatus,CountryOfOrigin,
  // DissolutionDate,IncorporationDate,
  // AccountsAccountRefDay,AccountsAccountRefMonth,AccountsNextDueDate,AccountsLastMadeUpDate,AccountsAccountCategory,
  // ReturnsNextDueDate,ReturnsLastMadeUpDate,
  // MortgagesNumMortCharges,MortgagesNumMortOutstanding,MortgagesNumMortPartSatisfied,MortgagesNumMortSatisfied,
  // SICCodeSicText_1,SICCodeSicText_2,SICCodeSicText_3,SICCodeSicText_4,
  // LimitedPartnershipsNumGenPartners,LimitedPartnershipsNumLimPartners,
  // URI,
  // PreviousName_1CONDATE,PreviousName_1CompanyName,
  // PreviousName_2CONDATE,PreviousName_2CompanyName,
  // PreviousName_3CONDATE,PreviousName_3CompanyName,
  // PreviousName_4CONDATE,PreviousName_4CompanyName,
  // PreviousName_5CONDATE,PreviousName_5CompanyName,
  // PreviousName_6CONDATE,PreviousName_6CompanyName,
  // PreviousName_7CONDATE,PreviousName_7CompanyName,
  // PreviousName_8CONDATE,PreviousName_8CompanyName,
  // PreviousName_9CONDATE,PreviousName_9CompanyName,
  // PreviousName_10CONDATE,PreviousName_10CompanyName


  def build = handled {
    CompaniesHouseRecord(
      company_name = map("CompanyName"),
      company_number = map("CompanyNumber"),
      company_category = map("CompanyCategory"),
      company_status = map("CompanyStatus"),
      country_of_origin = map("CountryOfOrigin"),
      post_code = mapFirst("RegAddress.PostCode", "RegAddressPostCode"),
      dissolution_date = map("DissolutionDate").asDateTimeOpt,
      incorporation_date = map("IncorporationDate").asDateTimeOpt,
      accounts = accountFromMap,
      returns = returnsFromMap.?,
      sic_code = sicCodeFromMap.?,
      limitedPartnerships = limitedPartnershipFromMap,
      uri = None, // save space map("URI").?,
      previous_names = previousNamesFromMap
    )
  }

  // AccountsAccountRefDay,AccountsAccountRefMonth,AccountsNextDueDate,AccountsLastMadeUpDate,AccountsAccountCategory,


  def accountFromMap = {
    Accounts(
      accounts_ref_day = mapFirst("AccountsAccountRefDay", "Accounts.AccountRefDay"),
      accounts_ref_month = mapFirst("AccountsAccountRefMonth","Accounts.AccountRefMonth"),
      next_due_date = mapFirst("AccountsNextDueDate", "Accounts.NextDueDate").asDateTimeOpt,
      last_made_up_date = mapFirst("AccountsLastMadeUpDate", "Accounts.LastMadeUpDate").asDateTimeOpt,
      account_category = mapFirst("AccountsAccountCategory", "Accounts.AccountCategory").?
    )
  }

  // ReturnsNextDueDate,ReturnsLastMadeUpDate,
  def returnsFromMap = {
    Returns(
      next_due_date = mapFirst("ReturnsNextDueDate", "Returns.NextDueDate").asDateTimeOpt,
      last_made_up_date = mapFirst("ReturnsLastMadeUpDate", "Returns.LastMadeUpDate").asDateTimeOpt
    )
  }

  // SICCodeSicText_1,SICCodeSicText_2,SICCodeSicText_3,SICCodeSicText_4,
  def sicCodeFromMap = {
    SICCode(
      sic_text_1 = mapFirst("SICCodeSicText_1", "SICCode.SicText_1"),
      sic_text_2 = mapFirst("SICCodeSicText_2", "SICCode.SicText_2"),
      sic_text_3 = mapFirst("SICCodeSicText_3", "SICCode.SicText_3"),
      sic_text_4 = mapFirst("SICCodeSicText_4", "SICCode.SicText_4")
    )
  }


  // LimitedPartnershipsNumGenPartners,LimitedPartnershipsNumLimPartners,
  def limitedPartnershipFromMap = {
    LimitedPartnerships(
      num_gen_partners = mapFirst("LimitedPartnershipsNumGenPartners", "LimitedPartnerships.NumGenPartners").asIntOpt,
      num_lim_partners = mapFirst("LimitedPartnershipsNumLimPartners", "LimitedPartnerships.NumLimPartners").asIntOpt
    )
  }

  // PreviousName_1CONDATE,PreviousName_1CompanyName,
  // PreviousName_2CONDATE,PreviousName_2CompanyName,
  // PreviousName_3CONDATE,PreviousName_3CompanyName,
  // PreviousName_4CONDATE,PreviousName_4CompanyName,
  // PreviousName_5CONDATE,PreviousName_5CompanyName,
  // PreviousName_6CONDATE,PreviousName_6CompanyName,
  // PreviousName_7CONDATE,PreviousName_7CompanyName,
  // PreviousName_8CONDATE,PreviousName_8CompanyName,
  // PreviousName_9CONDATE,PreviousName_9CompanyName,
  // PreviousName_10CONDATE,PreviousName_10CompanyName

  def previousNamesFromMap = {
    def getPrevName(i: Int) = None //FIXME:    {
//      PreviousName(
//        condate = mapFirst(s"PreviousName_${i}CONDATE", s"PreviousName_$i.CONDATE"),
//        company_name = mapFirst(s"PreviousName_${i}CompanyName", s"PreviousName_$i.CompanyName")
//      ).?
//    }
    PreviousNames(
      previous_name_1 = getPrevName(1),
      previous_name_2 = getPrevName(2),
      previous_name_3 = getPrevName(3),
      previous_name_4 = getPrevName(4),
      previous_name_5 = getPrevName(5),
      previous_name_6 = getPrevName(6),
      previous_name_7 = getPrevName(7),
      previous_name_8 = getPrevName(8),
      previous_name_9 = getPrevName(9),
      previous_name_10 = getPrevName(10)
    )
  }

}
