import scala.io.Codec.UTF8
import scala.io.Source
import scala.util.{Failure, Success, Try}

final case class CsvParsingException(private val message: String, private val cause: Throwable)
  extends Exception(message, cause)

trait CsvReader[A] {

  val source: Source

  val delimiter: String = ","

  val skipHeader: Boolean = true

  def convert: Array[String] => A

  def read: Seq[A] = {
    implicit val codec = UTF8
    try {
      for {
        line <- source.getLines().drop(if (skipHeader) 1 else 0).toSeq
        col = line.split(delimiter, -1).map(trim)
      } yield convert(col)
    } catch {
      case e: RuntimeException => throw CsvParsingException("Error occurs when parsing csv", e)
    }
  }

  private def trim(str: String): String = {
    str.trim.replaceAll("^\"(.*)\"$", "$1").trim // trim whitespaces and double quotes
  }

  implicit def stringToOption(s: String): Option[String] = s match {
    case null => None
    case "" => None
    case _ => Some(s)
  }

  implicit def stringToOptionOfInt(s: String): Option[Int] = Try(s.toInt) match {
    case Failure(_) => None
    case Success(value) => Some(value)
  }
}

final class CountryCsvReader(val source: Source) extends CsvReader[Country] {
  override def convert: (Array[String]) => Country = { col =>
    Country(col(0), col(1), col(2), col(3), col(4))
  }
}

final class AirportCsvReader(val source: Source) extends CsvReader[Airport] {
  override def convert: (Array[String]) => Airport = { col =>
    Airport(col(0), col(1), col(2), col(3), col(8))
  }
}

final class RunwayCsvReader(val source: Source) extends CsvReader[Runway] {
  override def convert: (Array[String]) => Runway = { col =>
    Runway(col(0), col(2), col(5),col(8))
  }
}
