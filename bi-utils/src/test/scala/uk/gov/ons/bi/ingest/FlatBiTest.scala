package uk.gov.ons.bi.ingest

import com.typesafe.config.ConfigFactory
import org.scalatest.FlatSpec

/**
  * Created by Volodymyr.Glushak on 15/02/2017.
  */
trait FlatBiTest extends FlatSpec {

  implicit val config = ConfigFactory.load().getConfig("env.test")

}
