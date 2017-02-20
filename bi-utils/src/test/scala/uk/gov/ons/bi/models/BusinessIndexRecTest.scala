package uk.gov.ons.bi.models

import org.scalatest._

/**
  * Created by Volodymyr.Glushak on 16/02/2017.
  */
class BusinessIndexRecTest extends FlatSpec with Matchers {

  "BusinessIndexRec object" should "be transformed from/to map properly" in {

    val bi = BusinessIndexRec(10L, "A", 9L, "B", 8L, "C", "D", "E", "F")

    val res = BusinessIndexRec.fromMap(10L, BusinessIndexRec.toMap(bi))

    res should be(bi)
  }

}
