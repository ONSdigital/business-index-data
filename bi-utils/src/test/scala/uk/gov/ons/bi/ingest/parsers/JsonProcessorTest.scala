package uk.gov.ons.bi.ingest.parsers

import org.scalatest.FlatSpec
import uk.gov.ons.bi.ingest.helper.Utils._

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
class JsonProcessorTest extends FlatSpec {

  "Map file" should "create proper Map object" in {
    val file = getResource("/links.json").mkString("\n")
    assert(LinkedFileParser.parse(file).size == 20)
  }

}
