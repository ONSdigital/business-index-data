package uk.gov.ons.bi.ingest.parsers

import cats.data.ValidatedNel

trait RecordParser[RecordType] {
  type SourceType

  def extract(sourceType: SourceType): ValidatedNel[String, RecordType]
}

object RecordParser {
  type Aux[Record, Source] = RecordParser[Record] { type SourceType = Source }
}

trait CsvParser[R] extends RecordParser[R] {
  override type SourceType = Seq[String]
}