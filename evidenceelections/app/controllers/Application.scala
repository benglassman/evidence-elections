package controllers

import services.SunService
import services.WeatherService

import play.api._
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global

class Application(components: ControllerComponents, sunService: SunService, weatherService: WeatherService)
extends AbstractController(components) {

  def index = Action.async {
    val lat = -33.8830
    val lon = 151.2167
    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)
    for{
      sunInfo <- sunInfoF
      temperature <- temperatureF } yield {
      Ok(views.html.index(sunInfo, temperature)) }
  }
}
