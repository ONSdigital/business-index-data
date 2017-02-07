package uk.gov.ons.bi.ingest.parsers

trait OffsetParser[T] extends RecordParser[T] {

  def parser: OffsetFormat

  override type SourceType = Map[String, String]
}