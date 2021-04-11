package com.srfsoftware.model

import java.util.Date

trait Vaccine extends Product with Serializable {
  def name: String
}

object Vaccine {
  case object SputnikV extends Vaccine { override def name = "Sputnik V" }
  case object EpiVacCorona extends Vaccine { override def name = "EpiVacCorona" }
  case object Oxford extends Vaccine { override def name = "Oxford/AstraZeneca" }
  case object Pfizer extends Vaccine { override def name = "Pfizer/BioNTech" }
  case object Moderna extends Vaccine { override def name = "Moderna" }
  case object Johnson extends Vaccine { override def name = "Johnson&Johnson" }
  case object SinopharmBeijing extends Vaccine { override def name = "Sinopharm/Beijing" }
  case object SinopharmWuhan extends Vaccine { override def name = "Sinopharm/Wuhan" }
  case object Sinovac extends Vaccine { override def name = "Sinovac" }
}

final case class Vaccination(location: String,
                              date: String,
                              vaccine:String,
                              source_url:String,
                              total_vaccinations:Int,
                              people_vaccinated:Int,
                              people_fully_vaccinated:Int)
object Vaccination {
  def fromString(string: String): Option[List[Vaccination]] = {
    val x = string.split("\n").tail
    val res = for {
      v1 <- x
      v2 = v1.split("\"")
      v3 = if (v2.size == 3) {
        val v4 = v2(1).replace(","," ::")
        v2(0) + v4 + v2(2)
      } else v1
      v  = v3.split(",")
      r  = if (v.length != 7) None else Some(Vaccination(v(0), v(1), v(2), v(3), v(4).toInt, v(5).toInt, v(6).toInt))
    } yield r
    sequence(res.toList)
  }

  def sequence[A](l: List[Option[A]]): Option[List[A]] = l match {
    case Nil => Some(Nil)
    case h :: t => h.flatMap((hh => sequence(t).map(hh :: _)))
  }
}

final case class Vaccinations(worldVaccinations: List[WorldVaccinations])
final case class WorldVaccinations(iso_code: String, iso_code_data: ISOCodeData1)
final case class ISOCodeData(continent: String,	location: String, population: Int, population_density: Double, median_age: Double, aged_65_older: Double,
  aged_70_older: Double, gdp_per_capita: Double, cardiovasc_death_rate: Double, diabetes_prevalence: Double, handwashing_facilities: Double,
  hospital_beds_per_thousand: Double, life_expectancy: Double, human_development_index: Double, data: List[CovidData])
final case class CovidData(date: Date, total_cases: Double, new_cases: Double, total_cases_per_million: Double, new_cases_per_million: Double, total_vaccinations: Double,
  stringency_index: Double)

final case class ISOCodeData1(continent: String, location: String, population: Double, population_density: Double, median_age: Double, aged_65_older: Double,
aged_70_older: Double, gdp_per_capita: Double, cardiovasc_death_rate: Double, diabetes_prevalence: Double, handwashing_facilities: Double,
hospital_beds_per_thousand: Double, life_expectancy: Double, human_development_index: Double, data: List[CovidData])