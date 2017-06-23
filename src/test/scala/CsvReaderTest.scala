import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class CsvReaderTest extends FlatSpec with Matchers {

  case class Dummy(a: Int, b: String)

  class DummyCsvReader(val source: Source) extends CsvReader[Dummy] {
    override def convert: (Array[String]) => Dummy = { col =>
      Dummy(col(0).toInt, col(1))
    }
  }

  "DummyCsvReader" should "Read dummy csv data and parse it into a collection of Dummy objects" in {
    val lines = new DummyCsvReader(Source.fromString(
      "header1,header2\n" +
        "1,One\n" +
        "2,Two"
    )).read

    lines should have size 2
    lines.head shouldBe Dummy(1, "One")
    lines.tail.head shouldBe Dummy(2, "Two")
  }

  "DummyCsvReader" should "Read dummy csv data and trimming white spaces and doubles quotes" in {
    val lines = new DummyCsvReader(Source.fromString(
      "header1,header2\n" +
        "   1   , \"    One  \" \n" +
        "2,      Two"
    )).read
    lines.head shouldBe Dummy(1, "One")
    lines.tail.head shouldBe Dummy(2, "Two")
  }

  "DummyCsvReader" should "Throw CsvParsingException when a parsing error occurs" in {
    val dummyReader = new DummyCsvReader(Source.fromString(
      "header1,header2\n" +
        "XXXXX,One"
    ))
    a[CsvParsingException] should be thrownBy {
      dummyReader.read
    }
  }

  "CountryCsvReader" should "Read countries csv data and parse it into a collection of Country objects" in {
    val countries = new CountryCsvReader(Source.fromResource("countries.csv")).read

    countries should have size 5
    countries.head shouldBe Country(Some(302687), Some("FR"), Some("France"), Some("EU"), Some("http://en.wikipedia.org/wiki/France"))
  }

  "AirportCsvReader" should "Read airports csv data and parse it into a collection of Airport objects" in {
    val airports = new AirportCsvReader(Source.fromResource("airports.csv")).read

    airports should have size 10
    airports.head shouldBe Airport(Some(41524), Some("GF-0001"), Some("small_airport"), Some("Kourou airport"), Some("GF"))
  }

}
