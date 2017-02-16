//package uk.gov.ons.bi.ingest
//
//import org.apache.spark.{SparkConf, SparkContext}
//import uk.gov.ons.bi.ingest.builder.{CHBuilder, PayeBuilder, VATBuilder}
//import uk.gov.ons.bi.ingest.parsers.{CsvProcessor, LinkedFileParser}
//
//
//object SparkApp extends App {
//
//
//  // rewrite to DataFrame structure
//  def fileAsMapRDD(filename: String) = {
//    val rdd = spark.textFile(filename)
//    import CsvProcessor._
//    val header = rdd.take(1).head.split(Delimiter)
//    val data = rdd.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
//    data.map { d => header zip d.split(Delimiter) toMap }
//  }
//
//  val conf = new SparkConf().setAppName("Spark Pi")
//  val spark = new SparkContext(conf)
//
//  val chs = fileAsMapRDD("ch.file").map(CHBuilder.companyHouseFromMap)
//
//  val payeMapList = fileAsMapRDD("paye.file").map(PayeBuilder.payeFromMap).map(py => py.entref -> py)
//
//  val vatMapList = fileAsMapRDD("vat.file").map(VATBuilder.vatFromMap).map(vt => vt.entref -> vt)
//
//
//  // TODO: build json in memory, incorrect approach - cause of custom parser ...
//  val links = LinkedFileParser.parse(spark.textFile("linked.file").collect().mkString).head.map { lk =>
//    lk.id -> lk
//  }.toMap
//
//
//  chs.groupBy { ch => ch.company_name
//  }
//
//  spark.stop()
//
//}
