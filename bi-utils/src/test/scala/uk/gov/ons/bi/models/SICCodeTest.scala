package uk.gov.ons.bi.models

import org.scalatest.FlatSpec

/**
  * Created by Volodymyr.Glushak on 14/02/2017.
  */
class SICCodeTest extends FlatSpec {

  "SICCode" should "properly be extracted" in {
    val default = SICCode("", "", "", "")

    assert(default.sicCodeNum.isEmpty)
    assert(default.copy(sicText1 = "sometext").sicCodeNum.isEmpty)
    assert(default.copy(sicText1 = "230021 - industry of fun").sicCodeNum.contains("230021"))
  }


}
