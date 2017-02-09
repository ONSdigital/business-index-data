package uk.gov.ons.bi.ingest.parsers

import org.scalatest.FlatSpec
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.helpers.IOHelper._
import uk.gov.ons.bi.ingest.parsers.CsvProcessor._
import uk.gov.ons.bi.ingest.process.{CompanyLinker, MapDataSource}

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
class BusinessIndexTest extends FlatSpec {


  "From input data" should "proper business data to be created" in {


    // read all input data
    // we need all InputData to be represented as DataSource

    val chMapList = csvToMap(readFile("/CH_Output.csv")).map(chCmp =>
      CHBuilder.companyHouseFromMap(chCmp)
    ).map(ch => ch.company_number -> ch).toMap

    val payeMapList = csvToMap(readFile("/PAYE_Output.csv")).map(payeRec =>
      PayeBuilder.payeFromMap(payeRec)
    ).map(py => py.entref -> py).toMap

    val vatMapList = csvToMap(readFile("/VAT_Output.csv")).map(vatRec =>
      VATBuilder.vatFromMap(vatRec)
    ).map(vt => vt.entref -> vt).toMap

    val links = LinkedFileParser.parse(readFile("/links.json").mkString("\n")).head.map { lk =>
      lk.id -> lk
    }.toMap

    def asMapDS[T](map: Map[String, T]) = new MapDataSource(map)

    // invoke linker class
    // and pass CSV as DataSources
    val busObjs = new CompanyLinker().buildLink(
      asMapDS(links),
      asMapDS(vatMapList),
      asMapDS(payeMapList),
      asMapDS(chMapList)
    )

    busObjs.foreach(x => println(x.toCsv))

  }

}
