package uk.gov.ons.bi.models

/*

{
  "0":{"CH":["12345678","23456789"],"PAYE":[],"VAT":["123412341234"]},
  "1":{"CH":["34567890"],"PAYE":[],"VAT":["123123123123"]}
}
 */
case class LinkedRecord(ubrn: String, paye: List[String], ch: List[String], vat: List[String]) {
  override def toString: String = s"$ubrn - CH: $ch, PAYE - $paye, VAT - $vat"
}


