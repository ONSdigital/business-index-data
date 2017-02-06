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

