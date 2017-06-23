
final case class CountryDTO(country: Country, airports: Option[Seq[AirportDTO]])

final case class AirportDTO(airport: Airport, runways: Option[Seq[Runway]])

final case class CountryAirportsDTO(country: Country, airportsCount: Int, runwaysType: Seq[String])