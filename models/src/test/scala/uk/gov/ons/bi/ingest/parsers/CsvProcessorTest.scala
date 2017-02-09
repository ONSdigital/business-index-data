package uk.gov.ons.bi.ingest.parsers

import org.scalatest.FlatSpec
import uk.gov.ons.bi.ingest.helpers.IOHelper
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
class CsvProcessorTest extends FlatSpec {

  import CsvProcessor._
  import IOHelper._

  val files = Map("/CH_Output.csv" -> 100, "/PAYE_Output.csv" -> 100, "/VAT_Output.csv" -> 100)


  files.foreach { case (file, sz) =>
    s"$file test file" should "transform to map properly" in {
      val data = readFile(file)
      assert(csvToMap(data).size == sz, s"$file size is invalid")
    }
  }


  "Company house" should "be created from map" in {
    val ch = readFile("/CH_Output.csv")
    val chMapList = csvToMap(ch)
    chMapList.foreach(chCmp =>
      CHBuilder.companyHouseFromMap(chCmp)
    )
  }

  "PAYE information" should "be created from map" in {
    val paye = readFile("/PAYE_Output.csv")
    val payeMapList = csvToMap(paye)
    payeMapList.foreach(payeRec =>
      PayeBuilder.payeFromMap(payeRec)
    )
  }

  "VAT information" should "be created from map" in {
    val vat = readFile("/VAT_Output.csv")
    val vatMapList = csvToMap(vat)
    vatMapList.foreach(vatRec =>
      VATBuilder.vatFromMap(vatRec)
    )
  }
}
