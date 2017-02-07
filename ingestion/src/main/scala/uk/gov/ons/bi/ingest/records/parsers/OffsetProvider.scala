package uk.gov.ons.bi.ingest.records.parsers

trait OffsetProvider[T] extends RecordParser[T] {

  def parser: OffsetParser

  override type SourceType = Map[String, String]
}