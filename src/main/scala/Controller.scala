import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit lazy val countryFormat: RootJsonFormat[Country] = jsonFormat5(Country)
  implicit lazy val countryDTOFormat: RootJsonFormat[CountryDTO] = jsonFormat2(CountryDTO)
  implicit lazy val airportFormat: RootJsonFormat[Airport] = jsonFormat5(Airport)
  implicit lazy val airportDTOFormat: RootJsonFormat[AirportDTO] = jsonFormat2(AirportDTO)
  implicit lazy val runwayFormat: RootJsonFormat[Runway] = jsonFormat4(Runway)
  implicit lazy val countryAirportDTOFormat: RootJsonFormat[CountryAirportsDTO] = jsonFormat3(CountryAirportsDTO)

}

class Controller(private val service: Service) extends Directives with JsonSupport {
  val route =
    pathPrefix("api") {
      pathPrefix("countries") {
        path(Segment / "airports") { code =>
          complete(service.getAirportsAndRunwaysByCountry(code))
        } ~
        path(Segment) { codeOrName =>
          complete(service.listCountriesByCodeOrName(codeOrName))
        }
      } ~
      pathPrefix("reports") {
        path("airportsPerCountry") {
          parameters("desc" ? true , "limit" ? 10) { (desc, limit) =>
            complete(service.getAirportsCountAndRunwaysTypePerCountry(desc,limit))
          }
        } ~
        path("top10RunwaysIdent") {
          complete(service.getTop10RunwaysIdentifications)
        }
      }
    } ~
    path(""){
      getFromResource("index.html")
    } ~
    pathPrefix("static") {
      getFromResourceDirectory("static")
    }
}
