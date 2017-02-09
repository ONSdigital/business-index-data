package uk.gov.ons.bi.ingest.parsers

import uk.gov.ons.bi.ingest.models.LinkedRecord

import scala.util.parsing.json.JSON

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
object LinkedFileParser {


  /**
    * Method parse JSON string and produce list of following Map:
    * Map(VAT -> List(681735974661), PAYE -> List(4428762), CH -> List(3246035))
    *
    * @param string
    * @return
    */
  def parse(string: String) = {

    def links(name: String, map: Map[String, Any]): List[String] = map(name) match {
      case r: List[String] => r
    }

    List(JSON.parseFull(string)).map {
      case Some(M(map)) => map.map {
        case (x, M(y)) => LinkedRecord(
          id = x,
          paye = links("PAYE", y),
          ch = links("CH", y),
          vat = links("VAT", y)
        )
      }
    }
  }

  // helper classes to transform JSON into Map like object
  class CC[T] {
    def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
  }

  object M extends CC[Map[String, Any]]

  object L extends CC[List[Any]]

  object S extends CC[String]

  object D extends CC[Double]

  object B extends CC[Boolean]


}
