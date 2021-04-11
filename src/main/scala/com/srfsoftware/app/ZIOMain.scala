package com.srfsoftware.app

import com.srfsoftware.app.FutureMain.countries
import com.srfsoftware.model.{CovidData, ISOCodeData1, WorldVaccinations}
import com.srfsoftware.server.Connection
import zio.blocking.effectBlocking
import zio.console.putStrLn
import zio.{App, ExitCode, URIO, ZIO}
import zio.stream.ZStream

import scala.io.Source
import scala.math.Ordering.Implicits.infixOrderingOps

object ZIOMain extends App with Connection {

  // 1. read the data from the uri
  // 2. parse the json into a WorldVaccination object
  // 3. iterate over the countries array
  //    for each date compare the total number of vaccinations


  def compareVaccinationRates(country: String, data: List[WorldVaccinations]) = {
    val countryData: List[CovidData] = data.withFilter(_.iso_code == country).flatMap(_.iso_code_data.data).sortWith(_.date < _.date)
    val otherCountries: List[(String, List[WorldVaccinations])] = data.groupBy(_.iso_code).toList.filter(_._1 != country)
    val resp = for {
      cd  <- countryData
      oc  <- otherCountries
      ocd =  oc._2.flatMap(_.iso_code_data.data)
      res =  ocd.find(p => cd.date == p.date).map {
        case cv: CovidData if cv.total_vaccinations > cd.total_vaccinations => s"$country is lower [${cd.total_vaccinations}] on ${cd.date} than ${oc._1} [${cv.total_vaccinations}]"
        case cv: CovidData if cv.total_vaccinations < cd.total_vaccinations => s"$country is higher [${cd.total_vaccinations}] on ${cd.date} than ${oc._1} [${cv.total_vaccinations}]"
        case _ => s"no data is available on ${cd.date}"
      }
    } yield res
    val ret = resp.map(_.getOrElse("")).filterNot(_ == "").filterNot(_.contains("no data is available"))
    ret
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    (for {
      _ <- ZIO.foreachParN_(32)(countries.map(_.isoCode)) { country =>
          download.foreach(data =>
           putStrLn(compareVaccinationRates(country, data).mkString("\n")))
      }
    } yield ()).exitCode
  }

  def download = {
    val uri = List(s"https://covid.ourworldindata.org/data/owid-covid-data.json")
    ZStream.fromIterable(uri).flatMap {u =>
      ZStream.unwrapManaged(openUri(u).map(req => ZStream((req, u))))
    }.mapM {
      case (request, countryUri) =>
       effectBlocking(request.getEntity.getContent).refineToOrDie[Exception].flatMap(fm =>
          ZIO.effect(parseJson(Source.fromInputStream(fm).mkString("")))
        )
    }
  }

  def sequence[A](l: List[Option[A]]): Option[List[A]] = l match {
    case Nil => Some(Nil)
    case h :: t => h.flatMap((hh => sequence(t).map(hh :: _)))
  }
}
