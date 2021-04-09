package com.srfsoftware.model

trait Country {
  def isoCode: String
}

case object Country { self =>
  case object AUSTRALIA extends Country { override def isoCode = "AUS"}
  case object AUSTRIA extends Country { override def isoCode = "AUT"}
  case object BRAZIL extends Country { override def isoCode = "BRA"}
  case object CANADA extends Country { override def isoCode = "CAN"}
  case object CHINA extends Country { override def isoCode = "CHN"}
  case object DENMARK extends Country { override def isoCode = "DEN"}
  case object FRANCE extends Country { override def isoCode = "FRA"}
  case object GERMANY extends Country { override def isoCode = "DEU"}
  case object ISRAEL extends Country { override def isoCode = "ISR"}
  case object ITALY extends Country { override def isoCode = "ITA"}
  case object JAPAN extends Country { override def isoCode = "JPN"}
  case object POLAND extends Country { override def isoCode = "POL"}
  case object RUSSIA extends Country { override def isoCode = "RUS"}
  case object SOUTH_AFRICA extends Country { override def isoCode = "ZAF"}
  case object UNITED_KINGDOM extends Country { override def isoCode = "GBR"}
  case object UNITED_STATES extends Country { override def isoCode = "USA"}
}
