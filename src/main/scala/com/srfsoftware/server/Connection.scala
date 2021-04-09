package com.srfsoftware.server

import com.srfsoftware.json.JSONMappers
import com.typesafe.config.ConfigFactory
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.entity.{ContentType, InputStreamEntity}
import org.apache.http.impl.client.HttpClients
import zio.Managed
import zio.blocking.effectBlocking

import java.io.{ByteArrayInputStream, IOException}

trait Connection extends JSONMappers {

  lazy val config = ConfigFactory.load()

  def post[A](uri: String, postValue: A) = {
    val p = new HttpPost(uri)
    p.setHeader("Accept", "application/x-jackson-smile, application/json")
    p.setEntity(new InputStreamEntity(new ByteArrayInputStream(requestBytes(postValue)), ContentType.APPLICATION_JSON))
    p
  }

  def get(uri: String) = {
    val g = new HttpGet(uri)
    g.setHeader("Accept", "application/x-jackson-smile, application/json")
    g
  }

  private def requestBytes[A](a: A): Array[Byte] = covidJsonMapper.writeValueAsBytes(a)

  def client = HttpClients.createDefault()

  def unsafeResponse(uri: String) = client.execute(get(uri))

  def openUri(uri: String) = {
    val acquire = effectBlocking(unsafeResponse(uri)).refineToOrDie[IOException]
    val release = (request: CloseableHttpResponse) => effectBlocking(request.close()).orDie
    Managed.make(acquire)(release)
  }

}
