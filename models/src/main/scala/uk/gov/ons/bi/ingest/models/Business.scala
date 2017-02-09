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
  payeRecord: Option[PayeRecord2],
  companiesHouse: Option[CompaniesHouseRecord]
)


// fixme: the same class exists in bi-api
case class BusinessIndex(
  id: Long,
  name: String,
  uprn: Long,
  industryCode: Long,
  legalStatus: String,
  tradingStatus: String,
  turnover: String,
  employmentBand: String
) {

  val Delim = ","
  def toCsv =
    s"""$id $Delim "$name" $Delim $uprn $Delim $industryCode $Delim $legalStatus $Delim "$tradingStatus" $Delim "$turnover" $Delim "$employmentBand" """

}
