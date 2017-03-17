package uk.gov.ons.bi.ingest.process

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.ingest.parsers.LinkedFileParser
import uk.gov.ons.bi.ingest.process.UbrnComparator.removePayeEqual
import uk.gov.ons.bi.models.LinkedRecord
/**
  * Created by Volodymyr.Glushak on 17/03/2017.
  */
class UbrnCompareTest extends FlatSpec with Matchers with UbrnCompareFunctions {

  def getResAndParse(name: String): List[LinkedRecord] = {
    LinkedFileParser.parse(getResource(name).mkString("\n"))
  }

  def toSizes(d: PairRec): (Int, Int) = (d.oldData.size, d.newData.size)

  "Files" should "compare properly" in {

    val newFile = getResAndParse("/links2.json")
    val oldFile = getResAndParse("/links.json")
    val x = PairRec(oldFile, newFile)

    toSizes(removeHashEqual(x)) should be((10, 9))

    toSizes(removeChEqual(removeHashEqual(x))) should be((9, 8))

    toSizes(removeVatEqual(removeChEqual(removeHashEqual(x)))) should be((3, 2))

    toSizes(removePayeEqual(removeVatEqual(removeChEqual(removeHashEqual(x))))) should be((2, 1))

  }

}
