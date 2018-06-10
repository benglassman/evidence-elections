package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.util.Timeout
import akka.pattern.ask
import akka.actor.ActorSystem

import services.SunService
import services.WeatherService

import play.api._
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global

class Application(components: ControllerComponents,
                  sunService: SunService,
                  weatherService: WeatherService,
                  actorSystem: ActorSystem)
  extends AbstractController(components) {

  def index = Action.async {
    val lat = -33.8830
    val lon = 151.2167
    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val requestsF = (actorSystem.actorSelection(StatsActor.path) ?
      StatsActor.GetStats).mapTo[Int]
    for{
      sunInfo <- sunInfoF
      temperature <- temperatureF
      requests <- requestsF
    } yield {
      Ok(views.html.index(sunInfo, temperature, requests)) }
  }

  def login = Action {
    Ok(views.html.login())
  }
}
