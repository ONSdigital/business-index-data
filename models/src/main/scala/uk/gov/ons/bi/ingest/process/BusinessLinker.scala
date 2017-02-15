package uk.gov.ons.bi.ingest.process

import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
import uk.gov.ons.bi.ingest.models._
import uk.gov.ons.bi.ingest.parsers.CsvProcessor._
import uk.gov.ons.bi.ingest.parsers.LinkedFileParser

/**
  * Created by Volodymyr.Glushak on 09/02/2017.
  */
class BusinessLinker(implicit config: Config) {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  def buildLink(linking: DataSource[String, LinkedRecord],
                vat: DataSource[String, VatRecord],
                paye: DataSource[String, PayeRecord2],
                ch: DataSource[String, CompaniesHouseRecord]): DataSource[String, BusinessIndex] = {
    linking.map { x => {
      val compHouseRec = x.ch.flatMap(ch.getById)
      val payeRec = x.paye.flatMap(paye.getById)
      val vatRec = x.vat.flatMap(vat.getById)
      new BusinessIndexDataExtractor(BusinessData(x.ubrn, compHouseRec, vatRec, payeRec))
    }
    }.filter(x => x.cvp match {
      case BusinessData(id, Nil, Nil, Nil) =>
        logger.warn(s"There are no links for ubrn: $id. Ignoring record")
        false
      case _ => true
    }).map { extractor =>

      BusinessIndex(
        id = extractor.uprn,
        name = extractor.companyName,
        uprn = extractor.uprn,
        postCode = extractor.postCode,
        industryCode = extractor.industryCode,
        legalStatus = extractor.legalStatus,
        tradingStatus = extractor.tradingStatus,
        turnover = extractor.turnover,
        employmentBand = extractor.employment
      )
    }
  }

  def transformAndLink(vatStream: Iterator[String],
                       payeStream: Iterator[String],
                       chStream: Iterator[String],
                       linkingData: String) = {
    val vatMapList = csvToMapToObj(vatStream, VATBuilder.vatFromMap, "vat").flatten.map(vt => vt.vatref -> vt).toMap

    val payeMapList = csvToMapToObj(payeStream, PayeBuilder.payeFromMap, "paye").flatten.map(py => py.payeref -> py).toMap

    val chMapList = csvToMapToObj(chStream, CHBuilder.companyHouseFromMap, "ch").flatten.map(ch => ch.company_number -> ch).toMap

    val links = LinkedFileParser.parse(linkingData).map { lk => lk.ubrn -> lk }.toMap

    logger.info("Input data read completed. Linking data ... ")

    def asMapDS[T](map: Map[String, T]) = new MapDataSource(map)

    // invoke linker class
    // and pass CSV as DataSources
    val busObjs = new BusinessLinker().buildLink(
      asMapDS(links),
      asMapDS(vatMapList),
      asMapDS(payeMapList),
      asMapDS(chMapList)
    )
    logger.info("Linking completed.")
    busObjs
  }

}

case class BusinessData(ubrn: String, c: List[CompaniesHouseRecord], v: List[VatRecord], p: List[PayeRecord2])