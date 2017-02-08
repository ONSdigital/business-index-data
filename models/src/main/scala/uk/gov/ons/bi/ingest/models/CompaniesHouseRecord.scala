package uk.gov.ons.bi.ingest.models

import cats.data.ValidatedNel
import org.joda.time.DateTime
import uk.gov.ons.bi.ingest.parsers.CsvParser
import com.outworkers.util.catsparsers._
import com.outworkers.util.validators.dsl._
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
        parseNonEmpty[DateTime](sourceType.getIndex(2)).prop("next_due_date") and
        parseNonEmpty[DateTime](sourceType.getIndex(3)).prop("last_made_up_date") and
        parseNonEmpty[String](sourceType.getIndex(4)).prop("account_category") map {
        case (day, month, nextDueDate, lastMadeUpToDate, category) =>
          Accounts(
            accounts_ref_day = day,
            accounts_ref_month = month,
            next_due_date = nextDueDate,
            last_made_up_date = lastMadeUpToDate,
            account_category = category
          )
        }
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
      parse[DateTime](sourceType.getIndex(0)).prop("next_due_date") and
        parseNonEmpty[DateTime](sourceType.getIndex(1)).prop("last_made_up_date") map {
        case (nextDate, lastDate) => new Returns(nextDate, lastDate)
      }
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
      parseNonEmpty[Int](sourceType.getIndex(0)).prop("num_mort_charges") and
        parseNonEmpty[Int](sourceType.getIndex(1)).prop("num_mort_outstanding") and
        parseNonEmpty[Int](sourceType.getIndex(2)).prop("num_mort_part_satisfied") and
        parseNonEmpty[Int](sourceType.getIndex(3)).prop("num_mort_satisfied") map {
          case (numCharges, outstanding, partSatisfied, mortSatisfied) => {
            new Mortgages(numCharges, outstanding, partSatisfied, mortSatisfied)
          }
        }
    }
  }
}

case class SICCode(
  sic_text_1: String,
  sic_text_2: String,
  sic_text_3: String,
  sic_text_4: String
)

object SICCode {
  implicit object SICCodeParser extends CsvParser[SICCode] {
    override def extract(sourceType: Seq[String]): Nel[SICCode] = {
      parse[String](sourceType.getIndex(0)).prop("sic_text_1") and
        parse[String](sourceType.getIndex(1)).prop("sic_text_2") and
        parse[String](sourceType.getIndex(2)).prop("sic_text_3") and
        parse[String](sourceType.getIndex(3)).prop("sic_text_4") map {
          case (sicText1, sicText2, sicText3, sicText4) => SICCode(sicText1, sicText2, sicText3, sicText4)
        }
    }
  }
}

case class LimitedPartnerships(
  num_gen_partners: Int,
  num_lim_partners: Int
)

object LimitedPartnerships {
  implicit object SICCodeParser extends CsvParser[LimitedPartnerships] {
    override def extract(sourceType: Seq[String]): Nel[LimitedPartnerships] = {
      parse[Int](sourceType.getIndex(0)).prop("num_gen_partners") and
        parse[Int](sourceType.getIndex(1)).prop("num_lim_partners") map {
          case (numGen, numLim) => LimitedPartnerships(numGen, numLim)
        }
    }
  }
}

case class PreviousName(
  condate: String,
  company_name: String
)

object PreviousName {
  implicit object PreviousNameParser extends CsvParser[PreviousName] {
    override def extract(sourceType: Seq[String]): Nel[PreviousName] = {
      parse[String](sourceType.getIndex(0)).prop("condate") and
        parse[String](sourceType.getIndex(1)).prop("company_name") map {
          case (condate, companyName) => PreviousName(condate, companyName)
        }
    }
  }
}

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

object RegistrationAddress {
  implicit object RegistrationAddressParser extends CsvParser[RegistrationAddress] {
    override def extract(sourceType: Seq[String]): Nel[RegistrationAddress] = {
      parseNonEmpty[String](sourceType.getIndex(0)).prop("care_of") and
        parseNonEmpty[String](sourceType.getIndex(1)).prop("po_box") and
        parse[String](sourceType.getIndex(2)).prop("address_line_1") and
        parse[String](sourceType.getIndex(3)).prop("address_line_2") and
        parseNonEmpty[String](sourceType.getIndex(4)).prop("post_town") and
        parseNonEmpty[String](sourceType.getIndex(5)).prop("county") and
        parseNonEmpty[String](sourceType.getIndex(6)).prop("country") and
        parseNonEmpty[String](sourceType.getIndex(7)).prop("postcode") map {
          case (careOf, poBox, al1, al2, postTown, county, country, postcode) =>
            RegistrationAddress(careOf, poBox, al1, al2, postTown, county, country, postcode)
        }
    }
  }
}

case class PreviousNames(
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

object PreviousNames {
  implicit object PreviousNamesParser extends CsvParser[PreviousNames] {
    override def extract(source: Seq[String]): Nel[PreviousNames] = {
      CsvParser[PreviousName].extract(source.take(2)).prop("previous_name_1") and
      CsvParser[PreviousName].extract(source.slice(2, 4)).prop("previous_name_2") and
      CsvParser[PreviousName].extract(source.slice(4, 6)).prop("previous_name_3") and
      CsvParser[PreviousName].extract(source.slice(6, 8)).prop("previous_name_4") and
      CsvParser[PreviousName].extract(source.slice(8, 10)).prop("previous_name_5") and
      CsvParser[PreviousName].extract(source.slice(10, 12)).prop("previous_name_6") and
      CsvParser[PreviousName].extract(source.slice(12, 14)).prop("previous_name_7") and
      CsvParser[PreviousName].extract(source.slice(14, 16)).prop("previous_name_8") and
      CsvParser[PreviousName].extract(source.slice(16, 18)).prop("previous_name_9") and
      CsvParser[PreviousName].extract(source.slice(18, 20)).prop("previous_name_10") map {
        case (pn1, pn2, pn3, pn4, pn5, pn6, pn7, pn8, pn9, pn10) =>
          PreviousNames(pn1, pn2, pn3, pn4, pn5, pn6, pn7, pn8, pn9, pn10)
      }
    }
  }
}

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
  previous_names: PreviousNames

)

object CompaniesHouseRecord {
  implicit object CompaniesHouseRecordParser extends CsvParser[CompaniesHouseRecord] {
    override def extract(source: Seq[String]): ValidatedNel[String, CompaniesHouseRecord] = {
      parse[String](source.getIndex(1)).prop("id") and
        parse[String](source.getIndex(2)).prop("company_name") and
        parse[String](source.getIndex(3)).prop("company_number") and
        parse[String](source.getIndex(4)).prop("company_category") and
        parse[String](source.getIndex(5)).prop("company_status") and
        parse[String](source.getIndex(6)).prop("country_of_origin") and
        parseNonEmpty[DateTime](source.getIndex(7)).prop("dissolution_date") and
        parseNonEmpty[DateTime](source.getIndex(8)).prop("incorporation_date") and
        CsvParser[Accounts].extract(source.slice(8, 13)).prop("accounts")

    }
  }
}
