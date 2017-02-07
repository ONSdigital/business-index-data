package uk.gov.ons.bi.ingest.parsers

case class OffsetFormat(
  config: Map[String, OffsetDelimiter]
) {

  /**
    * Chops a string up based on an input configuration that delimits fields.
    * @param source The source string to parse, seen s one ine in a input CSV.
    * @return A Map where the keys are the field names and the value is the value of the specific key in the input Csv.
    */
  def parse(source: String): Map[String, String] = {
    config.foldLeft(Map.newBuilder[String, String]) {
      case (acc, (key, offset)) => acc += key -> source.slice(offset.start, offset.start + offset.end)
    } result()
  }
}

object OffsetFormat {
  def apply(objects: (String, OffsetDelimiter)*): OffsetFormat = {
    OffsetFormat(config = objects.toMap)
  }
}
