package controllers

import java.math.BigInteger
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

import actors.StatsActor
import services.UserAuthAction
import services.VideoService
import akka.util.Timeout
import akka.pattern.ask
import akka.actor.ActorSystem
import helpers.Auth0Config
import model.Race
import model.User
import play.api.cache
import play.api.cache.SyncCacheApi
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
                  videoService: VideoService,
                  userAuthAction: UserAuthAction,
                  cache: SyncCacheApi)
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
                      order by race.raceid asc
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

//  def login = Action {
//    Ok(views.html.login(None))
//  }

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
      val ids: List[String] = videoService.getIdsByRace(id)
      maybeRace match {
        case Some(race) =>
          Ok(views.html.race(id,race,ids,None))
        case None =>
          Ok(views.html.login(Some("Race not found!!")))
      }
    }
  }

  def login = Action {
    val config = Auth0Config.get()
    // Generate random state parameter
    object RandomUtil {
      private val random = new SecureRandom()

      def alphanumeric(nrChars: Int = 24): String = {
        new BigInteger(nrChars * 5, random).toString(32)
      }
    }
    val state = RandomUtil.alphanumeric()
    var audience = config.audience
    cache.set("state", state)
    if (config.audience == ""){
      audience = String.format("https://%s/userinfo",config.domain)
    }

    val query = String.format(
      "authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=openid profile&audience=%s&state=%s",
      config.clientId,
      config.callbackURL,
      audience,
      state
    )
    Redirect(String.format("https://%s/%s",config.domain,query))
  }

  def logout = Action {
    val config = Auth0Config.get()
    Redirect(String.format(
      "https://%s/v2/logout?client_id=%s&returnTo=http://localhost:9000",
      config.domain,
      config.clientId)
    ).withNewSession
  }

}
