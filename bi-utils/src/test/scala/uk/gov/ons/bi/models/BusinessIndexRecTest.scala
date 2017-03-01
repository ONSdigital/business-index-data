package uk.gov.ons.bi.models

import org.scalatest._

/**
  * Created by Volodymyr.Glushak on 16/02/2017.
  */
class BusinessIndexRecTest extends FlatSpec with Matchers {

  "BusinessIndexRec object" should "be transformed from/to map properly" in {

    val bi = BusinessIndexRec(10L, "A", 9L, Some("B"), Some(8L), Some("C"), Some("D"), Some("E"), Some("F"), Some(Seq(1, 2, 3)), Some(Seq("2", "5")))

    val res = BusinessIndexRec.fromMap(10L, BusinessIndexRec.toMap(bi))

    res should be(bi)
  }

}
