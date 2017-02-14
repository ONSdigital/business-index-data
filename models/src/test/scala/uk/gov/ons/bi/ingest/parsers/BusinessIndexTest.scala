package uk.gov.ons.bi.ingest.parsers

import org.scalatest.FlatSpec
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.ingest.process.BusinessLinker

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
class BusinessIndexTest extends FlatSpec {

  "From input data" should "proper business data to be created" in {

    val busObjs = new BusinessLinker().transformAndLink(
      getResource("/VAT_Output.csv"),
      getResource("/PAYE_Output.csv"),
      getResource("/CH_Output.csv"),
      getResource("/links.json").mkString("\n")
    )

    busObjs.foreach(x => {
      assert(x.id > 0, "Id populated")
      assert(x.name.nonEmpty, "Non empty company name")
    })

  }
}
