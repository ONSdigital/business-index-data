package uk.gov.ons.bi.ingest.models

// fixme: the same class exists in bi-api
case class BusinessIndex(
  id: Long, // the same as uprn ?
  name: String,
  uprn: Long,
  postCode: String,
  industryCode: Long,
  legalStatus: String,
  tradingStatus: String,
  turnover: String,
  employmentBand: Int
) {

  val Delim = ","

  def toCsv =
    s"""$id$Delim"$name"$Delim$uprn$Delim$industryCode$Delim$legalStatus$Delim"$tradingStatus"$Delim"$turnover"$Delim"$employmentBand""""

}
