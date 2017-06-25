
case class CountryDTO(country: Country, airports: Option[Seq[AirportDTO]])

case class AirportDTO(airport: Airport, runways: Option[Seq[Runway]])

case class CountryAirportsDTO(country: Country, airportsCount: Int, runwaysType: Seq[String])