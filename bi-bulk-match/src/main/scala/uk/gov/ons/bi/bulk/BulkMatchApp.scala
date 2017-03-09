package uk.gov.ons.bi.bulk

import java.nio.file.Paths
import java.util.concurrent.LinkedBlockingQueue

import com.typesafe.config.ConfigFactory
import uk.gov.ons.bi.writers.BiConfigManager

import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by Volodymyr.Glushak on 09/03/2017.
  */
object BulkMatchApp extends App {

  // create FileMonitor
  // & go through all files in folder
  // populate queue
  // create BulkMatchProcessor with specific queue


  implicit val config = BiConfigManager.envConf(ConfigFactory.load())

  val bulkConfig = BulkConfig(
    config.getInt("max.minutes.per.file"),
    config.getInt("max.parallel.requests"),
    config.getString("bi.api.url"),
    config.getString("bi.in.folder"),
    config.getString("bi.out.folder")
  )

  val fileName = "Isample.csv"

  val queue = new LinkedBlockingQueue[String]

  val processor = new BulkMatchProcessor(bulkConfig, queue)

  val future = processor.run()

  FolderScanner.findFiles(Paths.get(bulkConfig.inFolder), "csv").foreach(file =>
    queue.put(file)
  )

  // queue.put(fileName)

  new FileAddedMonitor(bulkConfig.inFolder, "csv", true, queue)

  Await.result(future, 365 days) // restart once a year

}
