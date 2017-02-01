package uk.gov.ons.bi.ingest.records.parsers

trait OffsetProvider[T] {
  def parser: OffsetParser
}
