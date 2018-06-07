package controllers

import model.SunInfo

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.http.HttpEntity

import javax.inject._
import java.util.Date
import java.text.SimpleDateFormat
import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class Application @Inject() (components: ControllerComponents, ws: WSClient)
extends AbstractController(components) {
  def index = Action.async {
    val responseF = ws.url("http://api.sunrise-sunset.org/json?" +
      "lat=-33.8830&lng=151.2167&formatted=0").get()
    val weatherResponseF = ws.url("http://api.openweathermap.org/data/2.5/" +
      s"weather?lat=-33.8830&lon=151.2167&units=metric").get()
    for{
      response <- responseF
    } yield {
      val temperature = 98.7
      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunriseTime = ZonedDateTime.parse(sunriseTimeStr)
      val sunsetTime = ZonedDateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").
      withZone(ZoneId.of("Australia/Sydney"))
      val sunInfo = SunInfo(sunriseTime.format(formatter),
      sunsetTime.format(formatter))
      Ok(views.html.index(sunInfo, temperature))
    }

  }
}
