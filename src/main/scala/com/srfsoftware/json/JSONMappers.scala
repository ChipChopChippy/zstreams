package com.srfsoftware.json

import com.srfsoftware.model.{CovidData, ISOCodeData1, WorldVaccinations}
import org.json4s.{DefaultFormats, JObject}
import org.json4s.jackson.JsonMethods.parse

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import scala.util.Try

trait JSONMappers {
  def parseJson(str: String) = {
    implicit val formats = DefaultFormats
    val json = parse(str).asInstanceOf[JObject]
    val res = for {
      j      <- json.values
      isoMap =  j._2.asInstanceOf[Map[String, Any]]
      v =  WorldVaccinations(j._1, ISOCodeData1(
        vaccineField("continent", isoMap).asInstanceOf[Option[String]].getOrElse(""),
        vaccineField("location", isoMap).asInstanceOf[Option[String]].getOrElse(""),
        vaccineField("population", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("population_density", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("median_age", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("aged_65_older", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("aged_70_older", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("gdp_per_capita", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("cardiovasc_death_rate", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("diabetes_prevalence", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("handwashing_facilities", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("hospital_beds_per_thousand", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("life_expectancy", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        vaccineField("human_development_index", isoMap).asInstanceOf[Option[Double]].getOrElse(0.0),
        for {
          e <- j._2.asInstanceOf[Map[String, Any]]("data").asInstanceOf[List[Map[String, Any]]]
          f =  CovidData(
            vaccineField("date", e).asInstanceOf[Option[Date]].getOrElse(Date.from(Instant.EPOCH)),
            vaccineField("total_cases", e).asInstanceOf[Option[Double]].getOrElse(0.0),
            vaccineField("new_cases", e).asInstanceOf[Option[Double]].getOrElse(0.0),
            vaccineField("total_cases_per_million", e).asInstanceOf[Option[Double]].getOrElse(0.0),
            vaccineField("new_cases_per_million", e).asInstanceOf[Option[Double]].getOrElse(0.0),
            vaccineField("total_vaccinations", e).asInstanceOf[Option[Double]].getOrElse(0.0),
            vaccineField("stringency_index", e).asInstanceOf[Option[Double]].getOrElse(0.0))
        } yield f
      ))
    } yield v
    res.toList
  }

  private def vaccineField(field: String, isoMap: Map[String, Any]) = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val jf = isoMap get field
    jf match {
      case Some(value) => value match {
        case d:Double  => Some(d)
        case i:Int     => Some(i)
        case dt:String => Some(Try{dateFormat.parse(dt)}.getOrElse(dt))
      }
      case None        => None
    }
  }
}
