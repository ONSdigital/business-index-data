package uk.gov.ons.bi.ingest

import shapeless.Generic

import scala.collection.{GenSeqLike, TraversableOnce}


package object parsers {


  implicit class NumDsl(val num: Int) extends AnyVal {
    def -->(end: Int): OffsetDelimiter = OffsetDelimiter(num, end)
  }

  implicit class StringAugmenter(val str: String) extends AnyVal {
    def offset(num: OffsetDelimiter): (String, OffsetDelimiter) = str -> num
  }

  implicit class TpAs[T](val obj: T) extends AnyVal {
    def as[Caster](implicit gen: Generic.Aux[T, Caster]) = gen to obj
  }

  implicit class TraversableOps[T](val col: Seq[T]) extends AnyVal {
    def value(index: Int): Option[T] = if (col.isDefinedAt(index)) Some(col(index)) else None
  }
}
