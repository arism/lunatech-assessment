import java.text.Normalizer

class Service(implicit private val countryCsvReader: CountryCsvReader,
              implicit private val airportCsvReader: AirportCsvReader,
              implicit private val runwayCsvReader: RunwayCsvReader) {

  println ("Loading csv data ...")

  private val countries: Seq[Country] = countryCsvReader.read
  private val airports: Seq[Airport] = airportCsvReader.read
  private val runways: Seq[Runway] = runwayCsvReader.read

  // kind of index use for optimisation reasons
  private val countryAirports = airports.groupBy(_.isoCountry)
  private val airportRunways = runways.groupBy(_.airportIdent)
  private val leIdentRunways = runways.groupBy(_.leIdent)

  /**
    * Returns countries that matches by their:
    *  1 - country code if codeOrName is 2 characters size
    *  2 - country name ( fuzzy matching ) if codeOrName is more than 2 characters
    */
  def listCountriesByCodeOrName(codeOrName: String): Option[Seq[Country]] = {
    codeOrName.trim.length match {
      case 0 | 1 => None
      case 2 => countries.find(_.code.contains(codeOrName.toUpperCase)).map(c => Seq(c))
      case _ => countries.filter(_.name.fuzzyStartWith(codeOrName)) match {
        case Seq() => None
        case acc@_ => Some(acc)
      }
    }
  }

  /**
    * Returns the airports and corresponding runways for a given country
    */
  def getAirportsAndRunwaysByCountry(code: String): Option[CountryDTO] = {
    for {
      country <- countries.find(_.code.contains(code.toUpperCase))
      airportDTOs = countryAirports.get(Some(code.toUpperCase)).map { airports =>
        airports.map { airport =>
          AirportDTO(airport, airportRunways.get(airport.ident))
        }
      }
    } yield CountryDTO(country, airportDTOs)
  }

  /**
    * Returns a list of countries and their airports count ordered descending or ascending
    * according to "desc" parameter, and limited by "limit" parameter
    */
  def getAirportsCountAndRunwaysTypePerCountry(desc: Boolean, limit: Int = 10): Seq[CountryAirportsDTO] = {
    for {
      (countryCode, airports) <- countryAirports.toSeq.sortBy(if (desc) -_._2.length else _._2.length).take(limit)
      country <- countries.find(_.code == countryCode)
      runways = airports.flatMap(airport => airportRunways.get(airport.ident)).flatten
      distinctRunwayTypes = runways.flatMap(_.surface).distinct.sorted
    } yield CountryAirportsDTO(country, airports.length, distinctRunwayTypes)
  }

  /**
    * Returns a top 10 of most common runway identifications
    * @return
    */
  def getTop10RunwaysIdentifications: Seq[String] = {
    leIdentRunways.toSeq.sortBy(_._2.length)(Ordering[Int].reverse).take(10).flatMap(_._1)
  }

  // --------------- Implicit conversions ------------------------

  implicit class StringFuzzyStartWith(val s: Option[String]) {
    private def normalize(str: String): String = {
      Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase
    }

    def fuzzyStartWith(c: String): Boolean = s match {
      case None => false
      case Some(str) => normalize(str).startsWith(normalize(c))
    }
  }

}