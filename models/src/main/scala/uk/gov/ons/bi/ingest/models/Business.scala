package uk.gov.ons.bi.ingest.models

object Hmrc {
  case class VatRecord(
    id: String,
    vat_category: String
  )
}

case class Business(
  charitiesCommission: Option[CharitiesCommissionEntry],
  vatRecord: Option[Hmrc.VatRecord],
  payeRecord: Option[PayeRecord],
  companiesHouse: Option[CompaniesHouseRecord]
)

case class BusinessIndex(
  id: Int,
  name: String,
  uprn: String,
  industryCode: String,
  legalStatus: String,
  tradingStatus: String,
  turnover: String,
  employmentBand: String
) {

  val Delim = ","
  def toCsv =
    s"""$id $Delim "$name" $Delim $uprn $Delim $industryCode $Delim $legalStatus $Delim "$tradingStatus" $Delim "$turnover" $Delim "$employmentBand" """

}
