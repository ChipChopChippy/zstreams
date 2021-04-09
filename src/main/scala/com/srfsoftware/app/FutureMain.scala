package com.srfsoftware.app

import com.srfsoftware.json.JSONMappers
import com.srfsoftware.model.Country._
import com.srfsoftware.server.Connection

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.{Failure, Success}

object FutureMain extends App with JSONMappers with Connection {
    lazy val countries = Array(AUSTRALIA,AUSTRIA,BRAZIL,CANADA,CHINA,DENMARK,FRANCE,GERMANY,ISRAEL,ITALY,JAPAN,POLAND,RUSSIA,SOUTH_AFRICA,
      UNITED_KINGDOM,UNITED_STATES)
//  lazy val countries = Array(UNITED_KINGDOM)

  // no issues here - just create a bunch of requests and submit them asynchronously
  val resp = for {
    country <- countries.map(_.isoCode)
    uri = s"https://covid.ourworldindata.org/data/owid-covid-data.json"
    fut = Future(unsafeResponse(uri))(scala.concurrent.ExecutionContext.Implicits.global)
  } yield fut

  // so time to process the responses from those requests....hmmmm
  // I can't access the response data from outside the onComplete block - that's unfortunate, I have to perform my IO far earlier,
  // and more intricately bound [with the asynchronous calls] than I'd envisaged :(
    for {
      fut <- resp
      s   =  fut.onComplete {
        case Success(value) => println(parseJson(Source.fromInputStream(value.getEntity.getContent).mkString("")))
        case Failure(exception) => exception.printStackTrace()
      }(scala.concurrent.ExecutionContext.Implicits.global)
    } yield s

  Thread.sleep(10000)
}

object FutureMain2 extends App with JSONMappers with Connection {
  //  lazy val countries = Array(AUSTRALIA,AUSTRIA,BRAZIL,CANADA,CHINA,DENMARK,FRANCE,GERMANY,ISRAEL,ITALY,JAPAN,POLAND,RUSSIA,SOUTH_AFRICA,
  //    UNITED_KINGDOM,UNITED_STATES)
  lazy val countries = Array(UNITED_KINGDOM)

  // so that's working but I need to increase the number of requests I make in parallel. Let's create a fixed thread pool
  // but I really need 2 thread pools blocking & non-blocking (*source JDeGoes...)
  val resp = for {
    country <- countries.map(_.isoCode)
    uri = s"https://covid.ourworldindata.org/data/owid-covid-data.json"
    fut = Future(unsafeResponse(uri))(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(32)))
  } yield fut

  for {
    fut <- resp
    s = fut.onComplete {
      case Success(value) => println(parseJson(Source.fromInputStream(value.getEntity.getContent).mkString("")))
      case Failure(exception) => exception.printStackTrace()
    }(scala.concurrent.ExecutionContext.Implicits.global)
  } yield s

  Thread.sleep(10000)
}


object FutureMain3 extends App with JSONMappers with Connection {

  //  lazy val countries = Array(AUSTRALIA,AUSTRIA,BRAZIL,CANADA,CHINA,DENMARK,FRANCE,GERMANY,ISRAEL,ITALY,JAPAN,POLAND,RUSSIA,SOUTH_AFRICA,
  //    UNITED_KINGDOM,UNITED_STATES)
  lazy val countries = Array(UNITED_KINGDOM)

  // collecting all the results together blows the heap - need to stream the data
  val resp3 = (for {
    country <- countries.map(_.isoCode)
    res     =  unsafeResponse(s"https://covid.ourworldindata.org/data/owid-covid-data.json")
  } yield res).to(LazyList)

  (for {
    stream <- resp3
    res    =  Source.fromInputStream(stream.getEntity.getContent).mkString("")
  } yield res).foreach { str =>
    val v = parseJson(str)
    println(v.mkString("\n"))
  }

  // that's good but the requests need to be parallelised... but no direct parallel implementation in scala api for LazyList
  // the rabbit hole is starting to get deeper and deeper.......

}
