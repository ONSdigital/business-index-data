package uk.gov.ons.bi.models

import org.joda.time.DateTime

case class Accounts(
                     accountsRefDay: String,
                     accountsRefMonth: String,
                     nextDueDate: Option[DateTime],
                     lastMadeUpDate: Option[DateTime],
                     accountCategory: Option[String]
)

case class Returns(nextDueDate: Option[DateTime], lastMadeUpDate: Option[DateTime])

case class Mortgages(
  num_mort_charges: Option[Int],
  num_mort_outstanding: Option[Int],
  num_mort_part_satisfied: Option[Int],
  num_mort_satisfied: Option[Int]
)


object SICCode {

  private val NumStartRegex = "(\\d+).*".r
  def code(n: String): Option[String] = n match {
    case NumStartRegex(x) => Option(x)
    case _ => None
  }
}

case class SICCode(sicText1: String, sicText2: String, sicText3: String, sicText4: String) {
  def fullText = s"$sicText1 $sicText2 $sicText3 $sicText4"

  def sicCodeNum = SICCode.code(fullText)
}

case class LimitedPartnerships(numGenPartners: Option[Int], numLimPartners: Option[Int])

case class PreviousName(condate: String, company_name: String)

case class RegistrationAddress(
  care_of: Option[String],
  po_box: Option[String],
  address_line_1: String,
  address_line_2: String,
  post_town: Option[String],
  county: Option[String],
  country: Option[String],
  postcode: Option[String]
)

case class PreviousNames(
                          previousName1: Option[PreviousName],
                          previousName2: Option[PreviousName],
                          previousName3: Option[PreviousName],
                          previousName4: Option[PreviousName],
                          previousName5: Option[PreviousName],
                          previousName6: Option[PreviousName],
                          previousName7: Option[PreviousName],
                          previousName8: Option[PreviousName],
                          previousName9: Option[PreviousName],
                          previousName10: Option[PreviousName]
)

case class CompaniesHouseRecord(
                                 companyName: String,
                                 companyNumber: String,
                                 companyCategory: String,
                                 companyStatus: String,
                                 countryOfOrigin: String,
                                 postCode: String,
                                 dissolutionDate: Option[DateTime],
                                 incorporationDate: Option[DateTime],
                                 accounts: Accounts,
                                 returns: Option[Returns],
                                 sicCode: Option[SICCode],
                                 limitedPartnerships: LimitedPartnerships,
                                 uri: Option[String],
                                 previousNames: PreviousNames

)
