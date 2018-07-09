package controllers

// app/controllers/Callback.scala

package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Inject
import play.api.cache
import play.api.cache._
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.ws._
import play.api.mvc.{AbstractController, Action,ControllerComponents}
import helpers.Auth0Config

class Callback (components: ControllerComponents, cache:SyncCacheApi, ws: WSClient) extends AbstractController(components) {

  def callback(codeOpt: Option[String] = None, stateOpt: Option[String] = None) = Action.async {
    if (stateOpt == cache.get("state")) {
      (for {
        code <- codeOpt
      } yield {
        getToken(code).flatMap { case (idToken, accessToken) =>
          getUser(accessToken).map { user =>
            cache.set(idToken + "profile", user)
            Redirect(routes.User2.index())
              .withSession(
                "idToken" -> idToken,
                "accessToken" -> accessToken
              )
          }

        }.recover {
          case ex: IllegalStateException => Unauthorized(ex.getMessage)
        }
      }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
    } else {
      Future.successful(BadRequest("Invalid state parameter"))
    }
  }

  def getToken(code: String): Future[(String, String)] = {
    val config = Auth0Config.get()
    var audience = config.audience
    if (config.audience == ""){
      audience = String.format("https://%s/userinfo",config.domain)
    }
    val tokenResponse = ws.url(String.format("https://%s/oauth/token", config.domain)).
      withHttpHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(
        Json.obj(
          "client_id" -> config.clientId,
          "client_secret" -> config.secret,
          "redirect_uri" -> config.callbackURL,
          "code" -> code,
          "grant_type"-> "authorization_code",
          "audience" -> audience
        )
      )

    tokenResponse.flatMap { response =>
      (for {
        idToken <- (response.json \ "id_token").asOpt[String]
        accessToken <- (response.json \ "access_token").asOpt[String]
      } yield {
        Future.successful((idToken, accessToken))
      }).getOrElse(Future.failed[(String, String)](new IllegalStateException("Tokens not sent")))
    }

  }

  def getUser(accessToken: String): Future[JsValue] = {
    val config = Auth0Config.get()
    val userResponse = ws.url(String.format("https://%s/userinfo", config.domain))
      .withQueryStringParameters("access_token" -> accessToken)
      .get()

    userResponse.flatMap(response => Future.successful(response.json))
  }
}