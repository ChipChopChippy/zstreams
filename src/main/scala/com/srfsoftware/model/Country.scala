package com.srfsoftware.model

trait Country {
  def isoCode: String
}

case object Country { self =>
  case object AUSTRALIA extends Country { override def isoCode = "Australia"}
  case object AUSTRIA extends Country { override def isoCode = "Austria"}
  case object BRAZIL extends Country { override def isoCode = "Brazil"}
  case object CANADA extends Country { override def isoCode = "Canada"}
  case object CHINA extends Country { override def isoCode = "China"}
  case object DENMARK extends Country { override def isoCode = "Denmark"}
  case object FRANCE extends Country { override def isoCode = "France"}
  case object GERMANY extends Country { override def isoCode = "Germany"}
  case object ISRAEL extends Country { override def isoCode = "Israel"}
  case object ITALY extends Country { override def isoCode = "Italy"}
  case object JAPAN extends Country { override def isoCode = "Japan"}
  case object POLAND extends Country { override def isoCode = "Poland"}
  case object RUSSIA extends Country { override def isoCode = "Russia"}
  case object SOUTH_AFRICA extends Country { override def isoCode = "South%20Africa"}
  case object UNITED_KINGDOM extends Country { override def isoCode = "GBR"}
  case object UNITED_STATES extends Country { override def isoCode = "United%20States"}
}
