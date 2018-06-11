package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import services.UserAuthAction

import akka.util.Timeout
import akka.pattern.ask
import akka.actor.ActorSystem
import services.{AuthService, SunService, WeatherService}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.ExecutionContext.Implicits.global

case class UserLoginData(username: String, password: String)

class Application(components: ControllerComponents,
                  sunService: SunService,
                  weatherService: WeatherService,
                  actorSystem: ActorSystem,
                  authService: AuthService,
                  userAuthAction: UserAuthAction)
  extends AbstractController(components) {

  val userDataForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLoginData.apply)(UserLoginData.unapply)
  }

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

  def doLogin = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
    formWithErrors => Ok(views.html.login(Some("Wrong data"))),
      userData => {
      val maybeCookie = authService.login(
        userData.username, userData.password)
      maybeCookie match {
        case Some(cookie) =>
          Redirect("/").withCookies(cookie)
        case None =>
          Ok(views.html.login(Some("Login failed"))) }
      }
    )
  }

  def login = Action {
    Ok(views.html.login(None))
  }

  def restricted = userAuthAction { userAuthRequest =>
    Ok(views.html.restricted(userAuthRequest.user))
  }

}
