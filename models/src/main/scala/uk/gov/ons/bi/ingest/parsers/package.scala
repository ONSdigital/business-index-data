package uk.gov.ons.bi.ingest

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import com.outworkers.util.catsparsers.Parser
import com.outworkers.util.validators.dsl.Nel

package object parsers {

  implicit object StringParser extends Parser[String] {
    override def parse(str: String): ValidatedNel[String, String] = Valid(str)
  }

  implicit class NumDsl(val num: Int) extends AnyVal {
    def -->(end: Int): OffsetDelimiter = OffsetDelimiter(num, end)
  }

  implicit class StringAugmenter(val str: String) extends AnyVal {
    def offset(num: OffsetDelimiter): (String, OffsetDelimiter) = str -> num
  }

  implicit class NelAugmenter[T](val nel: Nel[T]) extends AnyVal {
    def prop(key: String): Nel[T] = nel
  }

  /**
    * Converts a tuple that we obtained from a validation chain to a case class
    * if the type of the parameters in the tuple match the types of the fields
    * of the case class in the same order.
    *
    * Example: {{{
    *   case class Record(num: Int, text: String, date: DateTime)
    *   val x: (Int, String, DateTime) = Tuple3(5, "text", new DateTime)
    *
    *   val generic = implicitly[Generic.Aux[Record, (Int, String, DateTime)]]
    *   var record: Record = gen to x
    *   record == Record(5, "text", new DateTime)
    * }}}
    * @param obj The tuple object to convert to a case class instance.
    * @tparam T The type of the object to augment with the as method.
    */
  implicit class TpAs[T](val obj: T) extends AnyVal {
    //def as[Caster <: Product](implicit gen: TupleGeneric.Aux[Caster, T]) = gen from obj
  }

  implicit class TraversableOps[T](val col: Seq[T]) extends AnyVal {
    def getIndex(index: Int): Option[T] = if (col.isDefinedAt(index)) Some(col(index)) else None
  }


}
