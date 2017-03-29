package uk.gov.ons.bi.ingest.builder

import com.typesafe.config.Config
import uk.gov.ons.bi.models._
import uk.gov.ons.bi.ingest.parsers.ImplicitHelpers._

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
object CHBuilder {

  def companyHouseFromMap(map: Map[String, String])(implicit config: Config): Option[CompaniesHouseRecord] = new CHBuilder(map).build

}

class CHBuilder(val map: Map[String, String])(implicit val config: Config) extends RecordBuilder[CompaniesHouseRecord] {

  def mapFirst(keys: String*): String = {
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


  def build: Option[CompaniesHouseRecord] = handled {
    CompaniesHouseRecord(
      companyName = map("CompanyName"),
      companyNumber = map("CompanyNumber"),
      companyCategory = map("CompanyCategory"),
      companyStatus = map("CompanyStatus"),
      countryOfOrigin = map("CountryOfOrigin"),
      postCode = mapFirst("RegAddress.PostCode", "RegAddressPostCode"),
      dissolutionDate = map("DissolutionDate").asDateTimeOpt,
      incorporationDate = map("IncorporationDate").asDateTimeOpt,
      accounts = accountFromMap,
      returns = returnsFromMap.?,
      sicCode = sicCodeFromMap.?,
      limitedPartnerships = limitedPartnershipFromMap,
      uri = None, // save space map("URI").?,
      previousNames = previousNamesFromMap
    )
  }

  // AccountsAccountRefDay,AccountsAccountRefMonth,AccountsNextDueDate,AccountsLastMadeUpDate,AccountsAccountCategory,


  def accountFromMap: Accounts = {
    Accounts(
      accountsRefDay = mapFirst("AccountsAccountRefDay", "Accounts.AccountRefDay"),
      accountsRefMonth = mapFirst("AccountsAccountRefMonth", "Accounts.AccountRefMonth"),
      nextDueDate = mapFirst("AccountsNextDueDate", "Accounts.NextDueDate").asDateTimeOpt,
      lastMadeUpDate = mapFirst("AccountsLastMadeUpDate", "Accounts.LastMadeUpDate").asDateTimeOpt,
      accountCategory = mapFirst("AccountsAccountCategory", "Accounts.AccountCategory").?
    )
  }

  // ReturnsNextDueDate,ReturnsLastMadeUpDate,
  def returnsFromMap: Returns = {
    Returns(
      nextDueDate = mapFirst("ReturnsNextDueDate", "Returns.NextDueDate").asDateTimeOpt,
      lastMadeUpDate = mapFirst("ReturnsLastMadeUpDate", "Returns.LastMadeUpDate").asDateTimeOpt
    )
  }

  // SICCodeSicText_1,SICCodeSicText_2,SICCodeSicText_3,SICCodeSicText_4,
  def sicCodeFromMap: SICCode = {
    SICCode(
      sicText1 = mapFirst("SICCodeSicText_1", "SICCode.SicText_1"),
      sicText2 = mapFirst("SICCodeSicText_2", "SICCode.SicText_2"),
      sicText3 = mapFirst("SICCodeSicText_3", "SICCode.SicText_3"),
      sicText4 = mapFirst("SICCodeSicText_4", "SICCode.SicText_4")
    )
  }


  // LimitedPartnershipsNumGenPartners,LimitedPartnershipsNumLimPartners,
  def limitedPartnershipFromMap: LimitedPartnerships = {
    LimitedPartnerships(
      numGenPartners = mapFirst("LimitedPartnershipsNumGenPartners", "LimitedPartnerships.NumGenPartners").asIntOpt,
      numLimPartners = mapFirst("LimitedPartnershipsNumLimPartners", "LimitedPartnerships.NumLimPartners").asIntOpt
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

  def previousNamesFromMap: PreviousNames = {
    def getPrevName(i: Int) = PreviousName(
      condate = mapFirst(s"PreviousName_${i}CONDATE", s"PreviousName_$i.CONDATE"),
      company_name = mapFirst(s"PreviousName_${i}CompanyName", s"PreviousName_$i.CompanyName")
    ).?
    PreviousNames(
      previousName1 = getPrevName(1),
      previousName2 = getPrevName(2),
      previousName3 = getPrevName(3),
      previousName4 = getPrevName(4),
      previousName5 = getPrevName(5),
      previousName6 = getPrevName(6),
      previousName7 = getPrevName(7),
      previousName8 = getPrevName(8),
      previousName9 = getPrevName(9),
      previousName10 = getPrevName(10)
    )
  }

}
