package uk.gov.ons.bi.ingest.process

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.ingest.parsers.LinkedFileParser
import uk.gov.ons.bi.models.LinkedRecord
/**
  * Created by Volodymyr.Glushak on 17/03/2017.
  */
class UbrnCompareTest extends FlatSpec with Matchers with UbrnCompareFunctions {

  def getResAndParse(name: String): List[LinkedRecord] = {
    LinkedFileParser.parse(getResource(name).mkString("\n"))
  }

  def toSizes(d: PairRec): (Int, Int) = (d.oldData.size, d.newData.size)

  private[this] val newFile = getResAndParse("/links2.json")
  private[this] val oldFile = getResAndParse("/links.json")
  private[this] val x = PairRec(oldFile, newFile)

  "Files" should "compare by hash properly" in {
    toSizes(removeHashEqual(x)) should be((12, 10))
  }

  "Files" should "compare by CH properly" in {
    toSizes(removeChEqual(removeHashEqual(x))) should be((11, 9))
  }

  "Files" should "compare by VAT properly" in {
    toSizes(removeVatEqual(removeChEqual(removeHashEqual(x)))) should be((5, 3))
  }

  "Files" should "compare by PAYE properly" in {
    toSizes(removePayeEqual(removeVatEqual(removeChEqual(removeHashEqual(x))))) should be((3, 1))
  }

}
