package uk.gov.ons.bi.ingest.models

import java.util.{Calendar, Date}

import org.joda.time.DateTime
import uk.gov.ons.bi.ingest.helper.Utils
import uk.gov.ons.bi.ingest.parsers._

case class Address(
  line_1: String,
  line_2: String,
  line_3: String,
  line_4: String,
  line_5: String,
  postcode: String
)

case class PayeName(nameline1: String, nameline2: String, nameline3: String) {
  override def toString: String = s"$nameline1 $nameline2 $nameline3"
}

case class TradStyle(tradstyle1: String, tradstyle2: String, tradstyle3: String)

case class MonthJobs(dec_jobs: Int, mar_jobs: Int, june_jobs: Int, sept_jobs: Int) {

  def recent_jobs = Utils.Month match {
    case m if m < 3 => dec_jobs
    case m if m < 6 => mar_jobs
    case m if m < 9 => june_jobs
    case _ => sept_jobs
  }
}

case class PayeEmp(mfullemp: Int, msubemp: Int, ffullemp: Int, fsubemp: Int, unclemp: Int, unclsubemp: Int)

case class PayeRecord2(
  entref: String,
  payeref: String,
  deathcode: String,
  birthdate: String,
  deathdate: String,
  payeEmp: PayeEmp,
  month_jobs: MonthJobs,
  jobs_lastupd: String,
  legalstatus: String,
  prevpaye: String,
  employer_cat: String,
  stc: String,
  crn: String,
  actiondate: String,
  addressref: String,
  marker: String,
  inqcode: String,
  name: PayeName,
  tradstyle: TradStyle,
  address: Address
)

