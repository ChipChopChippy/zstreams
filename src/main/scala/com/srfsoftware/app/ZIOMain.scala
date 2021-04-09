package com.srfsoftware.app

import com.srfsoftware.app.FutureMain.countries
import com.srfsoftware.model.{CovidData, ISOCodeData1, WorldVaccinations}
import com.srfsoftware.server.Connection
import zio.blocking.effectBlocking
import zio.console.putStrLn
import zio.{App, ExitCode, URIO, ZIO}
import zio.stream.ZStream

import scala.io.Source

object ZIOMain extends App with Connection {

  // 1. read the data from the uri
  // 2. parse the json into a WorldVaccination object
  // 3. iterate over the countries array
  //    for each date compare the


  def compareVaccinationRates(country: String, data: List[WorldVaccinations]) = {
    val countryData: List[CovidData] = data.withFilter(_.iso_code == country).flatMap(_.iso_code_data.data).sortWith(_.date < _.date)
    val otherCountries: List[List[CovidData]] = data.groupBy(_.iso_code).toList.withFilter(_._1 != country).flatMap(_._2.map(_.iso_code_data.data))
    val resp = for {
      cd  <- countryData
      oc  <- otherCountries
      res =  oc.find(p => cd.date == p.date).map {
        case cv: CovidData if cv.total_vaccinations > cd.total_vaccinations => s"$country is lower on ${cd.date}"
        case cv: CovidData if cv.total_vaccinations < cd.total_vaccinations => s"$country is higher on ${cd.date}"
        case _ => s"no data is available on ${cd.date}"
      }
    } yield res
    sequence(resp)
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    (for {
      _ <- ZIO.foreachParN_(32)(countries.map(_.isoCode)) { country =>
          download.foreach(data =>
           putStrLn(compareVaccinationRates(country, data).getOrElse(List()).mkString("\n")))
//          putStrLn(s"${data.mkString("\n")}"))
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
          ZIO.effect(parseJson(Source.fromInputStream(fm).mkString(""))) //zip ZIO.effect(countryUri)
        )
    }
  }

  def sequence[A](l: List[Option[A]]): Option[List[A]] = l match {
    case Nil => Some(Nil)
    case h :: t => h.flatMap((hh => sequence(t).map(hh :: _)))
  }
}
