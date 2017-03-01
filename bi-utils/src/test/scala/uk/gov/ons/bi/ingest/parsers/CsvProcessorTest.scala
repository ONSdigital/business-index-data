package uk.gov.ons.bi.ingest.parsers

import uk.gov.ons.bi.ingest.FlatBiTest
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.helper.Utils
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.models.BusinessIndexRec

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
class CsvProcessorTest extends FlatBiTest {

  import CsvProcessor._

  val files = Map("/CH_original.csv" -> 199, "/PAYE_original.csv" -> 6,
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
    data.size shouldBe 199
  }

  "PAYE information" should "be created from map" in {
    val paye = getResource("/PAYE_Output.csv")
    val payeMapList = csvToMapToObj(paye, PayeBuilder.payeFromMap, "paye")
    payeMapList.flatten.toSeq.size shouldBe 100
  }

  "VAT information" should "be created from map" in {
    val vat = getResource("/VAT_Output.csv")
    val vatMapList = csvToMapToObj(vat, VATBuilder.vatFromMap, "vat")
    vatMapList.flatten.toSeq.size shouldBe 100
  }

  "Original PAYE information" should "be correctly parsed" in {
    val paye = getResource("/PAYE_original.csv")
    val payeMapList = csvToMapToObj(paye, PayeBuilder.payeFromMap, "paye")
    payeMapList.flatten.toSeq.size shouldBe 6
  }


  private[this] val csvToParse =
    """ID,BusinessName,UPRN,IndustryCode,LegalStatus,TradingStatus,Turnover,EmploymentBands,PostCode,VatRefs,PayeRefs
      |21840175,ACCLAIMED HOMES LIMITED,951638,50742,3,A,C,H,SE,10000,20000
      |28919372,5TH PROPERTY TRADING LIMITED,9424,90481,3,C,B,N,SE,10001,20002
    """.stripMargin

  "Sample CSV" should "be parsed" in {

    val itr = csvToParse.split("\n").toIterator
    CsvProcessor.csvToMap(itr).map { r =>
      BusinessIndexRec.fromMap(r("ID").toLong, r)
    }.size shouldBe 2
  }


//    // Performance test ...
//    "ALL CH" should "be read and parsed" in {
//      val ch = readFile("/Users/Volodymyr.Glushak/Downloads/BasicCompanyData-2017-02-03-part1_5.csv")
//      assert(csvToMapToObj(ch, CHBuilder.companyHouseFromMap).length == 850000 - 1)
//    }
}
