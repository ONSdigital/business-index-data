package uk.gov.ons.bi.ingest.helpers

import scala.io.Source

/**
  * Created by Volodymyr.Glushak on 08/02/2017.
  */
object IOHelper {

  def readFile(file: String) = Source.fromInputStream(getClass.getResourceAsStream(file)).getLines().toSeq

}
