package uk.gov.ons.bi.bulk

import java.io.File
import java.util.concurrent.LinkedBlockingQueue

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import uk.gov.ons.bi.ingest.helper.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by Volodymyr.Glushak on 10/03/2017.
  */
class FileAddedMonitorTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  private[this] val tmpDir = System.getProperty("java.io.tmpdir")

  private[this] val workDir = tmpDir + s"/bi${System.currentTimeMillis()}"
  new File(workDir + "/dd").mkdirs()

  override protected def afterAll(): Unit = {
    FileUtils.deleteDirectory(new File(workDir))
  }

  "New files" should "be found by listener" in {
    val queue = new LinkedBlockingQueue[String]

    val files = List("/file1.txt", "/dd/file2.txt", "/dd/file3.txt")
    new FileAddedMonitor(workDir, "txt", queue)
    files.foreach(create)

    val check = Future {
      (0 to 2).map(_ => queue.take()).toList shouldBe files
    }

    Await.result(check, 30 seconds)
  }

  private[this] def create(x: String) = Utils.writeToFile(workDir + "/" + x, "Nil")

}
