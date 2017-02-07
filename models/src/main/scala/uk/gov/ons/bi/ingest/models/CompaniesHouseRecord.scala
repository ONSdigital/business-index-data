package uk.gov.ons.bi.ingest.models

import cats.data.ValidatedNel
import org.joda.time.DateTime
import uk.gov.ons.bi.ingest.parsers.CsvParser
import com.outworkers.util.parsers._
import com.outworkers.util.validators._
import uk.gov.ons.bi.ingest.parsers._

case class Accounts(
  accounts_ref_day: String,
  accounts_ref_month: String,
  next_due_date: Option[DateTime],
  last_made_up_date: Option[DateTime],
  account_category: Option[String]
)


object Accounts {
  implicit object AccountsParser extends CsvParser[Accounts] {
    override def extract(sourceType: Seq[String]): Nel[Accounts] = {
      parse[String](sourceType.head).prop("accounts_ref_day") and
        parse[String](sourceType(1)).prop("accounts_ref_month") and
        parseNonEmpty[DateTime](sourceType.value(2)).prop("next_due_date") and
        parseNonEmpty[DateTime](sourceType.value(3)).prop("last_made_up_date") and
        parseNonEmpty[String](sourceType.value(4)).prop("account_category") map (_.as[Accounts])
    }
  }
}

case class Returns(
  next_due_date: DateTime,
  last_made_up_date: Option[DateTime]
)

object Returns {
  implicit object ReturnsParser extends CsvParser[Returns] {
    override def extract(sourceType: Seq[String]): Nel[Returns] = {
      parse[DateTime](sourceType.head).prop("next_due_date") and
        parseNonEmpty[DateTime](sourceType.value(1)).prop("last_made_up_date") map (_.as[Returns])
    }
  }
}

case class Mortgages(
  num_mort_charges: Option[Int],
  num_mort_outstanding: Option[Int],
  num_mort_part_satisfied: Option[Int],
  num_mort_satisfied: Option[Int]
)

object Mortgages {

  implicit object Mortgages extends CsvParser[Mortgages] {
    override def extract(sourceType: Seq[String]): Nel[Mortgages] = {
      parseNonEmpty[Int](sourceType.value(0)).prop("num_mort_charges") and
        parseNonEmpty[Int](sourceType.value(1)).prop("num_mort_outstanding") and
        parseNonEmpty[Int](sourceType.value(2)).prop("num_mort_part_satisfied") and
        parseNonEmpty[Int](sourceType.value(3)).prop("num_mort_satisfied") map (_.as[Mortgages])
    }
  }
}

case class SICCode(
  sic_text_1: String,
  sic_text_2: String,
  sic_text_3: String,
  sic_text_4: String
)

case class LimitedPartnerships(
  num_gen_partners: Int,
  num_lim_partners: Int
)

case class PreviousName(
  condate: String,
  company_name: String
)

case class RegistrationAddress(
  care_of: Option[String],
  po_box: Option[String],
  address_line_1: String,
  address_line_2: String,
  post_town: Option[String],
  county: Option[String],
  country: Option[String],
  postcode: Option[String]
)

case class CompaniesHouseRecord(
  id: String,
  company_name: String,
  company_number: String,
  company_category: String,
  company_status: String,
  country_of_origin: String,
  dissolution_date: Option[DateTime],
  incorporation_date: Option[DateTime],
  accounts: Accounts,
  returns: Option[Returns],
  sic_code: SICCode,
  limitedPartnerships: LimitedPartnerships,
  uri: Option[String],
  previous_name_1: Option[PreviousName],
  previous_name_2: Option[PreviousName],
  previous_name_3: Option[PreviousName],
  previous_name_4: Option[PreviousName],
  previous_name_5: Option[PreviousName],
  previous_name_6: Option[PreviousName],
  previous_name_7: Option[PreviousName],
  previous_name_8: Option[PreviousName],
  previous_name_9: Option[PreviousName],
  previous_name_10: Option[PreviousName]
)
