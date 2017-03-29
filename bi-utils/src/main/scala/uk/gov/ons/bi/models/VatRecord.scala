package uk.gov.ons.bi.models

case class VatRecord(
                      entref: String,
                      vatref: String,
                      deathcode: String,
                      birthdate: String,
                      deathdate: String,
                      sic92: String,
                      turnover: String,
                      turnoverDate: String,
                      recordType: String,
                      legalstatus: String,
                      actiondate: String,
                      crn: String,
                      marker: String,
                      addressref: String,
                      inqcode: String,
                      name: PayeName,
                      tradStyle: TradStyle,
                      address: Address
)