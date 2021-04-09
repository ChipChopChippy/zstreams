package com.srfsoftware.json

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.srfsoftware.model.{CovidData, ISOCodeData1, WorldVaccinations}
import org.json4s.{DefaultFormats, JObject}
import org.json4s.jackson.JsonMethods.parse

import scala.util.Try

trait JSONMappers {
  lazy val covidJsonMapper = {
    val m = new ObjectMapper() with ScalaObjectMapper
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    m.registerModule(DefaultScalaModule)
    m
  }

  def parseJson(str: String) = {
    implicit val formats = DefaultFormats
    val json = parse(str).asInstanceOf[JObject]
    val res = for {
      j      <- json.values
      isoMap =  j._2.asInstanceOf[Map[String, AnyRef]]
      v =  WorldVaccinations(j._1, ISOCodeData1(
        Try{isoMap("continent").toString}.getOrElse(""),
        Try{isoMap("location").toString}.getOrElse(""),
        Try{isoMap("population").toString}.getOrElse(""),
        Try{isoMap("population_density").toString}.getOrElse(""),
        Try{isoMap("median_age").toString}.getOrElse(""),
        Try{isoMap("aged_65_older").toString}.getOrElse(""),
        Try{isoMap("aged_70_older").toString}.getOrElse(""),
        Try{isoMap("gdp_per_capita").toString}.getOrElse(""),
        Try{isoMap("cardiovasc_death_rate").toString}.getOrElse(""),
        Try{isoMap("diabetes_prevalence").toString}.getOrElse(""),
        Try{isoMap("handwashing_facilities").toString}.getOrElse(""),
        Try{isoMap("hospital_beds_per_thousand").toString}.getOrElse(""),
        Try{isoMap("life_expectancy").toString}.getOrElse(""),
        Try{isoMap("human_development_index").toString}.getOrElse(""),
        for {
          e <- j._2.asInstanceOf[Map[String, AnyRef]]("data").asInstanceOf[List[Map[String, AnyRef]]]
          f =  CovidData(
            Try{e("date").toString}.getOrElse(""),
            Try{e("total_cases").toString}.getOrElse(""),
            Try{e("new_cases").toString}.getOrElse("0.0"),
            Try{e("total_cases_per_million").toString}.getOrElse("0.0"),
            Try{e("new_cases_per_million").toString}.getOrElse("0.0"),
            Try{e("total_vaccinations").toString}.getOrElse("0.0"),
            Try{e("stringency_index").toString}.getOrElse(""))
        } yield f
      ))
    } yield v
    res.toList
  }
}
