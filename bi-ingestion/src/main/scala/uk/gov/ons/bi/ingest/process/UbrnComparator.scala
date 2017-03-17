package uk.gov.ons.bi.ingest.process

import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.ingest.parsers.LinkedFileParser

/**
  * Program to compare two links files and shows difference
  * Created by Volodymyr.Glushak on 16/03/2017.
  *
  * Rules.
  * 1. Find the same by content - remove (the same UBRN)
  * 2. Find the same by company house - remove
  * 3. Find CH in old without pairs in new
  * 3.1. Find all VAT or PAYE records in NEW for them - if found remove
  * 3.2. Others disappeared
  * 4. Find linked by VAT (if 0/1 to 1 relationship) - remove
  * 4.1. If relationship is Many to 1 - assign UBRN for 1, lost other UBRNs
  * 4.2. If relationship is 1 to Many - assign UBRN to 1, create UBRN for others
  * 5. Repeate 4 for PAYE
  * 6. For leftovers create new UBRNs.
  *
  */
object UbrnComparator extends App with UbrnCompareFunctions {


  private[this] def getProp(name: String) = sys.props.getOrElse(name, sys.error(s"Can not find mandatory property $name"))

  private[this] def readAndProcess(name: String) =
    LinkedFileParser.parse(readFile(getProp(name)).mkString("\n"))


  private[this] val oldFile = readAndProcess("oldFile")
  private[this] val newFile = readAndProcess("newFile")
  logger.debug(s"Read old file. ${stats(oldFile)}")
  logger.debug(s"Read new file. ${stats(newFile)}")


  removePayeEqual(
    removeVatEqual(
      removeChEqual(
        removeHashEqual(
          PairRec(oldFile, newFile)
        )
      )
    )
  )
}
