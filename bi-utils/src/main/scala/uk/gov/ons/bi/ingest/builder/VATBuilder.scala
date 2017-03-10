package uk.gov.ons.bi.ingest.builder

import com.typesafe.config.Config
import uk.gov.ons.bi.models.VatRecord

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
object VATBuilder {

  def vatFromMap(map: Map[String, String])(implicit config: Config) = new VATBuilder(map).build

}

class VATBuilder(val map: Map[String, String])(implicit val config: Config) extends RecordBuilder[VatRecord] {

  override def build = handled {
    VatRecord(
      entref = map("entref"),
      vatref = map("vatref"),
      deathcode = map("deathcode"),
      birthdate = map("birthdate"),
      deathdate = map("deathdate"),
      sic92 = map("sic92"),
      turnover = map("turnover"),
      turnoverDate = map("turnover_date"),
      recordType = map("record_type"),
      legalstatus = map("legalstatus"),
      actiondate = map("actiondate"),
      crn = map("crn"),
      marker = map("marker"),
      addressref = map("addressref"),
      inqcode = map("inqcode"),
      name = multiLineNameFromMap,
      tradStyle = tradStyleFromMap,
      address = addressFromMap
    )
  }
}
