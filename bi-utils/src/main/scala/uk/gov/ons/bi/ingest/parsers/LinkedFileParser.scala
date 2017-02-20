package uk.gov.ons.bi.ingest.parsers

import org.json4s._
import org.json4s.native.JsonMethods
import uk.gov.ons.bi.models.LinkedRecord

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
object LinkedFileParser {


  /**
    * Method parse JSON string and produce list of following Map:
    * Map(UBRN -> 12345, VAT -> List(681735974661), PAYE -> List(4428762), CH -> List(3246035))
    *
    * @param string
    * @return
    */
  def parse(string: String) = {

    def links(name: String, map: Map[String, Any]): List[String] = map(name) match {
      case r: List[String] => r
    }

    val json = JsonMethods.parse(string)

    val jsonExtractable = json transformField {
      case ("UBRN", x) => ("ubrn", x)
      case ("PAYE", x) => ("paye", x)
      case ("VAT", x) => ("vat", x)
      case ("CH", x) => ("ch", x)
    }

    implicit val formats = DefaultFormats

    jsonExtractable.extract[List[LinkedRecord]]
  }

}
