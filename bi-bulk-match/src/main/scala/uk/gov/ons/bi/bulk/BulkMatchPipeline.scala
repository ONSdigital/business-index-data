package uk.gov.ons.bi.bulk

case class BulkConfig(maxMinutesPerFile: Int, maxConcurrentReq: Int, biUrl: String, inFolder: String, outFolder: String)

object BulkConsts {

  val SearchHeaders = Map("BusinessName" -> 3, "IndustryCode" -> 0, "VatRefs" -> 1, "PayeRefs" -> 1, "PostCode" -> 0)

}
