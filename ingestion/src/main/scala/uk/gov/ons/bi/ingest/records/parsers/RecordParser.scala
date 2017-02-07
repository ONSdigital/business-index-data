package uk.gov.ons.bi.ingest.records.parsers

import cats.data.ValidatedNel

trait RecordParser[RecordType] {
  type SourceType

  def parse(sourceType: SourceType): ValidatedNel[String, RecordType]
}

object RecordParser {
  type Aux[Record, Source] = RecordParser[Record] { type SourceType = Source }
}