
final case class Country(id: Option[Int], code: Option[String], name: Option[String], continent: Option[String], wikipediaLink: Option[String])

final case class Airport(id: Option[Int], ident: Option[String], `type`: Option[String], name: Option[String], isoCountry: Option[String])

final case class Runway(id: Option[Int], airportIdent: Option[String], surface: Option[String], leIdent : Option[String])