package uk.gov.ons.bi.bulk

import java.net.URLEncoder
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{BlockingQueue, Executors}

import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods
import org.slf4j.LoggerFactory
import uk.gov.ons.bi.bulk.BulkConsts._
import uk.gov.ons.bi.ingest.helper.Utils
import uk.gov.ons.bi.ingest.helper.Utils._
import uk.gov.ons.bi.ingest.parsers.StringHelpers
import uk.gov.ons.bi.models.BusinessIndexRec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}
import scalaj.http.Http
import scala.concurrent.duration._

/**
  * Created by Volodymyr.Glushak on 09/03/2017.
  */
class BulkMatchProcessor(val cfg: BulkConfig, queue: BlockingQueue[String]) {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  /**
    * Run bulk match processor to read messages from in queue.
    * Waiting until new messages arrives.
    *
    * @return Indefinite future
    */
  def run() = Future {
    while (true) {
      processFile(queue.take()) // this must be sync call
    }
  }

  /**
    * Main method for processing IN files
    * 1. Parse
    * 2. Execute HTTP requests for each line
    * 3. Collect results
    * 4. Create Output file
    * 5. Rename IN file
    * 6. Send email notification
    * In case of exception Err file created with error information
    *
    * @param fileName - file name
    */
  private[this] def processFile(fileName: String) = withErrHandling(fileName) {
    val startTime = System.currentTimeMillis()
    val (header, content) = parseCsv(s"${cfg.inFolder}/$fileName")
    val counter = new AtomicInteger(0)
    val futures = content.map(rec => fileFlow(header, rec, counter))
    val records = futures.size
    val futSeq = Future.sequence(futures)
    futSeq onComplete {
      case Success(res) =>
        val recGood = res.collect { case Success(s) => s }
        val recFailed = res.collect { case Failure(fa) => fa }

        val headerLine = s"$header,${BusinessIndexRec.cBiSecuredHeader}"
        writeToFile(s"${cfg.outFolder}/$fileName.OUT", (headerLine :: recGood.flatten).mkString(System.lineSeparator()))
        if (recFailed.nonEmpty) writeErrors(fileName, recFailed)
        val time = System.currentTimeMillis() - startTime
        val msg = s"File $fileName processed in $time ms, rec count: $records (including ${recFailed.size} failures)"
        logger.info(msg)
        sendEmail(fileName, msg)
      case Failure(exx) =>
        logger.error(s"Error in processing records of $fileName", exx)
        sendEmail(fileName, s"Error in processing records: ${exx.getMessage}")
        throw exx
    }
    Await.result(futSeq, cfg.maxMinutesPerFile minutes)
  }

  private[this] val ex = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(cfg.maxConcurrentReq))

  def fileFlow(header: String, rec: String, counter: AtomicInteger): Future[Try[List[String]]] = {
    if (rec.isEmpty) Future.successful(Success(Nil)) // just ignore empty lines
    else
      Future {
        val count = counter.incrementAndGet()
        if (count % 1000 == 0) logger.info(s"Processing $count records for $header search...")
        Try {performBulkMatch(header, rec)} match {
          case Success(x) if x.isSuccess => Success(parseResponse(rec, x.body))
          case Success(f) if f.code == 500 =>
            // try again if it's server error without special chars
            val response = performBulkMatch(header, trimSpecialChars(rec))
            if (response.isSuccess) {
              Success(parseResponse(rec, response.body))
            } else {
              buildFailure(rec, response.body)
            }
          case Success(z) => buildFailure(rec, z.body)
          case Failure(z) => buildFailure(rec, z.getMessage)
        }
      }(ex)
  }

  private[this] def performBulkMatch(header: String, rec: String) = {
    val reqEncoded = URLEncoder.encode(rec, "UTF-8")
    val limit = cSearchHeaders.find(_.field == header).flatMap { v =>
      v.responsesPerQuery
    }.map { x => s"?limit=$x" }.getOrElse("")
    val url = s"${cfg.biUrl}/$header:$reqEncoded$limit"
    logger.trace(s"Perform request to $url")
    val response = Http(url).asString
    logger.trace(s"Got response for $header:$rec: ${response.body}")
    response
  }

  private[this] def parseResponse(rec: String, response: String) = {
    implicit val formats = DefaultFormats
    val json = JsonMethods.parse(response)
    if (json.children.isEmpty) List()
    else {
      val recss = json.extract[List[BusinessIndexRec]]
      recss.map { rc => s"$rec,${rc.toCsvSecured}" }
    }
  }

  private[this] def trimSpecialChars(s: String) = s.replaceAll("[^a-zA-Z\\d\\s]", " ")

  private[this] def buildFailure(rec: String, err: String) = {
    val msg = s"Error returned for $rec: $err"
    logger.error(msg)
    Failure(new RuntimeException(msg))
  }

  private[this] def withErrHandling[T](fileName: String, silently: Boolean = false)(f: => T): Option[T] = {
    Try(f) match {
      case Success(s) => Some(s)
      case Failure(exx) =>
        logger.error(s"Error while processing $fileName", exx)
        if (silently) {
          writeErrors(fileName, List(exx))
          sendEmail(fileName, s"Error in processing file $fileName: ${exx.getMessage}")
        }
        None
      // TODO: send email notification about error ...
    }
  }

  private[this] def writeErrors(inFileName: String, exxs: List[Throwable]) = {
    writeToFile(s"${cfg.outFolder}/$inFileName.ERR", exxs.map(_.getMessage).mkString(System.lineSeparator()))
  }

  private[this] def parseCsv(fileName: String) = {
    logger.info(s"Parsing file $fileName")
    val header :: content = Utils.readFile(fileName).toList.map(StringHelpers.unquote)
    if (!cSearchHeaders.map(_.field).contains(header)) {
      // we need this error message to be stored in output
      val err = s"$header based search is not supported for $fileName."
      logger.error(err)
      sys.error(err)
    } else {
      (header, content)
    }
  }

  private[this] def sendEmail(file: String, msg: String) = {
    implicit val config = cfg.cfg
    if (configOverride("email.service.enabled").toBoolean)
      Try {
        val from = configOverride("email.from")
        val emailTo = configOverride("email.to")
        val agent = new MailAgent
        agent.sendMessage(s"$file processed", msg, emailTo)
      } match {
        case Success(x) => x
        case Failure(exx) =>
          logger.error("Exception while sending email", exx) // ignore exceptions from
      }
  }

}
