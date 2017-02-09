package uk.gov.ons.bi.ingest.models

import scala.collection.generic.CanBuildFrom

/*

{
  "0":{"CH":["12345678","23456789"],"PAYE":[],"VAT":["123412341234"]},
  "1":{"CH":["34567890"],"PAYE":[],"VAT":["123123123123"]}
}
 */

case class LinkedRecord(id: String, paye: List[String], ch: List[String], vat: List[String]) {
  override def toString: String = s"$id - CH: $ch, PAYE - $paye, VAT - $vat"
}


object LinkedRecord {

  def apply[M[X] <: TraversableOnce[X]](col: M[(String, InnerLink)])(
    implicit cbf: CanBuildFrom[M[(String, InnerLink)], LinkedRecord, M[LinkedRecord]]
  ): M[LinkedRecord] = {
    val builder = cbf()

    for ((id, rec) <- col) builder += LinkedRecord(id, rec.paye, rec.ch, rec.vat)

    builder.result()
  }
}

case class InnerLink(
  paye: List[String],
  ch: List[String],
  vat: List[String]
)


