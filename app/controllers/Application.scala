package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import services.UserAuthAction
import akka.util.Timeout
import akka.pattern.ask
import akka.actor.ActorSystem
import model.Race
import model.User
import services.AuthService
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import scalikejdbc._

import scala.concurrent.ExecutionContext.Implicits.global

case class UserLoginData(username: String, password: String)

class Application(components: ControllerComponents,
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
    val races: List[Race] = DB.readOnly { implicit session =>
      val race = sql"""
                      select race.raceid, race.raceType, state.statename, race.candidate1id, c1.name as c1name, c1.party as c1party, race.candidate2id, c2.name as c2name, c2.party as c2party
                      from elections.race
                      left join elections.state
                      on race.state = state.stateid
                      left join elections.candidates c1
                      on race.candidate1id = c1.candidateid
                      left join elections.candidates c2
                      on race.candidate2id=c2.candidateid
                      order by race.raceid desc
                      limit 10
        """
        .map(Race.fromRS).list().apply()
      race
    }
    println("races: " + races)
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val requestsF = (actorSystem.actorSelection(StatsActor.path) ?
      StatsActor.GetStats).mapTo[Int]
    for{
      requests <- requestsF
    } yield {
      Ok(views.html.index(races, requests)) }
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

  def doSignup = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.login(Some("Wrong data"))),
      userData => {
        val maybeCookie = authService.signUp(
          userData.username, userData.password)
        maybeCookie match {
          case Some(cookie) =>
            Redirect("/").withCookies(cookie)
          case None =>
            Ok(views.html.login(Some("Signup failed"))) }
      }
    )
  }

  def signup = Action {
    Ok(views.html.login(None))
  }

  def restricted = userAuthAction { userAuthRequest =>
    Ok(views.html.restricted(userAuthRequest.user))
  }

  def raceView(id: Long) = Action {
    DB.readOnly { implicit session =>
      val maybeRace = sql"""
                      select race.raceid, race.raceType, state.statename, race.candidate1id, c1.name as c1name, c1.party as c1party, race.candidate2id, c2.name as c2name, c2.party as c2party
                      from elections.race
                      left join elections.state
                      on race.state = state.stateid
                      left join elections.candidates c1
                      on race.candidate1id = c1.candidateid
                      left join elections.candidates c2
                      on race.candidate2id=c2.candidateid
                      where race.raceid= $id
        """
        .map(Race.fromRS).single().apply()
      maybeRace match {
        case Some(race) =>
          Ok(views.html.race(id,race,None))
        case None =>
          Ok(views.html.login(Some("Race not found!!")))
      }
    }
  }
}
