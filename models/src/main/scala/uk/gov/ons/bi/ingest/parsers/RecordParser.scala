package uk.gov.ons.bi.ingest.parsers

import cats.data.Validated.{Invalid, Valid}
import com.outworkers.util.catsparsers.Parser
import com.outworkers.util.validators.dsl.Nel

trait RecordParser[RecordType] {
  type SourceType

  def extract(source: SourceType): Nel[RecordType]

  def extractOpt(source: SourceType): Nel[Option[RecordType]] = extract(source) match {
    case Invalid(nel) => Valid(Option.empty[RecordType])
    case Valid(x) => Valid(Some(x))
  }
}

object RecordParser {
  type Aux[Record, Source] = RecordParser[Record] { type SourceType = Source }
}

trait CsvParser[R] extends RecordParser[R] {
  override type SourceType = Seq[String]
}

object CsvParser {
  def apply[R]()(implicit parser: CsvParser[R]): CsvParser[R] = parser
}