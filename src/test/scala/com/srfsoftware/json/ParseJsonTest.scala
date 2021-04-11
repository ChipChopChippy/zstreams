package com.srfsoftware.json

import com.srfsoftware.model.{CovidData, ISOCodeData, ISOCodeData1, Vaccinations, WorldVaccinations}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.json4s._
import org.json4s.jackson.JsonMethods._

class ParseJsonTest extends AnyFlatSpec with should.Matchers with JSONMappers {

  val afg =
    """
      |{"AFG": {
      |        "continent": "Asia",
      |        "location": "Afghanistan",
      |        "population": 38928341.0,
      |        "population_density": 54.422,
      |        "median_age": 18.6,
      |        "aged_65_older": 2.581,
      |        "aged_70_older": 1.337,
      |        "gdp_per_capita": 1803.987,
      |        "cardiovasc_death_rate": 597.029,
      |        "diabetes_prevalence": 9.59,
      |        "handwashing_facilities": 37.746,
      |        "hospital_beds_per_thousand": 0.5,
      |        "life_expectancy": 64.83,
      |        "human_development_index": 0.511,
      |        "data": [{
      |                "date": "2020-02-24",
      |                "total_cases": 1.0,
      |                "new_cases": 1.0,
      |                "total_cases_per_million": 0.026,
      |                "new_cases_per_million": 0.026,
      |                "stringency_index": 8.33
      |            }, {
      |                "date": "2020-02-25",
      |                "total_cases": 1.0,
      |                "new_cases": 0.0,
      |                "total_cases_per_million": 0.026,
      |                "new_cases_per_million": 0.0,
      |                "stringency_index": 8.33
      |            }, {
      |                "date": "2020-02-26",
      |                "total_cases": 1.0,
      |                "new_cases": 0.0,
      |                "total_cases_per_million": 0.026,
      |                "new_cases_per_million": 0.0,
      |                "stringency_index": 8.33
      |            }]},
      |            "AND": {
      |        "continent": "Europe",
      |        "location": "Andorra",
      |        "population": 77265.0,
      |        "population_density": 163.755,
      |        "cardiovasc_death_rate": 109.135,
      |        "diabetes_prevalence": 7.97,
      |        "female_smokers": 29.0,
      |        "male_smokers": 37.8,
      |        "life_expectancy": 83.73,
      |        "human_development_index": 0.868,
      |        "data": [{
      |                "date": "2020-03-02",
      |                "total_cases": 1.0,
      |                "new_cases": 1.0,
      |                "total_cases_per_million": 12.942,
      |                "new_cases_per_million": 12.942,
      |                "stringency_index": 0.0
      |            }, {
      |                "date": "2020-03-03",
      |                "total_cases": 1.0,
      |                "new_cases": 0.0,
      |                "total_cases_per_million": 12.942,
      |                "new_cases_per_million": 0.0,
      |                "stringency_index": 0.0
      |            }]}}""".stripMargin

//  "AFG" should "be parsed correctly" in {
//    implicit val formats = DefaultFormats
//    val json = parse(afg).asInstanceOf[JObject]
//    val res = for {
//      j <- json.values
//      d =  j._2.asInstanceOf[Map[String, Any]]("data").asInstanceOf[List[Map[String, Any]]]
//      e <- d
//      f = build(e, CovidData)("date","total_cases","new_cases","total_cases_per_million","new_cases_per_million","total_vaccinations","stringency_index")
//    } yield f
//    res
//  }

  "AFG" should "be parsed correctly" in {
    val json = parseJson(afg)
    json.head.iso_code shouldBe "AFG"
    json.head.iso_code_data.population shouldBe 38928341.0
  }

  def build[A,B,C](m: Map[A,B], f: (B,B,B,B,B,B,B) => C)(k1: A, k2: A, k3: A, k4: A, k5: A, k6: A, k7: A): Option[C] = for {
    v1 <- m.get(k1)
    v2 <- m.get(k2)
    v3 <- m.get(k3)
    v4 <- m.get(k4)
    v5 <- m.get(k5)
    v6 <- m.get(k6)
    v7 <- m.get(k7)
  } yield f(v1, v2, v3, v4, v5, v6, v7)

//  for {
//    j <- json.obj
//    k =  j._1
//    l = j._2.values
//    m = l.asInstanceOf[Map[String, AnyRef]]("data")
//  } yield (k, l, m)


}
