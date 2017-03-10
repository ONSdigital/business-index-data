package uk.gov.ons.bi.bulk

import java.io.{File, FileFilter}
import java.nio.file.StandardWatchEventKinds._
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.BlockingQueue

import org.slf4j.LoggerFactory
import uk.gov.ons.bi.bulk.FolderScanner._

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Watch for files with specific extention in folder and its subfolders.
  * Found files should be placed in queue (excluding full rootPath).
  * Created by Volodymyr.Glushak on 09/03/2017.
  */
class FileAddedMonitor(rootFolder: String,
                       extension: String,
                       queue: BlockingQueue[String]) {


  private[this] val logger = LoggerFactory.getLogger(getClass)
  private[this] val rootPath = Paths.get(rootFolder)

  private[this] val watcher = FileSystems.getDefault.newWatchService

  Files.walkFileTree(rootPath, (f: Path) => {
    if (f.toFile.isDirectory) {
      f.register(watcher, ENTRY_CREATE) // interested only in added items
      logger.info(s"Registered watcher for $f")
    }
  })

  def watch() {
    while (true) {
      val watchKey = watcher.take()
      val dir = watchKey.watchable() match { case p: Path => p }
      watchKey.pollEvents().asScala.foreach { ev =>
        ev.kind() match {
          case ENTRY_CREATE => ev.context() match {
            case p: Path =>
              val path = dir.resolve(p).toString.replace(rootPath.toString, "") // remove root rootPath
              queue.add(path)
              logger.info(s"File $path has been added.")
          }
          case z => logger.warn(s"Got unexpected event type $z for ${ev.context()}")
        }
      }
      if (!watchKey.reset()) {
        val msg = "Unable to reset watch key. Need to restart file monitor!"
        logger.error(msg)
        watchKey.cancel()
        watcher.close()
        sys.error(msg)
      }
    }
  }

  Future {
    watch()
  }

}


object FolderScanner {

  /**
    * Makes it easier to walk a file tree
    */
  implicit def makeDirVisitor[T](f: (Path) => T): SimpleFileVisitor[Path] = new SimpleFileVisitor[Path] {
    override def preVisitDirectory(p: Path, attrs: BasicFileAttributes): FileVisitResult = {
      f(p)
      FileVisitResult.CONTINUE
    }
  }

  private[this] val logger = LoggerFactory.getLogger(getClass)

  // TODO: get rid of this path logic - switch to work with Path object
  def findFiles(path: Path, extension: String): List[String] = {
    logger.info(s"Looking for files in $path")
    val pathes = ListBuffer.empty[String]
    val pathLen = path.toFile.getAbsolutePath.length
    val res = Files.walkFileTree(path, (f: Path) => {
      logger.info(s"Found directory: $f")
      val files = f.toFile.listFiles(new FileFilter {
        override def accept(pathname: File): Boolean = pathname.getName.endsWith(extension)
      })
      pathes.++=(files.map(_.getAbsolutePath.substring(pathLen)))
    })
    pathes.toList
  }
}