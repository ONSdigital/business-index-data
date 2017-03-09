package uk.gov.ons.bi.bulk

import com.typesafe.config.Config

case class BulkConfig(maxMinutesPerFile: Int, maxConcurrentReq: Int, biUrl: String, inFolder: String, outFolder: String, cfg: Config)

case class BulkType(field: String, responsesPerQuery: Option[Int], responsesPerFile: Option[Int])

object BulkConsts {

  val SearchHeaders = List(
    BulkType("BusinessName", Some(3), Some(200000)),
    BulkType("IndustryCode", None, Some(200000)),
    BulkType("VatRefs", Some(1), Some(200000)),
    BulkType("PayeRefs", Some(1), Some(200000)),
    BulkType("PostCode", None, Some(200000))
  )

}
