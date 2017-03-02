package uk.gov.ons.bi.ingest

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Volodymyr.Glushak on 15/02/2017.
  */
trait FlatBiTest extends FlatSpec with Matchers {

  implicit val config: Config = ConfigFactory.load().getConfig("env.test")

}
