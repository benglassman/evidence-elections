import play.Configuration
import controllers.{Application, Callback, User}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import router.Routes
import play.api.routing.Router
import com.softwaremill.macwire._
import _root_.controllers.AssetsComponents
import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props

import scala.concurrent.Future
import services.VideoService
import filters.StatsFilter
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import scalikejdbc.config.DBs
import play.api.cache.ehcache.EhCacheComponents
import play.api.cache.SyncCacheApi


class AppApplicationLoader extends ApplicationLoader { def load(context: Context) = {
  LoggerConfigurator(context.environment.classLoader).foreach { cfg => cfg.configure(context.environment)
  }
  new AppComponents(context).application }
}

class AppComponents(context: Context) extends
  BuiltInComponentsFromContext(context) with AhcWSComponents with EvolutionsComponents
  with DBComponents with HikariCPComponents with EhCacheComponents with AssetsComponents {

  override lazy val controllerComponents = wire[DefaultControllerComponents]

  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = new Application(controllerComponents, actorSystem, videoService, defaultCacheApi.sync)
  lazy val callBackController = new Callback(controllerComponents, defaultCacheApi.sync, wsClient, configuration)
  lazy val userController = new User(controllerComponents, defaultCacheApi.sync)
  override lazy val dynamicEvolutions = new DynamicEvolutions
  lazy val videoService = new VideoService()

  applicationLifecycle.addStopHook { () =>
    Logger.info("The app is about to stop")
    DBs.closeAll()
    Future.successful(Unit)
  }

  val onStart = {
    Logger.info("The app is about to start")
    applicationEvolutions
    DBs.setupAll()
    statsActor ! Ping
  }

  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)

  lazy val statsActor = actorSystem.actorOf(
    Props(wire[StatsActor]), StatsActor.name)

}

