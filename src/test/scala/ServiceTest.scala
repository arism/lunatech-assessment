import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

import scala.io.Source


class ServiceTest extends FunSuite with Matchers {
  implicit val countryCsvReader = new CountryCsvReader(Source.fromResource("countries.csv"))
  implicit val airportCsvReader = new AirportCsvReader(Source.fromResource("airports.csv"))
  implicit val runwayCsvReader = new RunwayCsvReader(Source.fromResource("runways.csv"))
  val service = new Service

  test("listCountriesByCodeOrName") {
    // Empty or invalid codeOrName
    service.listCountriesByCodeOrName("") shouldBe None
    service.listCountriesByCodeOrName("X") shouldBe None
    service.listCountriesByCodeOrName("US") shouldBe None

    // By code lookup
    service.listCountriesByCodeOrName("FR").get shouldBe
      Seq(Country(Some(302687), Some("FR"), Some("France"), Some("EU"), Some("http://en.wikipedia.org/wiki/France")))
    service.listCountriesByCodeOrName("DZ").get shouldBe
      Seq(Country(Some(302568), Some("DZ"), Some("Algeria"), Some("AF"), Some("http://en.wikipedia.org/wiki/Algeria")))

    // Fuzzy name search
    service.listCountriesByCodeOrName("fre").get.map(_.code.get) shouldBe Seq("PF", "TF", "GF")
  }

  test("getCountryAirports") {
    // invalid country code
    service.getAirportsAndRunwaysByCountry("XXX") shouldBe None

    var countryDTO = service.getAirportsAndRunwaysByCountry("GF")
    countryDTO should not be None
    countryDTO.get.country shouldBe Country(Some(302796), Some("GF"), Some("French Guiana"), Some("SA"), Some("http://en.wikipedia.org/wiki/French_Guiana"))
    countryDTO.get.airports should not be None
    countryDTO.get.airports.get should have size 10
    countryDTO.get.airports.get.find(_.airport.ident.get == "SOOG") shouldBe
      Some(AirportDTO(Airport(Some(6199), Some("SOOG"), Some("medium_airport"), Some("Saint-Georges-de-l'Oyapock Airport"), Some("GF")),
        Some(Seq(Runway(Some(235704), Some("SOOG"), Some("CON"), Some("04"))))
      ))
  }

  test("getTop10RunwaysIdentifications") {
    service.getTop10RunwaysIdentifications shouldBe Seq("H1", "18", "09", "17", "08", "16", "E", "N", "04", "15")
  }
  
}
