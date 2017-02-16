package uk.gov.ons.bi.models

import org.scalatest.FlatSpec

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */
class SICCodeTest extends FlatSpec {

  "SICCode" should "properly be extracted" in {
    val default = SICCode("", "", "", "")

    assert(default.sicCodeNum == 0)
    assert(default.copy(sic_text_1 = "sometext").sicCodeNum == 0)
    assert(default.copy(sic_text_1 = "230021 - industry of fun").sicCodeNum == 230021)
  }


}
