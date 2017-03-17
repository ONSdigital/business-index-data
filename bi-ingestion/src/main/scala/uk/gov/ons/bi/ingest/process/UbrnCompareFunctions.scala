package uk.gov.ons.bi.ingest.process

import org.slf4j.{Logger, LoggerFactory}
import uk.gov.ons.bi.models.LinkedRecord

/**
  * Created by Volodymyr.Glushak on 17/03/2017.
  */


trait UbrnCompareFunctions {


  protected def printSample[X](list: List[X], amount: Int = 3): Unit = {
    if (amount > 0 && list.nonEmpty) {
      logger.debug(s"SAMPLE: ${list.head.toString}")
      printSample(list.tail, amount - 1)
    }
  }

  case class PairRec(oldData: List[LinkedRecord], newData: List[LinkedRecord]) {
    def pairStats = s"\n[OLD: ${stats(oldData)}]\n[NEW: ${stats(newData)}]"
  }

  protected val logger: Logger = LoggerFactory.getLogger(getClass)

  protected def stats(list: List[LinkedRecord]): String = {
    val total = list.size
    val noCh = list.count(_.ch.isEmpty)
    val withVatOnly = list.count(r => r.ch.isEmpty && r.paye.isEmpty)
    val withPayeOnly = list.count(r => r.ch.isEmpty && r.vat.isEmpty)
    s"TOTAL: $total, no CH: $noCh, VAT only: $withVatOnly, PAYE only: $withPayeOnly"
  }

  def removeHashEqual(d: PairRec): PairRec = {
    val oldFileHashMapped = d.oldData.map(r => r.dataHash -> r).toMap

    val newNonEq = d.newData.filter(rec => !oldFileHashMapped.contains(rec.dataHash))
    val newEq = d.newData.filterNot(newNonEq.toSet).map(r => r.dataHash -> r).toMap
    val oldNonEq = d.oldData.filter(rec => !newEq.contains(rec.dataHash))
    val res = PairRec(oldNonEq, newNonEq)
    logger.debug(s"[SAME UBRN] Records in new File hash-equaled ${newEq.size}. ${stats(newEq.values.toList)}")
    logger.debug(s"After hash-check left. ${res.pairStats}")
    res
  }


  def removeChEqual(d: PairRec): PairRec = {
    val oldFileChMapped = d.oldData.map(r => r.ch -> r).toMap

    val newNonEq = d.newData.filter(rec => rec.ch.isEmpty || !oldFileChMapped.contains(rec.ch))
    val newEq = d.newData.filterNot(newNonEq.toSet).map(r => r.ch -> r).toMap
    val oldNonEq = d.oldData.filter(rec => !newEq.contains(rec.ch))
    val res = PairRec(oldNonEq, newNonEq)
    logger.debug(s"[SAME UBRN] Records in new File CH-equaled ${newEq.size}. ${stats(newEq.values.toList)}")
    logger.debug(s"After CH-check left. ${res.pairStats}")
    res
  }

  /**
    * We should skip cases 1 to Many, Many to 1 or Many to Many as those are more complicated scenarios that requires
    * extra analysis
    * 0 to 1 case requires extra analysis too
    */
  def removeVatEqual(d: PairRec): PairRec = {
    val oldFileVatMapped = d.oldData.flatMap(r => r.vat.map(v => v -> r)).toMap
    val newEq = d.newData.map(
      r => r -> r.vat.flatMap(newVat => {
        oldFileVatMapped.get(newVat)
      })
    ).toMap

    val newOneLinkToOne = newEq.filter(_._2.size == 1)
    val oldOneToOneLinked = newOneLinkToOne.values.flatten.toSet
    printSample(newOneLinkToOne.keys.toList)
    printSample(oldOneToOneLinked.toList)
    logger.debug(s"[SAME UBRN] Records with the same VATs. NEW: ${stats(newOneLinkToOne.keys.toList)}, OLD: ${stats(oldOneToOneLinked.toList)}")
    val res = PairRec(d.oldData.filterNot(oldOneToOneLinked), d.newData.filterNot(newOneLinkToOne.keys.toSet))
    logger.debug(s"After VAT-VAT check left. ${res.pairStats}")
    res
  }


  def removePayeEqual(d: PairRec): PairRec = {
    val oldFilePayeMapped = d.oldData.flatMap(r => r.paye.map(v => v -> r)).toMap
    val newEq = d.newData.map(
      r => r -> r.paye.flatMap(newPaye => {
        oldFilePayeMapped.get(newPaye)
      })
    ).toMap

    val newOneLinkToOne = newEq.filter(_._2.size == 1)
    val oldOneToOneLinked = newOneLinkToOne.values.flatten.toSet
    printSample(newOneLinkToOne.keys.toList)
    printSample(oldOneToOneLinked.toList)
    logger.debug(s"[SAME UBRN] Records with the same PAYEs. NEW: ${stats(newOneLinkToOne.keys.toList)}, OLD: ${stats(oldOneToOneLinked.toList)}")
    val res = PairRec(d.oldData.filterNot(oldOneToOneLinked), d.newData.filterNot(newOneLinkToOne.keys.toSet))
    logger.debug(s"After PAYE-PAYE check left. ${res.pairStats}")
    printSample(res.newData)
    printSample(res.oldData)
    res
  }

}
