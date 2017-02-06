package uk.gov.ons.bi.ingest.models

case class Address(
  line_1: String,
  line_2: String,
  line_3: String,
  line_4: String,
  line_5: String,
  postcode: String
)

case class PayeRecord(
  emp_stats_rec_id: String,
  district_number: Int,
  employer_reference: Int,
  accounts_office_reference: Int,
  address: Address,
  trading_as_name_1: String,
  trading_as_name_2: String,
  communication_name: String,
  communication_address: Address,
  legal_status: String,
  scheme_type: String,
  date_commenced: DateTime,
  transfer_in_identifier: String,
  successor_in_identifier: String,
  date_ceased: DateTime,
  transfer_out_identifier: String,
  merger_out_identifier: String,
  succession_out_identifier: String,
  date_of_transfer: DateTime,
  scheme_cancelled_date: DateTime,
  scheme_reopened_date: DateTime
) {
  def trade_classification_number: String = legal_status
}

object PayeRecord {
  implicit object PayeRecordDelimiter extends OffsetProvider[PayeRecord] {
    override def parser: OffsetParser = OffsetParser(
      "emp_stats_rec_id" offset 1 --> 3,
      "district_number" offset 4 --> 12,
      "employer_reference" offset 14 --> 17,
      "accounts_office_reference" offset 18 --> 23,
      "address_line_1" offset 24 --> 29,
      "address_line_2" offset 30 --> 30,
      "address_line_3" offset 30 --> 30,
      "address_line_4" offset 30 --> 30,
      "address_line_5" offset 30 --> 30,
      "postcode" offset 30 --> 30,
      "trading_as_name_1" offset 31 --> 36,
      "trading_as_name_2" offset 37 --> 42,
      "communication_name" offset 43 --> 43,
      "communication_address_line_1" offset 24 --> 29,
      "communication_address_line_2" offset 30 --> 30,
      "communication_address_line_3" offset 30 --> 30,
      "communication_address_line_4" offset 30 --> 30,
      "communication_address_line_5" offset 30 --> 30,
      "communication_postcode" offset 30 --> 30,
      "legal_status" offset 44 --> 49,
      "scheme_type" offset 50 --> 50,
      "date_commenced" offset 51 --> 51,
      "transfer_in_identifier" offset 52 --> 52,
      "successor_in_identifier" offset 53 --> 53,
      "date_ceased" offset 54 --> 54,
      "transfer_out_identifier" offset 56 --> 56,
      "merger_out_identifier" offset 57 --> 57,
      "succession_out_identifier" offset 58 --> 66,
      "date_of_transfer" offset 67 --> 84,
      "scheme_cancelled_date" offset 85 --> 189,
      "scheme_reopened_date" offset 190 --> 219
    )
  }
}
