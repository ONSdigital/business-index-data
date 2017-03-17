package uk.gov.ons.bi.models

/*

{
  "0":{"CH":["12345678","23456789"],"PAYE":[],"VAT":["123412341234"]},
  "1":{"CH":["34567890"],"PAYE":[],"VAT":["123123123123"]}
}
 */
case class LinkedRecord(ubrn: Option[String], paye: Set[String], ch: Set[String], vat: Set[String]) {
  override def toString: String = s"$ubrn - CH: $ch, PAYE - $paye, VAT - $vat"


  def dataHash: Int = ch.hashCode() + vat.hashCode() + paye.hashCode()

}


