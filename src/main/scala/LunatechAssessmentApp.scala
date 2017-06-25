import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.{Source, StdIn}

object LunatechAssessmentApp extends App {

  val csvCountryReader = new CountryCsvReader(Source.fromResource("data/countries.csv"))
  val csvAirportReader = new AirportCsvReader(Source.fromResource("data/airports.csv"))
  val csvRunwayReader = new RunwayCsvReader(Source.fromResource("data/runways.csv"))

  val service = new Service(csvCountryReader,csvAirportReader,csvRunwayReader)
  val controller = new Controller(service)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(controller.route, "0.0.0.0", 9000)

  println(s"Server started at http://localhost:9000/\nPress a key to stop ...")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

}
