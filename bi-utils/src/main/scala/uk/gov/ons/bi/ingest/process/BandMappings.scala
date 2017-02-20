package uk.gov.ons.bi.ingest.process

/**
  * Created by Volodymyr.Glushak on 15/02/2017.
  */
object BandMappings {


  def employmentBand(z: Int): String = z match {
    case 0 => "A"
    case 1 => "B"
    case x if x < 5 => "C"
    case x if x < 10 => "D"
    case x if x < 20 => "E"
    case x if x < 25 => "F"
    case x if x < 50 => "G"
    case x if x < 75 => "H"
    case x if x < 100 => "I"
    case x if x < 150 => "J"
    case x if x < 200 => "K"
    case x if x < 250 => "K"
    case x if x < 300 => "L"
    case x if x < 300 => "M"
    case x if x < 500 => "N"
    case _ => "O"

  }

  def turnoverBand(z: Long): String = z match {
    case x if x < 100 => "A"
    case x if x < 250 => "B"
    case x if x < 500 => "C"
    case x if x < 1000 => "D"
    case x if x < 2000 => "E"
    case x if x < 5000 => "F"
    case x if x < 10000 => "G"
    case x if x < 50000 => "H"
    case _ => "I"
  }

  def legalStatusBand(s: String): Int = s match {
    case "Company" => 1
    case "Sole Proprietor" => 2
    case "Partnership" => 3
    case "Public Corporation" => 4
    case "Non-Profit Organisation" => 5
    case "Local Authority" => 6
    case "Central Government" => 7
    case "Charity" => 8
    case _ => 0
  }

  def tradingStatusBand(s: String): String = s match {
    case "Active" => "A"
    case "Closed" => "C"
    case "Dormant" => "D"
    case "Insolvent" => "I"
    case _ => "?"
  }


}
