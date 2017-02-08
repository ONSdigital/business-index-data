package uk.gov.ons.bi.ingest.parsers

import com.outworkers.util.catsparsers.CatsBiParser
import com.outworkers.util.validators.dsl.Nel

trait RecordParser[RecordType] {
  type SourceType

  def extract(source: SourceType): Nel[RecordType]
}

object RecordParser {
  type Aux[Record, Source] = RecordParser[Record] { type SourceType = Source }
}

trait CsvParser[R] extends RecordParser[R] with CatsBiParser[Seq[String], R] {
  override type SourceType = Seq[String]
}

object CsvParser {
  def apply[R]()(implicit parser: CsvParser[R]): CsvParser[R] = parser
}