package uk.gov.ons.bi.ingest.parsers

import uk.gov.ons.bi.ingest.FlatBiTest
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.helper.Utils._

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
class CsvProcessorTest extends FlatBiTest {

  import CsvProcessor._

  val files = Map("/CH_original.csv" -> 199, "/PAYE_original.csv" -> 5,
    "/CH_Output.csv" -> 100, "/PAYE_Output.csv" -> 100, "/VAT_Output.csv" -> 100)


  files.foreach { case (file, sz) =>
    s"$file test file" should "transform to map properly" in {
      val data = getResource(file)
      assert(csvToMap(data).size == sz, s"$file size is invalid")
    }
  }

  "Company house" should "be created from map" in {
    val ch = getResource("/CH_original.csv")
    val chMapList = csvToMapToObj(ch, CHBuilder.companyHouseFromMap, "ch")
    val data = chMapList.flatten.toSeq
    assert(data.size == 199)
  }

  "PAYE information" should "be created from map" in {
    val paye = getResource("/PAYE_Output.csv")
    val payeMapList = csvToMapToObj(paye, PayeBuilder.payeFromMap, "paye")
    assert(payeMapList.flatten.toSeq.size == 100)
  }

  "VAT information" should "be created from map" in {
    val vat = getResource("/VAT_Output.csv")
    val vatMapList = csvToMapToObj(vat, VATBuilder.vatFromMap, "vat")
    assert(vatMapList.flatten.toSeq.size == 100)
  }

  "Original PAYE information" should "be correctly parsed" in {
    val paye = getResource("/PAYE_Original.csv")
    val payeMapList = csvToMapToObj(paye, PayeBuilder.payeFromMap, "paye")
    assert(payeMapList.flatten.toSeq.size == 5)
  }

    // Performance test ...
    "ALL CH" should "be read and parsed" in {
      val ch = readFile("/Users/Volodymyr.Glushak/Downloads/BasicCompanyData-2017-02-03-part1_5.csv")
      assert(csvToMapToObj(ch, CHBuilder.companyHouseFromMap).length == 850000 - 1)
    }
}
