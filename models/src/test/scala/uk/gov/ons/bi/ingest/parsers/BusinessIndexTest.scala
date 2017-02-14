package uk.gov.ons.bi.ingest.parsers

import org.scalatest.FlatSpec
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.parsers.CsvProcessor._
import uk.gov.ons.bi.ingest.process.{BusinessLinker, MapDataSource}
import uk.gov.ons.bi.ingest.helper.Utils._

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
class BusinessIndexTest extends FlatSpec {

  "From input data" should "proper business data to be created" in {

    // read all input data
    // we need all InputData to be represented as DataSource

    val chMapList = csvToMapToObj(getResource("/CH_Output.csv"), CHBuilder.companyHouseFromMap).flatten
      .map(ch => ch.company_number -> ch).toMap

    val payeMapList = csvToMapToObj(getResource("/PAYE_Output.csv"), PayeBuilder.payeFromMap).flatten
      .map(py => py.entref -> py).toMap

    val vatMapList = csvToMapToObj(getResource("/VAT_Output.csv"), VATBuilder.vatFromMap).flatten
      .map(vt => vt.entref -> vt).toMap

    val links = LinkedFileParser.parse(getResource("/links.json").mkString("\n")).head.map { lk =>
      lk.id -> lk
    }.toMap

    def asMapDS[T](map: Map[String, T]) = new MapDataSource(map)

    // invoke linker class
    // and pass CSV as DataSources
    val busObjs = new BusinessLinker().buildLink(
      asMapDS(links),
      asMapDS(vatMapList),
      asMapDS(payeMapList),
      asMapDS(chMapList)
    )
    busObjs.foreach(x => {
      assert(x.id > 0, "Id populated")
      assert(x.name.nonEmpty, "Non empty company name")
    })
  }
}
